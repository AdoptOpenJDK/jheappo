package org.adoptopenjdk.jheappo.io

import org.adoptopenjdk.jheappo.heapdump.EncodedChunk
import org.junit.Test

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue

class HeapDumpSectionTest {

    @Test
    fun testBufferReading() {
        val one = byteArrayOf(0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 1, 10, 10, 10, 10)
        val buffer = EncodedChunk(one)
        assertEquals("", 0, buffer.extractU1().toInt())
        assertEquals("", 0, buffer.extractU2().toInt())
        assertEquals("", 16777216, buffer.extractU4())
        assertEquals("", 72340172989663754L, buffer.extractU8())
        assertTrue(buffer.endOfBuffer())
    }

}
