package org.adoptopenjdk.jheappo.objects;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

import org.adoptopenjdk.jheappo.heapdump.EncodedChunk;
import org.adoptopenjdk.jheappo.model.BasicDataTypeValue;


public class PrimitiveArray extends HeapObject {

    public static final int TAG = 0x23;

    private int stackTraceSerialNumber;
    private int size;
    private byte elementType;
    private char signature = ' ';
    BasicDataTypeValue[] elements;

    public PrimitiveArray(EncodedChunk buffer) {
        super(buffer);
        stackTraceSerialNumber = buffer.extractInt();
        size = buffer.extractInt();
        elementType = buffer.extractByte();
        readArray(buffer,elementType,size);
    }

    private void readArray(EncodedChunk buffer, byte elementType, int size) {
        BasicDataTypes dataType = BasicDataTypes.fromInt(elementType);
        signature = BasicDataTypes.fromInt(elementType).getMnemonic();
        if ( dataType.equals(BasicDataTypes.UNKNOWN)) {
            throw new IllegalArgumentException("Unknown data type : " + elementType);
        }
        elements = new BasicDataTypeValue[size];
        for (int i = 0; i < size; i++) {
            elements[i] = buffer.extractBasicType(dataType);
        }
    }
}
