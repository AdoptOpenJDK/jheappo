package org.adoptopenjdk.jheappo.objects;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */


import org.adoptopenjdk.jheappo.io.EncodedChunk;

public class RootNativeStack extends HeapObject {

    public final static int TAG = 0x04;

    private int threadSerialNumber;

    public RootNativeStack(EncodedChunk buffer) {
        super(buffer);
        threadSerialNumber = buffer.extractU4();
    }
}
