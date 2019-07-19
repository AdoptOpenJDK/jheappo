package org.adoptopenjdk.jheappo.heapdump

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */


import org.adoptopenjdk.jheappo.io.HeapProfileRecord

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
