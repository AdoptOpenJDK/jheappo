package com.kodewerk.jheappo.objects;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

import com.kodewerk.jheappo.io.HeapDumpBuffer;


public class PrimitiveArray extends HeapData {

    private long objectID;
    private int stackTraceSerialNumber;
    private int size;
    private byte elementType;
    private byte[] elements;
    private char signature = ' ';

    public PrimitiveArray(HeapDumpBuffer buffer) {
        objectID = buffer.extractID();
        stackTraceSerialNumber = buffer.extractInt();
        size = buffer.extractInt();
        System.out.println("Object Array size " + size);
        elementType = buffer.extractByte();
        elements = readArray(buffer, elementType, size);
    }

    private byte[] readArray( HeapDumpBuffer buffer, byte elementType, int size) {
        if ( elementType < 2 || elementType > 11) {
            System.out.println("Unknown primitive type " + elementType);
            return new byte[0];
        }
        signature = SYMBOLS[elementType];
        return buffer.read( size * TYPE_SIZES[elementType]);
    }
}
