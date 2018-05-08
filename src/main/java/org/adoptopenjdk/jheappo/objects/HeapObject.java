package org.adoptopenjdk.jheappo.objects;

import org.adoptopenjdk.jheappo.io.HeapProfileRecord;

public class HeapObject {

    private long id;

    public HeapObject() {
        this.id = -1;
    }

    public HeapObject(HeapProfileRecord buffer) {
        id = buffer.extractID();
    }

    public long getId() {
        return id;
    }
}
