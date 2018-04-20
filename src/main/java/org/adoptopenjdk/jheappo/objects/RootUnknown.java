package org.adoptopenjdk.jheappo.objects;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */


import org.adoptopenjdk.jheappo.io.HeapDumpBuffer;

public class RootUnknown extends HeapData {

    public final static int TAG = 0xFF;

    long objectID;

    public RootUnknown(HeapDumpBuffer buffer) {
        objectID = buffer.extractID();
    }
}
