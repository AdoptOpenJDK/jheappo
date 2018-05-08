package org.adoptopenjdk.jheappo.objects;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */


import org.adoptopenjdk.jheappo.io.HeapProfileRecord;

public class RootStickyClass extends HeapObject {

    public static final int TAG = 0x05;

    public RootStickyClass(HeapProfileRecord buffer) {
        super(buffer);
    }
}
