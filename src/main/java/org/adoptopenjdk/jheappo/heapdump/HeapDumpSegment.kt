package org.adoptopenjdk.jheappo.heapdump

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */


import org.adoptopenjdk.jheappo.io.HeapProfileRecord
import org.adoptopenjdk.jheappo.objects.*

class HeapDumpSegment(private val body: EncodedChunk) : HeapProfileRecord() {

    /*
    ROOT UNKNOWN            | 0xFF  | ID      | object ID

    ROOT JNI GLOBAL         | 0x01  | ID      | object ID
                                    | ID      | JNI global ref ID

    ROOT JNI LOCAL          | 0x02  | ID      | object ID
                                    | u4      | thread serial number
                                    | u4      | frame number in stack trace (-1 for empty)

    ROOT JAVA FRAME         | 0x03  | ID      | object ID
                                    | u4      | thread serial number
                                    | u4      | frame number in stack trace (-1 for empty)

    ROOT NATIVE STACK       | 0x04  | ID      | object ID
                                    | u4      | thread serial number

    ROOT STICKY CLASS       | 0x05  | ID      | object ID

    ROOT THREAD BLOCK       | 0x06  | ID      | object ID
                                    | u4      | thread serial number

    ROOT MONITOR USED       | 0x07  | ID      | object ID

    ROOT THREAD OBJECT      | 0x08  | ID      | thread object ID
                                    | u4      | thread serial number
                                    | u4      | stack trace serial number

    CLASS DUMP              | 0x20  | ID      | class object ID
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


    INSTANCE DUMP           | 0x21  | ID      | object ID
                                    | u4      | stack trace serial number
                                    | ID      | class object ID
                                    | u4      | number of bytes that follow
                                    |[value]* | instance field values (this class, followed by super class, etc)

    OBJECT ARRAY DUMP       | 0x22  | ID    | array object ID
                                    | u4    | stack trace serial number
                                    | u4    | number of elements
                                    | ID    | array class object ID
                                    | [ID]* | elements

    PRIMITIVE ARRAY DUMP    | 0x23  | ID    | array object ID
                                    | u4    | stack trace serial number
                                    | u4    | number of elements
                                    | u1    | element type (See Basic Type)
                                    | [u1]* | elements (packed array)



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

    companion object {
        const val TAG1 = 0x0C
        const val TAG2 = 0x1C
    }

    fun hasNext(): Boolean {
        return body.endOfBuffer()
    }

    fun next(): HeapObject? {
        when (val typeCode = body.extractU1().toInt()) {
            // TODO use unsigned ints
            RootUnknown.TAG -> return RootUnknown(body)
            RootJNIGlobal.TAG -> return RootJNIGlobal(body)
            RootJNILocal.TAG -> return RootJNILocal(body)
            RootJavaFrame.TAG -> return RootJavaFrame(body)
            RootNativeStack.TAG -> return RootNativeStack(body)
            RootStickyClass.TAG -> return RootStickyClass(body)
            RootThreadBlock.TAG -> return RootThreadBlock(body)
            RootMonitorUsed.TAG -> return RootMonitorUsed(body)
            RootThreadObject.TAG -> return RootThreadObject(body)
            ClassObject.TAG -> return ClassObject(body)
            InstanceObject.TAG -> return InstanceObject(body)
            ObjectArray.TAG -> return ObjectArray(body)
            PrimitiveArray.TAG -> return PrimitiveArray(body)
            else -> {
                println(typeCode.toString() + " not recognized... @index=" + body.index)
                return null
            }
        }
    }
}
