package org.adoptopenjdk.jheappo.heapdump;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */


import org.adoptopenjdk.jheappo.io.HeapDumpBuffer;

public class LoadClass extends HeapDumpBuffer {

    public final static int TAG = 0x02;

    /*
        u4   |  class serial number (always > 0)
        ID   |  class object ID
        u4   |  stack trace serial number
        ID   |  class name string ID
     */

    long classSerialNumber;
    long classObjectID;
    long stackTraceSerialNumber;
    long classNameStringID;

    public LoadClass(byte[] body) {
        super(body);
        classSerialNumber = super.extractU4();
        classObjectID = super.extractID();
        stackTraceSerialNumber = super.extractU4();
        classNameStringID = super.extractID();
    }

    public long getClassObjectID() { return classObjectID; }

    public String toString() {
        return "Loaded -> " + classSerialNumber + ":" + classObjectID + ":" + stackTraceSerialNumber + ":" + classNameStringID;
    }
}
