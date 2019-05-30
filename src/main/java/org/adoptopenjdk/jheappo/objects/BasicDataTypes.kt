package org.adoptopenjdk.jheappo.objects

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

enum class BasicDataTypes constructor(
        val ordinalValue: Int,
        val label: String,
        val mnemonic: Char,
        private val size: Int
) {
    OBJECT(2, "Object", 'L', 8),
    BOOLEAN(4, "boolean", 'V', 1),
    CHAR(5, "char", 'C', 2),
    FLOAT(6, "float", 'F', 4),
    DOUBLE(7, "double", 'F', 8),
    BYTE(8, "byte", 'B', 1),
    SHORT(9, "short", 'S', 2),
    INT(10, "int", 'I', 4),
    LONG(11, "long", 'L', 8),
    ARRAY(12, "", '[', 0),
    UNKNOWN(-1, "", ' ', -1);

    fun size(): Int {
        return size
    }

    companion object {
        fun fromInt(typeIndex: Int): BasicDataTypes? {
            return when (typeIndex) {
                2 -> OBJECT
                4 -> BOOLEAN
                5 -> CHAR
                6 -> FLOAT
                7 -> DOUBLE
                8 -> BYTE
                9 -> SHORT
                10 -> INT
                11 -> LONG
                12 -> ARRAY
                else -> null
            }
        }
    }

}
