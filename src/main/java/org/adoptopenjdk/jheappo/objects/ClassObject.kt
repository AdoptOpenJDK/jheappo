package org.adoptopenjdk.jheappo.objects

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

import org.adoptopenjdk.jheappo.heapdump.EncodedChunk
import org.adoptopenjdk.jheappo.model.BasicDataTypeValue

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
        const val TAG = 0x20
    }

    internal val stackTraceSerialNumber: Int = buffer.extractU4()
    internal val superClassObjectID: Long = buffer.extractID()
    internal val classLoaderObjectID: Long = buffer.extractID()
    internal val signersObjectID: Long = buffer.extractID()
    internal val protectionDomainObjectID: Long = buffer.extractID()
    internal val reserved = arrayOf(buffer.extractID(), buffer.extractID())
    internal val instanceSizeInBytes: Int = buffer.extractU4()

    internal lateinit var staticFieldNameIndicies: LongArray
    internal lateinit var staticValues: Array<BasicDataTypeValue>

    internal lateinit var fieldNamesIndicies: LongArray
    internal lateinit var fieldTypes: IntArray

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
