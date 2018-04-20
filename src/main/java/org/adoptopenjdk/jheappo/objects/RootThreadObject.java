package org.adoptopenjdk.jheappo.objects;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */


import org.adoptopenjdk.jheappo.io.HeapDumpBuffer;

public class RootThreadObject extends HeapData {

    public static final int TAG = 0x08;

    private long threadObjectID;
    private long serialNumber;
    private long traceSerialNumber;

    public RootThreadObject(HeapDumpBuffer buffer) {
        threadObjectID = buffer.extractID();
        serialNumber = buffer.extractU4();
        traceSerialNumber = buffer.extractU4();
    }
}
