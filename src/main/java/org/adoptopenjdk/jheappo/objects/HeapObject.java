package org.adoptopenjdk.jheappo.objects;

import org.adoptopenjdk.jheappo.heapdump.EncodedChunk;

public abstract class HeapObject {

    private long id;

    public HeapObject() {
        this.id = -1;
    }

    public HeapObject(EncodedChunk buffer) {
        id = buffer.extractID();
    }

    public long getId() {
        return id;
    }
}
