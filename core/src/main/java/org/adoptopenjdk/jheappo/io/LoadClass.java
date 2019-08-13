package org.adoptopenjdk.jheappo.io;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

public class LoadClass extends HeapProfileRecord {


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

    public LoadClass(EncodedChunk body) {
        classSerialNumber = body.extractU4();
        classObjectID = body.extractID();
        stackTraceSerialNumber = body.extractU4();
        classNameStringID = body.extractID();
    }

    public long getClassObjectID() {
        return classObjectID;
    }

    public long classSerialNumber() {
        return classSerialNumber;
    }

    public long stackTraceSerialNumber() {
        return stackTraceSerialNumber;
    }

    public long classNameStringID() {
        return classNameStringID;
    }

    public String toString() {
        return "Loaded -> " + classSerialNumber + ":" + classObjectID + ":" + stackTraceSerialNumber + ":" + classNameStringID;
    }
}
