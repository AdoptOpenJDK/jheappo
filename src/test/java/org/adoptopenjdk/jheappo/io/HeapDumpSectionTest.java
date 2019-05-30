package org.adoptopenjdk.jheappo.io;

import org.adoptopenjdk.jheappo.heapdump.EncodedChunk;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class HeapDumpSectionTest {

    @Test
    public void testBufferReading() {
        byte[] one = { 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 1, 10, 10, 10, 10 };
        EncodedChunk buffer = new EncodedChunk(one);
        assertEquals("", 0, buffer.extractU1());
        assertEquals("", 0, buffer.extractU2());
        assertEquals("", 16777216, buffer.extractU4());
        assertEquals("", 72340172989663754L, buffer.extractU8());
        assertTrue( buffer.endOfBuffer());
    }

}
