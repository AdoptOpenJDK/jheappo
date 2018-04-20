package com.kodewerk.jheappo.objects;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

import com.kodewerk.jheappo.io.HeapDumpBuffer;

public class RootMonitorUsed extends HeapData {

    public final static int TAG = 0x07;

    private long objectID;

    public RootMonitorUsed(HeapDumpBuffer buffer) {
        objectID = buffer.extractID();
    }
}
