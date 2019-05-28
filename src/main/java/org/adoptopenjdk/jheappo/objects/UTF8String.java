package org.adoptopenjdk.jheappo.objects;

import org.adoptopenjdk.jheappo.heapdump.EncodedChunk;

public class UTF8String extends HeapObject {

    private String string;

    public UTF8String(EncodedChunk buffer) {
        super(buffer);
        string = new String(buffer.readRemaining());
    }

    public String getString() {
        return string;
    }

    public String toString() { return getId() + " : " + string; }
}
