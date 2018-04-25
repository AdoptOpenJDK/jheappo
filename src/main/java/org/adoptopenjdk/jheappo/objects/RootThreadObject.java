package org.adoptopenjdk.jheappo.objects;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */


import org.adoptopenjdk.jheappo.io.HeapDumpBuffer;

public class RootThreadObject extends HeapObject {

    public static final int TAG = 0x08;

    private int serialNumber;
    private int traceSerialNumber;

    public RootThreadObject(HeapDumpBuffer buffer) {
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
