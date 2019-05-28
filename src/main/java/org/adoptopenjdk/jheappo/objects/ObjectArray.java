package org.adoptopenjdk.jheappo.objects;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

import org.adoptopenjdk.jheappo.heapdump.EncodedChunk;
import org.adoptopenjdk.jheappo.model.BasicDataTypeValue;

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
    private BasicDataTypeValue[] elements;

    public ObjectArray(EncodedChunk buffer) {
        super(buffer);
        stackTraceSerialNumber = buffer.extractInt();
        size = buffer.extractInt();
        elementsObjectID = buffer.extractID();
        elements = new BasicDataTypeValue[size];
        for ( int i = 0; i < size; i++) {
            elements[i] = buffer.extractBasicType(BasicDataTypes.OBJECT);
        }
    }

    public int getStackTraceSerialNumber() {
        return stackTraceSerialNumber;
    }

    public int getSize() {
        return size;
    }

    public long getElementsObjectID() {
        return elementsObjectID;
    }

    public long getValueObjectIDAt(int index) {
        return ((Long)elements[index].getValue()).longValue();
    }
}
