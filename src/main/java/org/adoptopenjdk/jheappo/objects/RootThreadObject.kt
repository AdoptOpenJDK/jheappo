package org.adoptopenjdk.jheappo.objects

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

/*
    0x08  | ID      | thread object ID
          | u4      | thread serial number
          | u4      | stack trace serial number
 */

import org.adoptopenjdk.jheappo.heapdump.EncodedChunk

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
