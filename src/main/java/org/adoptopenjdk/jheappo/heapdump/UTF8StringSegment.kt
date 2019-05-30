package org.adoptopenjdk.jheappo.heapdump

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

import org.adoptopenjdk.jheappo.io.HeapProfileRecord
import org.adoptopenjdk.jheappo.objects.UTF8String

class UTF8StringSegment(private val body: EncodedChunk) : HeapProfileRecord() {

    /*
        ID     | ID for this string
        [u1]*  | UTF8 characters for string (NOT NULL terminated)
     */

    companion object {
        const val TAG = 0x01
    }

    fun toUtf8String(): UTF8String {
        // defer UTF8 parsing
        return UTF8String(body.copy())
    }
}
