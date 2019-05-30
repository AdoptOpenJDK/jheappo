package org.adoptopenjdk.jheappo.objects;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

import org.adoptopenjdk.jheappo.heapdump.EncodedChunk;

public class RootUnknown extends HeapObject {

    public final static int TAG = 0xFF;

    public RootUnknown(EncodedChunk buffer) {
        super(buffer);
    }

    @Override
    public String toString() {
        return "Root Unknown : " + getId();
    }
}
