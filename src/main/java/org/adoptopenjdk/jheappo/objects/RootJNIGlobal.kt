package org.adoptopenjdk.jheappo.objects

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

import org.adoptopenjdk.jheappo.heapdump.EncodedChunk

/*
    0x01  | ID      | object ID
          | ID      | JNI global ref ID
 */
class RootJNIGlobal(buffer: EncodedChunk) : HeapObject(buffer) {

    companion object {
        const val TAG: UByte = 0x01U
    }

    val jniGlobalRefID: Long = buffer.extractID()

    override fun toString(): String {
        return "RootJNIGlobal : $id:$jniGlobalRefID"
    }
}
