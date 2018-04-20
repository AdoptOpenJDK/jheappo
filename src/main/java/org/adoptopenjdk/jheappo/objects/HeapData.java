package com.kodewerk.jheappo.objects;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

public class HeapData {

    public static final int OBJECT = 2;
    public static final int BOOLEAN = 4;
    public static final int CHAR = 5;
    public static final int FLOAT = 6;
    public static final int DOUBLE = 7;
    public static final int BYTE = 8;
    public static final int SHORT = 9;
    public static final int INT = 10;
    public static final int LONG = 11;
    public static final int ARRAY = 12;
    public static final String[] PRIMITIVE_TYPES = { "", "", "Object", "", "boolean", "char", "float", "double", "byte", "short", "int", "long", "" };
    public static final char[] SYMBOLS = { ' ', ' ', 'L', ' ', 'V', 'C', 'F', 'D', 'B', 'S', 'I', 'L', '[' };
    public static final int[] TYPE_SIZES =  { 0, 0, 8, 0, 1, 2, 4, 8, 1, 2, 4, 8, 0 };

}
