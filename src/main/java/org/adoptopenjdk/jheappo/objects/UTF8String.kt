package org.adoptopenjdk.jheappo.objects

import org.adoptopenjdk.jheappo.heapdump.EncodedChunk

class UTF8String(buffer: EncodedChunk) : HeapObject(buffer) {

    val string: String = String(buffer.readRemaining())

    override fun toString(): String {
        return "$id : $string"
    }
}
