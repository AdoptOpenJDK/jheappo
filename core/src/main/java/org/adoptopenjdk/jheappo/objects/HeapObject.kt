package org.adoptopenjdk.jheappo.objects

import org.adoptopenjdk.jheappo.io.EncodedChunk
import org.adoptopenjdk.jheappo.model.BasicDataTypeValue
import org.adoptopenjdk.jheappo.model.ObjectValue

sealed class HeapObject(buffer: EncodedChunk) {
    val id: Long = buffer.extractID()
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

class ClassObject(buffer: EncodedChunk) : HeapObject(buffer) {
    companion object {
        const val TAG: UByte = 0x20U
    }

    internal val stackTraceSerialNumber: UInt = buffer.extractU4()
    val superClassObjectID: Long = buffer.extractID()
    internal val classLoaderObjectID: Long = buffer.extractID()
    internal val signersObjectID: Long = buffer.extractID()
    internal val protectionDomainObjectID: Long = buffer.extractID()
    internal val reserved = arrayOf(buffer.extractID(), buffer.extractID())
    val instanceSizeInBytes: UInt = buffer.extractU4()

    internal lateinit var staticFieldNameIndicies: LongArray
    internal lateinit var staticValues: Array<BasicDataTypeValue>

    lateinit var fieldNamesIndicies: LongArray
    lateinit var fieldTypes: IntArray

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
            val value = buffer.extractBasicType(buffer.extractU1().toInt())
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
        staticFieldNameIndicies = LongArray(numberOfRecords)
        val values = ArrayList<BasicDataTypeValue>(numberOfRecords)

        for (i in 0 until numberOfRecords) {
            staticFieldNameIndicies[i] = buffer.extractID()
            values.add(buffer.extractBasicType(buffer.extractU1().toInt()))
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
        if (numberOfInstanceFields > -1) {
            fieldNamesIndicies = LongArray(numberOfInstanceFields)
            fieldTypes = IntArray(numberOfInstanceFields)
        }
        for (i in 0 until numberOfInstanceFields) {
            fieldNamesIndicies[i] = buffer.extractID()
            if (fieldNamesIndicies[i] < 1)
                println("field name invalid id: " + fieldNamesIndicies[i])
            fieldTypes[i] = buffer.extractU1().toInt()
        }
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
class InstanceObject(buffer: EncodedChunk) : HeapObject(buffer) {

    companion object {
        const val TAG: UByte = 0x21U
    }
    val stackTraceSerialNumber: UInt
    val classObjectID: Long
    var instanceFieldValues = arrayOf<BasicDataTypeValue>()

    private var buffer: EncodedChunk? = null

    init {
        this.buffer = buffer
        stackTraceSerialNumber = buffer.extractU4()
        classObjectID = buffer.extractID()
        val bufferLength = buffer.extractU4()
        this.buffer = EncodedChunk(buffer.read(bufferLength.toInt()))
    }

    fun inflate(classObject: ClassObject) {
        val b = buffer ?: return
        if (!b.endOfBuffer()) {
            instanceFieldValues = classObject.fieldTypes
                    .map { b.extractBasicType(it) }
                    .toTypedArray()
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
class ObjectArray(buffer: EncodedChunk) : HeapObject(buffer) {
    companion object {
        const val TAG: UByte = 0x22U
    }

    private val stackTraceSerialNumber: UInt = buffer.extractU4()
    val size: UInt = buffer.extractU4()
    private val elementsObjectID: Long = buffer.extractID()

    private val elements: Array<ObjectValue>

    init {
        elements = (0U until size).map { buffer.extractBasicType(BasicDataTypes.OBJECT) }
                .map { it as ObjectValue }
                .toTypedArray()
    }

    fun getValueObjectIDAt(index: Int): Long {
        return elements[index].objectId
    }
}

class PrimitiveArray(buffer: EncodedChunk) : HeapObject(buffer) {

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
        val dataType = BasicDataTypes.fromInt(elementType.toInt()) ?: BasicDataTypes.UNKNOWN
        if (dataType == BasicDataTypes.UNKNOWN) {
            throw IllegalArgumentException("Unknown data type : $elementType")
        }
        signature = BasicDataTypes.fromInt(elementType.toInt())!!.mnemonic
        elements = (0U until size).map { buffer.extractBasicType(dataType) }.toTypedArray()
    }
}

class RootJavaFrame(buffer: EncodedChunk) : HeapObject(buffer) {

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
class RootJNIGlobal(buffer: EncodedChunk) : HeapObject(buffer) {

    companion object {
        const val TAG: UByte = 0x01U
    }

    val jniGlobalRefID: Long = buffer.extractID()

    override fun toString(): String {
        return "RootJNIGlobal : $id:$jniGlobalRefID"
    }
}

class RootJNILocal(buffer: EncodedChunk) : HeapObject(buffer) {

    companion object {
        const val TAG: UByte = 0x02U
    }

    private val threadSerialNumber: UInt = buffer.extractU4()

    // -1 if empty
    private val frameNumberInStackTrace: UInt = buffer.extractU4()

}

class RootMonitorUsed(buffer: EncodedChunk) : HeapObject(buffer) {
    companion object {
        const val TAG: UByte = 0x07U
    }
}

class RootNativeStack(buffer: EncodedChunk) : HeapObject(buffer) {

    companion object {
        const val TAG: UByte = 0x04U
    }

    private val threadSerialNumber: UInt = buffer.extractU4()
}

class RootStickyClass(buffer: EncodedChunk) : HeapObject(buffer) {
    companion object {
        const val TAG: UByte = 0x05U
    }
}

class RootThreadBlock(buffer: EncodedChunk) : HeapObject(buffer) {

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

class RootThreadObject(buffer: EncodedChunk) : HeapObject(buffer) {

    companion object {
        const val TAG: UByte = 0x08U
    }

    val serialNumber: UInt = buffer.extractU4()

    val traceSerialNumber: UInt = buffer.extractU4()

    override fun toString(): String {
        return "Root Thread Object : $serialNumber : $traceSerialNumber"
    }
}

class RootUnknown(buffer: EncodedChunk) : HeapObject(buffer) {

    companion object {
        const val TAG: UByte = 0xFFU
    }

    override fun toString(): String {
        return "Root Unknown : $id"
    }
}

class UTF8String(buffer: EncodedChunk) : HeapObject(buffer) {

    val string: String = String(buffer.readRemaining())

    override fun toString(): String {
        return "$id : $string"
    }
}
