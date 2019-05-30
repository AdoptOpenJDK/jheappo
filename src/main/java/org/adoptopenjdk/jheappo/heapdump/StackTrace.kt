package org.adoptopenjdk.jheappo.heapdump

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */


import org.adoptopenjdk.jheappo.io.HeapProfileRecord

class StackTrace(body: EncodedChunk) : HeapProfileRecord() {

    /*
        u4    | stack trace serial number
        u4    | thread serial number
        u4    | number of frames
        [ID]* | series of stack frame ID's
     */

    companion object {
        const val TAG = 0x05
    }

    internal val stackTraceSerialNumber: Int = body.extractU4()
    internal val threadSerialNumber: Int = body.extractU4()
    internal val numberOfFrames: Int = body.extractU4()

    internal val stackFrameIDs: LongArray

    init {
        stackFrameIDs = LongArray(numberOfFrames)
        for (i in 0 until numberOfFrames) {
            stackFrameIDs[i] = body.extractID()
        }
    }

    override fun toString(): String {
        return "StackTrace --> $stackTraceSerialNumber:$threadSerialNumber:$numberOfFrames"
    }
}
