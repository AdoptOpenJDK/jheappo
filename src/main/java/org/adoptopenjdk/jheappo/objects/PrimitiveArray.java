package org.adoptopenjdk.jheappo.objects;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

import org.adoptopenjdk.jheappo.io.HeapProfileRecord;


public class PrimitiveArray extends HeapObject {

    public static final int TAG = 0x23;

    private int stackTraceSerialNumber;
    private int size;
    private byte elementType;
    private byte[] elements;
    private char signature = ' ';

    public PrimitiveArray(HeapProfileRecord buffer) {
        super(buffer);
        stackTraceSerialNumber = buffer.extractInt();
        size = buffer.extractInt();
        elementType = buffer.extractByte();
        elements = readArray(buffer, elementType, size);
    }

    private byte[] readArray(HeapProfileRecord buffer, byte elementType, int size) {
        BasicDataTypes dataType = BasicDataTypes.fromInt(elementType);
        signature = BasicDataTypes.fromInt(elementType).getMnemonic();
        if ( dataType.equals(BasicDataTypes.UNKNOWN)) {
            System.out.println("Unknown primitive type " + elementType);
            return new byte[0];
        }
        return buffer.read( size * dataType.size());
    }
}
