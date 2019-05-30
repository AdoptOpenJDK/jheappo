package org.adoptopenjdk.jheappo.objects

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

import org.adoptopenjdk.jheappo.heapdump.EncodedChunk

class RootJavaFrame(buffer: EncodedChunk) : HeapObject(buffer) {

    companion object {
        const val TAG = 0x03
    }

    private val threadSerialNumber: Int = buffer.extractU4()

    // -1 if empty
    private val frameNumberInStackTrace: Int = buffer.extractU4()
}
