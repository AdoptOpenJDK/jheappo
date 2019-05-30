package org.adoptopenjdk.jheappo.heapdump

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */


import org.adoptopenjdk.jheappo.io.HeapProfileRecord

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
        const val TAG = 0x04
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
