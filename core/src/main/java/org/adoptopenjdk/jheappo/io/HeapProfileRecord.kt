package org.adoptopenjdk.jheappo.io

import org.adoptopenjdk.jheappo.objects.ClassObject
import org.adoptopenjdk.jheappo.objects.HeapObject
import org.adoptopenjdk.jheappo.objects.InstanceObject
import org.adoptopenjdk.jheappo.objects.ObjectArray
import org.adoptopenjdk.jheappo.objects.PrimitiveArray
import org.adoptopenjdk.jheappo.objects.RootJNIGlobal
import org.adoptopenjdk.jheappo.objects.RootJNILocal
import org.adoptopenjdk.jheappo.objects.RootJavaFrame
import org.adoptopenjdk.jheappo.objects.RootMonitorUsed
import org.adoptopenjdk.jheappo.objects.RootNativeStack
import org.adoptopenjdk.jheappo.objects.RootStickyClass
import org.adoptopenjdk.jheappo.objects.RootThreadBlock
import org.adoptopenjdk.jheappo.objects.RootThreadObject
import org.adoptopenjdk.jheappo.objects.RootUnknown
import org.adoptopenjdk.jheappo.objects.UTF8String

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

sealed class HeapProfileRecord

class AllocSites(body: EncodedChunk) : HeapProfileRecord() {
    companion object {
        const val TAG: UByte = 0x06U
    }
}

class ControlSettings(body: EncodedChunk) : HeapProfileRecord() {
    companion object {
        const val TAG: UByte = 0x0EU
    }
}

class CPUSamples(body: EncodedChunk) : HeapProfileRecord() {
    companion object {
        const val TAG: UByte = 0x0DU
    }
}

class EndThread(body: EncodedChunk) : HeapProfileRecord() {
    companion object {
        const val TAG: UByte = 0x0BU
    }
}

class HeapDumpEnd(body: EncodedChunk) : HeapProfileRecord() {
    companion object {
        const val TAG: UByte = 0x2CU
    }
}

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
        const val TAG1: UByte = 0x0CU
        const val TAG2: UByte = 0x1CU
    }

    fun hasNext(): Boolean {
        return body.endOfBuffer()
    }

    fun next(): HeapObject? {
        when (val typeCode = body.extractU1()) {
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

class HeapSummary(body: EncodedChunk) : HeapProfileRecord() {
    companion object {
        const val TAG: UByte = 0x07U
    }
}

class LoadClass(body: EncodedChunk) : HeapProfileRecord() {

    /*
        u4   |  class serial number (always > 0)
        ID   |  class object ID
        u4   |  stack trace serial number
        ID   |  class name string ID
     */

    companion object {
        const val TAG: UByte = 0x02U
    }

    val classSerialNumber: Long = body.extractU4().toLong()
    val classObjectID: Long = body.extractID()
    val stackTraceSerialNumber: Long = body.extractU4().toLong()
    val classNameStringID: Long = body.extractID()

    override fun toString(): String {
        return "Loaded -> $classSerialNumber:$classObjectID:$stackTraceSerialNumber:$classNameStringID"
    }
}

/*
    ID    | stack frame ID
    ID    | method name string ID
    ID    | method signature string ID
    ID    | source file name string ID
    u4    | class serial number
    u4    | > 0  | line number
          |   0  | no line information available
          |  -1  | unknown location
          |  -2  | compiled method (Not implemented)
          |  -3  | native method (Not implemented)
 */

class StackFrame(body: EncodedChunk) : HeapProfileRecord() {
    companion object {
        const val TAG: UByte = 0x04U
    }

    internal val stackFrameID: Long = body.extractID()
    internal val methodNameStringID: Long = body.extractID()
    internal val methodSignatureStringID: Long = body.extractID()
    internal val sourceFileNameStringID: Long = body.extractID()

    internal val classSerialNumber: Long = body.extractID()

    override fun toString(): String {
        return "StackFrame --> $stackFrameID:$methodNameStringID:$methodSignatureStringID:$sourceFileNameStringID:$classSerialNumber"
    }
}

class StackTrace(body: EncodedChunk) : HeapProfileRecord() {

    /*
        u4    | stack trace serial number
        u4    | thread serial number
        u4    | number of frames
        [ID]* | series of stack frame ID's
     */

    companion object {
        const val TAG: UByte = 0x05U
    }

    internal val stackTraceSerialNumber: UInt = body.extractU4()
    internal val threadSerialNumber: UInt = body.extractU4()
    internal val numberOfFrames: UInt = body.extractU4()

    internal val stackFrameIDs: LongArray

    init {
        stackFrameIDs = LongArray(numberOfFrames.toInt())
        for (i in 0 until numberOfFrames.toInt()) {
            stackFrameIDs[i] = body.extractID()
        }
    }

    override fun toString(): String {
        return "StackTrace --> $stackTraceSerialNumber:$threadSerialNumber:$numberOfFrames"
    }
}

class StartThread(body: EncodedChunk) : HeapProfileRecord() {
    companion object {
        const val TAG: UByte = 0x0AU
    }
}

class UnloadClass(body: EncodedChunk) : HeapProfileRecord() {

    /*
        u4   |  class serial number (always > 0)
     */

    companion object {
        const val TAG: UByte = 0x03U
    }

    internal val classSerialNumber: Long = body.extractU4().toLong()

    override fun toString(): String {
        return "Unloaded -> $classSerialNumber"
    }
}

class UTF8StringSegment(private val body: EncodedChunk) : HeapProfileRecord() {

    /*
        ID     | ID for this string
        [u1]*  | UTF8 characters for string (NOT NULL terminated)
     */

    companion object {
        const val TAG: UByte = 0x01U
    }

    fun toUtf8String(): UTF8String {
        // defer UTF8 parsing
        return UTF8String(body.copy())
    }
}
