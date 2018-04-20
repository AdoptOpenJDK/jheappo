package com.kodewerk.jheappo.objects;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

import com.kodewerk.jheappo.io.HeapDumpBuffer;

public class RootJavaFrame extends HeapData {

    public final static int TAG = 0x03;

    private long objectID;
    private long threadSerialNumber;
    private long frameNumberInStackTrace; // -1 if empty

    public RootJavaFrame(HeapDumpBuffer buffer) {
        objectID = buffer.extractID();
        threadSerialNumber = buffer.extractU4();
        frameNumberInStackTrace = buffer.extractU4();
    }
}
