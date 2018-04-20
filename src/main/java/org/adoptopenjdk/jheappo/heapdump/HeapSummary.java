package com.kodewerk.jheappo.heapdump;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

import com.kodewerk.jheappo.io.HeapDumpBuffer;

public class HeapSummary extends HeapDumpBuffer {

    public final static int TAG = 0x07;

    public HeapSummary(byte[] body) {
        super(body);
    }
}
