package org.adoptopenjdk.jheappo.objects;

import org.adoptopenjdk.jheappo.io.HeapProfileRecord;

public class UTF8String extends HeapObject {

    private String string;

    public UTF8String(HeapProfileRecord buffer) {
        super(buffer);
        string = new String(buffer.readRemaining());
    }

    public String getString() {
        return string;
    }

    public String toString() { return getId() + " : " + string; }
}
