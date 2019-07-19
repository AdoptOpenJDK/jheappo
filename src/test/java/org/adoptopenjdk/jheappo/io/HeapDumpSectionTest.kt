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
        assertEquals("", 0U, buffer.extractU1().toUInt())
        assertEquals("", 0U, buffer.extractU2().toUInt())
        assertEquals("", 16777216U, buffer.extractU4())
        assertEquals("", 72340172989663754U, buffer.extractU8())
        assertTrue(buffer.endOfBuffer())
    }

    @Test
    fun testBufferReadingUnsignedWithHighBitSet() {
        // 8 bytes with the high bit set
        val bytes = (1..8).map { Byte.MIN_VALUE }.toByteArray()
        assertEquals(128U, EncodedChunk(bytes).extractU1().toUInt())
        // bits 7 and 15
        assertEquals((0..1).map { 1U shl (it * 8 + 7)}.sum(), EncodedChunk(bytes).extractU2().toUInt())
        // bits 7, 15, 23, 31
        assertEquals((0..3).map { 1U shl (it * 8 + 7)}.sum(), EncodedChunk(bytes).extractU4())
        // bits 7, 15, ... 63
        assertEquals((0..7).map { 1UL shl (it * 8 + 7)}.sum(), EncodedChunk(bytes).extractU8())
    }


}
