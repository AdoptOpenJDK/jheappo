package org.adoptopenjdk.jheappo.objects;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

import org.adoptopenjdk.jheappo.io.HeapProfileRecord;

public class RootJavaFrame extends HeapObject {

    public final static int TAG = 0x03;

    private int threadSerialNumber;
    private int frameNumberInStackTrace; // -1 if empty

    public RootJavaFrame(HeapProfileRecord buffer) {
        super(buffer);
        threadSerialNumber = buffer.extractU4();
        frameNumberInStackTrace = buffer.extractU4();
    }
}
