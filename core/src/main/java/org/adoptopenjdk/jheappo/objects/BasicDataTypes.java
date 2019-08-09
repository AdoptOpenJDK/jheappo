package org.adoptopenjdk.jheappo.objects;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

import java.util.stream.Stream;

public enum BasicDataTypes {

    OBJECT(2, "Object", 'L', 8),
    BOOLEAN(4, "boolean", 'V', 1),
    CHAR(5, "char", 'C', 2),
    FLOAT(6, "float", 'F', 4),
    DOUBLE(7, "double", 'F', 8),
    BYTE(8, "byte", 'B', 1),
    SHORT(9, "short", 'S', 2),
    INT(10, "int", 'I', 4),
    LONG(11, "long", 'L', 8),
    // TODO Not found in https://hg.openjdk.java.net/jdk/jdk/file/9a73a4e4011f/src/hotspot/share/services/heapDumper.cpp
    ARRAY(12, "", '[', 0),
    UNKNOWN(-1, "", ' ', -1);


    private final int ordinalValue;
    private final String label;
    private final char mnemonic;
    private final int size;

    BasicDataTypes(int ordinalValue, String label, char mnemonic, int size) {
        this.ordinalValue = ordinalValue;
        this.label = label;
        this.mnemonic = mnemonic;
        this.size = size;
    }

    public int getOrdinalValue() {
        return ordinalValue;
    }

    public String getLabel() {
        return label;
    }

    public char getMnemonic() {
        return mnemonic;
    }

    public int getSize() {
        return size;
    }

    public static BasicDataTypes fromInt(int typeIndex) {

        return Stream.of(values())
                .filter(it -> it.ordinalValue == typeIndex)
                .findFirst()
                .orElse(null);
    }
}
