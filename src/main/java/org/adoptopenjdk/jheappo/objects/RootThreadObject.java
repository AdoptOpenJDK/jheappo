package org.adoptopenjdk.jheappo.objects;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

/*
    0x08  | ID      | thread object ID
          | u4      | thread serial number
          | u4      | stack trace serial number
 */

import org.adoptopenjdk.jheappo.io.HeapProfileRecord;

public class RootThreadObject extends HeapObject {

    public static final int TAG = 0x08;

    private int serialNumber;
    private int traceSerialNumber;

    public RootThreadObject(HeapProfileRecord buffer) {
        super(buffer);
        serialNumber = buffer.extractU4();
        traceSerialNumber = buffer.extractU4();
    }

    public int getSerialNumber() { return this.serialNumber; }
    public int getTraceSerialNumber() { return this.traceSerialNumber; }

    @Override
    public String toString() {
        return "Root Thread Object : " + serialNumber + " : " +  traceSerialNumber;
    }
}
