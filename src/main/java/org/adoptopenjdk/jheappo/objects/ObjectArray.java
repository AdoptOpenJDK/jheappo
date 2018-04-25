package org.adoptopenjdk.jheappo.objects;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */


import org.adoptopenjdk.jheappo.io.HeapDumpBuffer;

/*
ID array object ID
u4 stack trace serial number
u4 number of elements
ID array class object ID
[ID]* elements
 */
public class ObjectArray extends HeapObject {

    public static final int TAG = 0x22;

    private int stackTraceSerialNumber;
    private int size;
    private long elementsObjectID;
    private byte[] elements;

    public ObjectArray(HeapDumpBuffer buffer) {
        super(buffer);
        stackTraceSerialNumber = buffer.extractInt();
        size = buffer.extractInt();
        //todo: extract array
        //System.out.println("Object Array size " + size);
        elementsObjectID = buffer.extractID();
        elements = buffer.read(size * 8);
    }
}
