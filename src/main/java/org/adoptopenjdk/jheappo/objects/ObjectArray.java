package com.kodewerk.jheappo.objects;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */


import com.kodewerk.jheappo.io.HeapDumpBuffer;

/*
ID array object ID
u4 stack trace serial number
u4 number of elements
ID array class object ID
[ID]* elements
 */
public class ObjectArray extends HeapData {

    private long objectID;
    private int stackTraceSerialNumber;
    private int size;
    private long elementsObjectID;
    private byte[] elements;

    public ObjectArray(HeapDumpBuffer buffer) {
        objectID = buffer.extractID();
        stackTraceSerialNumber = buffer.extractInt();
        size = buffer.extractInt();
        //todo: extract array
        System.out.println("Object Array size " + size);
        elementsObjectID = buffer.extractID();
        elements = buffer.read(size * 8);
    }
}
