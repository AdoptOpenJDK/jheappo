package org.adoptopenjdk.jheappo.heap

import org.adoptopenjdk.jheappo.io.EncodedChunk
import org.adoptopenjdk.jheappo.io.BasicDataTypeValue
import org.adoptopenjdk.jheappo.io.FieldType
import org.adoptopenjdk.jheappo.io.Id
import org.adoptopenjdk.jheappo.io.ObjectValue

sealed class HeapObject(buffer: EncodedChunk) {
    val id: Id = buffer.extractID()
}

/*
        0x20    | ID      | class object ID
                | u4      | stack trace serial number
                | ID      | super class object ID
                | ID      | class loader object ID
                | ID      | signers object ID
                | ID      | protection domain object ID
                | ID      | reserved
                | ID      | reserved
                | u4      | instance size (in bytes)
                | u2      | size of constant pool and number of records that follow:
                          | u2    | constant pool index
                          | u1    | type of entry: (See Basic Type)
                          | value | value of entry (u1, u2, u4, or u8 based on type of entry)
                | u2      | Number of static fields:
                          | ID    | static field name string ID
                          | u1    | type of field: (See Basic Type)
                          | value | value of entry (u1, u2, u4, or u8 based on type of field)
                | u2      | Number of instance fields (not including super class's)
                          | ID    | field name string ID
                          | u1    | type of field: (See Basic Type)

        Basic Types
         2  | object
         4  | boolean
         5  | char
         6  | float
         7  | double
         8  | byte
         9  | short
        10  | int
        11  | long
 */

class ClassMetadata internal constructor(buffer: EncodedChunk) : HeapObject(buffer) {
    companion object {
        const val TAG: UByte = 0x20U
    }

    internal val stackTraceSerialNumber: UInt = buffer.extractU4()
    val superClassObjectID: Id = buffer.extractID()
    internal val classLoaderObjectID: Id = buffer.extractID()
    internal val signersObjectID: Id = buffer.extractID()
    internal val protectionDomainObjectID: Id = buffer.extractID()
    internal val reserved = arrayOf(buffer.extractID(), buffer.extractID())
    val instanceSizeInBytes: UInt = buffer.extractU4()

    internal lateinit var staticFieldNameIndicies: Array<Id>
    internal lateinit var staticValues: Array<BasicDataTypeValue>

    lateinit var fieldNamesIndicies: Array<Id>
    lateinit var fieldTypes: List<FieldType>

    init {
        extractConstantPool(buffer)
        extractStaticFields(buffer)
        extractInstanceFields(buffer)
    }

    /*
        | u2      | size of constant pool and number of records that follow:
                  | u2    | constant pool index
                  | u1    | type of entry: (See Basic Type)
                  | value | value of entry (u1, u2, u4, or u8 based on type of entry)
     */
    private fun extractConstantPool(buffer: EncodedChunk) {
        val numberOfRecords = buffer.extractU2().toInt()
        for (i in 0 until numberOfRecords) {
            val constantPoolIndex = buffer.extractU2().toInt()
            val value = buffer.extractBasicType(FieldType.fromInt(buffer.extractU1()))
            println("Constant Pool: $constantPoolIndex:$value")
        }
    }

    /*
        | u2  | Number of static fields:
              | ID    | static field name string ID
              | u1    | type of field: (See Basic Type)
              | value | value of entry (u1, u2, u4, or u8 based on type of field)
     */
    private fun extractStaticFields(buffer: EncodedChunk) {
        val numberOfRecords = buffer.extractU2().toInt()
        // TODO don't require an array here
        staticFieldNameIndicies = Array(numberOfRecords) { Id(1u) }
        val values = ArrayList<BasicDataTypeValue>(numberOfRecords)

        for (i in 0 until numberOfRecords) {
            staticFieldNameIndicies[i] = buffer.extractID()
            values.add(buffer.extractBasicType(FieldType.fromInt(buffer.extractU1())))
        }

        staticValues = values.toTypedArray()
    }

    /*
        | u2  | Number of instance fields (not including super class's)
              | ID    | field name string ID
              | u1    | type of field: (See Basic Type)
     */
    private fun extractInstanceFields(buffer: EncodedChunk) {
        val numberOfInstanceFields = buffer.extractU2().toInt()
        val types = mutableListOf<FieldType>()
        fieldNamesIndicies = Array(numberOfInstanceFields) { Id(1u) }
        for (i in 0 until numberOfInstanceFields) {
            fieldNamesIndicies[i] = buffer.extractID()
            if (fieldNamesIndicies[i].id < 1u) {
                println("field name invalid id: " + fieldNamesIndicies[i])
            }
            types.add(buffer.extractU1().let { FieldType.fromInt(it) })
        }
        fieldTypes = types.toList()
    }

    override fun toString(): String {
        var fields = ", Fields --> "
        for (i in fieldNamesIndicies.indices) {
            fields += ", " + fieldNamesIndicies[i]
        }

        return "Class Object -->" + id +
                ", stackTraceSerialNumber -->" + stackTraceSerialNumber +
                ", superClassObjectID -->" + superClassObjectID +
                ", classLoaderObjectID -->" + classLoaderObjectID +
                ", signersObjectID -->" + signersObjectID +
                ", protectionDomainObjectID -->" + protectionDomainObjectID +
                fields
    }
}

/*
ID object ID
u4 stack trace serial number
ID class object ID
u4 number of bytes that follow
[value]*  instance field values (this class, followed by super class, etc)
 */
class InstanceObject internal constructor(buffer: EncodedChunk) : HeapObject(buffer) {

    companion object {
        const val TAG: UByte = 0x21U
    }

