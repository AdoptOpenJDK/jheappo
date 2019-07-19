package org.adoptopenjdk.jheappo.objects

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

import org.adoptopenjdk.jheappo.heapdump.EncodedChunk

class RootNativeStack(buffer: EncodedChunk) : HeapObject(buffer) {

    companion object {
        const val TAG = 0x04
    }

    private val threadSerialNumber: Int = buffer.extractU4()
}