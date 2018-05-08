package org.adoptopenjdk.jheappo.heapdump;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */


import org.adoptopenjdk.jheappo.io.HeapProfileRecord;

public class UnloadClass extends HeapProfileRecord {

    public final static int TAG = 0x03;

    /*
        u4   |  class serial number (always > 0)
     */

    long classSerialNumber;

    public UnloadClass(byte[] body) {
        super(body);
        classSerialNumber = super.extractU4();
    }

    public String toString() {
        return "Unloaded -> " + classSerialNumber;
    }
}
