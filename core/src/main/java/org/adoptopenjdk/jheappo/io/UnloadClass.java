package org.adoptopenjdk.jheappo.io;


public class UnloadClass extends HeapProfileRecord {
    public static final int TAG = 0x03;

     /*
        u4   |  class serial number (always > 0)
     */

    private long classSerialNumber;

    public UnloadClass(EncodedChunk body) {
        classSerialNumber = body.extractU4();
    }

    public String toString() {
        return "Unloaded -> " + classSerialNumber;
    }
}
