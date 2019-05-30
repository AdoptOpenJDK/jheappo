package org.adoptopenjdk.jheappo.heapdump

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */


import org.adoptopenjdk.jheappo.io.HeapProfileRecord

class LoadClass(body: EncodedChunk) : HeapProfileRecord() {

    /*
        u4   |  class serial number (always > 0)
        ID   |  class object ID
        u4   |  stack trace serial number
        ID   |  class name string ID
     */

    companion object {
        const val TAG = 0x02
    }

    val classSerialNumber: Long = body.extractU4().toLong()
    val classObjectID: Long = body.extractID()
    val stackTraceSerialNumber: Long = body.extractU4().toLong()
    val classNameStringID: Long = body.extractID()

    override fun toString(): String {
        return "Loaded -> $classSerialNumber:$classObjectID:$stackTraceSerialNumber:$classNameStringID"
    }
}
