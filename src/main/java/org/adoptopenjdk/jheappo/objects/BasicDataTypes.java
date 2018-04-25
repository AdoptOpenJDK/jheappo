package org.adoptopenjdk.jheappo.objects;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

public enum BasicDataTypes {

    OBJECT(   2,  "Object", 'L',  8),
    BOOLEAN(  4, "boolean", 'V',  1),
    CHAR(     5,    "char", 'C',  2),
    FLOAT(    6,   "float", 'F',  4),
    DOUBLE(   7,  "double", 'F',  8),
    BYTE(     8,    "byte", 'B',  1),
    SHORT(    9,   "short", 'S',  2),
    INT(     10,     "int", 'I',  4),
    LONG(    11,    "long", 'L',  8),
    ARRAY(   12,       "",  '[',  0),
    UNKNOWN( -1,      "",   ' ', -1);

    private int ordinalValue;
    private String name;
    private char mnemonic;
    private int size;

    BasicDataTypes(int index, String name, char mnemonic, int size) {
        this.ordinalValue = index;
        this.name = name;
        this.mnemonic = mnemonic;
        this.size = size;
    }

    public static BasicDataTypes fromInt(int typeIndex) {
        switch (typeIndex) {
            case  2 : return OBJECT;
            case  4 : return BOOLEAN;
            case  5 : return CHAR;
            case  6 : return FLOAT;
            case  7 : return DOUBLE;
            case  8 : return BYTE;
            case  9 : return SHORT;
            case 10 : return INT;
            case 11 : return LONG;
            case 12 : return ARRAY;
            default:
                return null;
        }
    }

    public int getOrdinalValue() { return ordinalValue; }
    public String getName() { return name; }
    public char getMnemonic() { return mnemonic; }
    public int size() { return size; }

}
