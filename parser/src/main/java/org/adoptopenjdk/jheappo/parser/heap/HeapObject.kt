package org.adoptopenjdk.jheappo.parser.heap

import org.adoptopenjdk.jheappo.parser.BasicDataTypeValue
import org.adoptopenjdk.jheappo.parser.EncodedChunk
import org.adoptopenjdk.jheappo.parser.FieldType
import org.adoptopenjdk.jheappo.parser.Id
import org.adoptopenjdk.jheappo.parser.ObjectValue
import org.slf4j.Logger
import org.slf4j.LoggerFactory

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
        private val logger: Logger = LoggerFactory.getLogger(ClassMetadata::class.java)
        const val TAG: UByte = 0x20U
    }

    internal val stackTraceSerialNumber: UInt = buffer.extractU4()
    val superClassObjectID: Id = buffer.extractID()
    internal val classLoaderObjectID: Id = buffer.extractID()
    internal val signersObjectID: Id = buffer.extractID()
    internal val protectionDomainObjectID: Id = buffer.extractID()
    internal val reserved = arrayOf(buffer.extractID(), buffer.extractID())
    val instanceSizeInBytes: UInt = buffer.extractU4()

    internal val staticFields: List<FieldWithValueMetadata>
    val instanceFields: List<FieldWithTypeMetadata>

    init {
        extractConstantPool(buffer)
        staticFields = extractStaticFields(buffer)
        instanceFields = extractInstanceFields(buffer)
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
            logger.debug("Constant Pool: $constantPoolIndex:$value")
        }
    }

    /*
        | u2  | Number of static fields:
              | ID    | static field name string ID
              | u1    | type of field: (See Basic Type)
              | value | value of entry (u1, u2, u4, or u8 based on type of field)
     */
    private fun extractStaticFields(buffer: EncodedChunk): List<FieldWithValueMetadata> {
        val numberOfRecords = buffer.extractU2().toInt()
        // create correct sized list filled with nulls to avoid resize allocation
        val fields = MutableList<FieldWithValueMetadata?>(numberOfRecords) { null }
        for (i in 0 until numberOfRecords) {
            val id = buffer.extractID()
            val value = buffer.extractBasicType(FieldType.fromInt(buffer.extractU1()))
            fields[i] = FieldWithValueMetadata(id, value)
        }

        return fields.requireNoNulls().toList()
    }

    /*
        | u2  | Number of instance fields (not including super class's)
              | ID    | field name string ID
              | u1    | type of field: (See Basic Type)
     */
    private fun extractInstanceFields(buffer: EncodedChunk): List<FieldWithTypeMetadata> {
        val numberOfInstanceFields = buffer.extractU2().toInt()
        val fields = MutableList<FieldWithTypeMetadata?>(numberOfInstanceFields) { null }
        for (i in 0 until numberOfInstanceFields) {
            val id = buffer.extractID()
            if (id.id < 1u) {
                logger.warn("field name invalid id: $id")
            }
            val type = (buffer.extractU1().let { FieldType.fromInt(it) })
            fields[i] = FieldWithTypeMetadata(id, type)
        }
        return fields.requireNoNulls().toList()
    }

    override fun toString(): String {
        var fields = ", Fields --> "
        for (fm in instanceFields) {
            fields += ", ${fm.nameId}"
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
            // TODO handle superclass fields
            instanceFieldValues = classMetadata.instanceFields
                    .map { b.extractBasicType(it.type) }
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

    internal val elements: PrimitiveArrayWrapper

    init {
        // TODO decide what to do about unknown records
        val dataType = FieldType.fromInt(elementType)
        if (dataType == FieldType.UNKNOWN) {
            throw IllegalArgumentException("Unknown data type : $elementType")
        }
        signature = dataType.mnemonic
        elements = buffer.extractPrimitiveArray(dataType, size)
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

class FieldWithValueMetadata(val nameId: Id, val value: BasicDataTypeValue)
class FieldWithTypeMetadata(val nameId: Id, val type: FieldType)
