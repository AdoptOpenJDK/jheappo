package com.kodewerk.jheappo.objects;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

import com.kodewerk.jheappo.io.HeapDumpBuffer;

/*
ID object ID
u4 stack trace serial number
ID class object ID
u4 number of bytes that follow
[value]*  instance field values (this class, followed by super class, etc)
 */
public class InstanceObject extends HeapData {

    public final static int TAG = 0x21;

    private long objectID;
    private long stackTraceSerialNumber;
    private long classObjectID;
    private byte[] instanceFieldValues;

    public InstanceObject(HeapDumpBuffer buffer) {
        objectID = buffer.extractID();
        stackTraceSerialNumber = buffer.extractU4();
        classObjectID = buffer.extractID();
        int bufferLength = (int)buffer.extractU4();
        if ( bufferLength > 0)
            instanceFieldValues = buffer.read(bufferLength);

    }
}
