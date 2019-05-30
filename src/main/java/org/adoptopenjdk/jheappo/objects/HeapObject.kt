package org.adoptopenjdk.jheappo.objects

import org.adoptopenjdk.jheappo.heapdump.EncodedChunk

abstract class HeapObject(buffer: EncodedChunk) {
    val id: Long = buffer.extractID()
}
