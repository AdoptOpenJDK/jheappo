package com.kodewerk.jheappo.heapdump;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */


import com.kodewerk.jheappo.io.HeapDumpBuffer;

public class UnloadClass extends HeapDumpBuffer {

    public final static int TAG = 0x03;

    /*
        u4   |  class serial number (always > 0)
     */

    long classSerialNumber;

    public UnloadClass(byte[] body) {
        super(body);
        classSerialNumber = super.extractU4();
    }

    public String toString() {
        return "Unloaded -> " + classSerialNumber;
    }
}