    val stackTraceSerialNumber: UInt
    val classObjectID: Id
    var instanceFieldValues = listOf<BasicDataTypeValue>()

    private var buffer: EncodedChunk? = null

    init {
        this.buffer = buffer
        stackTraceSerialNumber = buffer.extractU4()
        classObjectID = buffer.extractID()
        val bufferLength = buffer.extractU4()
        this.buffer = EncodedChunk(buffer.read(bufferLength.toInt()))
    }

    fun inflate(classMetadata: ClassMetadata) {
        val b = buffer ?: return
        if (!b.endOfBuffer()) {
            instanceFieldValues = classMetadata.fieldTypes
                    .map { b.extractBasicType(it) }
        }
        buffer = null
    }

    override fun toString(): String {
        var prefix = "InstanceObject->$classObjectID"
        if (instanceFieldValues.size > 0)
            prefix += " fields --> "
        for (i in instanceFieldValues.indices) {
            prefix += instanceFieldValues[i].toString() + ", "
        }
        return prefix
    }
}

/*
ID array object ID
u4 stack trace serial number
u4 number of elements
ID array class object ID
[ID]* elements
 */
class ObjectArray internal constructor(buffer: EncodedChunk) : HeapObject(buffer) {
    companion object {
        const val TAG: UByte = 0x22U
    }

    private val stackTraceSerialNumber: UInt = buffer.extractU4()
    val size: UInt = buffer.extractU4()
    private val elementsObjectID: Id = buffer.extractID()

    private val elements: Array<ObjectValue>

    init {
        elements = (0U until size).map { buffer.extractBasicType(FieldType.OBJECT) }
                .map { it as ObjectValue }
                .toTypedArray()
    }

    fun getValueObjectIDAt(index: Int): Id {
        return elements[index].objectId
    }
}

class PrimitiveArray internal constructor(buffer: EncodedChunk) : HeapObject(buffer) {

    /*
        id         array object ID
        u4         stack trace serial number
        u4         number of elements
        u1         element type
        [u1]*      elements
     */
    companion object {
        const val TAG: UByte = 0x23U
    }

    private val stackTraceSerialNumber: UInt = buffer.extractU4()
    private val size: UInt = buffer.extractU4()
    private val elementType: UByte = buffer.extractU1()
    private val signature: Char

    internal val elements: Array<BasicDataTypeValue>

    init {
        // TODO decide what to do about unknown records
        val dataType = FieldType.fromInt(elementType)
        if (dataType == FieldType.UNKNOWN) {
            throw IllegalArgumentException("Unknown data type : $elementType")
        }
        signature = dataType.mnemonic
        elements = (0U until size).map { buffer.extractBasicType(dataType) }.toTypedArray()
    }
}

class RootJavaFrame internal constructor(buffer: EncodedChunk) : HeapObject(buffer) {

    companion object {
        const val TAG: UByte = 0x03U
    }

    private val threadSerialNumber: UInt = buffer.extractU4()

    // -1 if empty
    private val frameNumberInStackTrace: UInt = buffer.extractU4()
}

/*
    0x01  | ID      | object ID
          | ID      | JNI global ref ID
 */
class RootJNIGlobal internal constructor(buffer: EncodedChunk) : HeapObject(buffer) {

    companion object {
        const val TAG: UByte = 0x01U
    }

    val jniGlobalRefID: Id = buffer.extractID()

    override fun toString(): String {
        return "RootJNIGlobal : $id:$jniGlobalRefID"
    }
}

class RootJNILocal internal constructor(buffer: EncodedChunk) : HeapObject(buffer) {

    companion object {
        const val TAG: UByte = 0x02U
    }

    private val threadSerialNumber: UInt = buffer.extractU4()

    // -1 if empty
    private val frameNumberInStackTrace: UInt = buffer.extractU4()

}

class RootMonitorUsed internal constructor(buffer: EncodedChunk) : HeapObject(buffer) {
    companion object {
        const val TAG: UByte = 0x07U
    }
}

class RootNativeStack internal constructor(buffer: EncodedChunk) : HeapObject(buffer) {

    companion object {
        const val TAG: UByte = 0x04U
    }

    private val threadSerialNumber: UInt = buffer.extractU4()
}

class RootStickyClass internal constructor(buffer: EncodedChunk) : HeapObject(buffer) {
    companion object {
        const val TAG: UByte = 0x05U
    }
}

class RootThreadBlock internal constructor(buffer: EncodedChunk) : HeapObject(buffer) {

    companion object {
        const val TAG: UByte = 0x06U
    }

    private val threadSerialNumber: UInt = buffer.extractU4()

    override fun toString(): String {
        return "Root Sticky Class : $id : $threadSerialNumber"
    }
}

/*
    0x08  | ID      | thread object ID
          | u4      | thread serial number
          | u4      | stack trace serial number
 */

class RootThreadObject internal constructor(buffer: EncodedChunk) : HeapObject(buffer) {

    companion object {
        const val TAG: UByte = 0x08U
    }

    val serialNumber: UInt = buffer.extractU4()

    val traceSerialNumber: UInt = buffer.extractU4()

    override fun toString(): String {
        return "Root Thread Object : $serialNumber : $traceSerialNumber"
    }
}

class RootUnknown internal constructor(buffer: EncodedChunk) : HeapObject(buffer) {

    companion object {
        const val TAG: UByte = 0xFFU
    }

    override fun toString(): String {
        return "Root Unknown : $id"
    }
}

class UTF8String internal constructor(buffer: EncodedChunk) : HeapObject(buffer) {

    val string: String = String(buffer.readRemaining())

    override fun toString(): String {
        return "$id : $string"
    }
}
