package org.adoptopenjdk.jheappo.heapdump;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */


import org.adoptopenjdk.jheappo.io.HeapProfileRecord;

public class StackTrace extends HeapProfileRecord {

    public final static int TAG = 0x05;

    /*
        u4    | stack trace serial number
        u4    | thread serial number
        u4    | number of frames
        [ID]* | series of stack frame ID's
     */

    int stackTraceSerialNumber;
    int threadSerialNumber;
    int numberOfFrames;
    long[] stackFrameIDs;

    public StackTrace(byte[] body) {
        super(body);
        stackTraceSerialNumber = extractU4();
        threadSerialNumber = extractU4();
        numberOfFrames = extractU4();
        stackFrameIDs = new long[numberOfFrames];
        for ( int i = 0; i < numberOfFrames; i++) {
            stackFrameIDs[i] = extractID();
        }
    }

    public String toString() {
        return "StackTrace --> " + stackTraceSerialNumber + ":" + threadSerialNumber + ":" + numberOfFrames;
    }
}
