package org.adoptopenjdk.jheappo.objects;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

import org.adoptopenjdk.jheappo.heapdump.EncodedChunk;

public class RootThreadBlock extends HeapObject {

    public final static int TAG = 0x06;

    private int threadSerialNumber;

    public RootThreadBlock(EncodedChunk buffer) {
        super( buffer);
        threadSerialNumber = buffer.extractU4();
    }

    public String toString() {
        return "Root Sticky Class : " + getId() + " : " + threadSerialNumber;
    }
}
