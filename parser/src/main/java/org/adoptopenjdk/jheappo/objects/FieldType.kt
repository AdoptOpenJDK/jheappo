package org.adoptopenjdk.jheappo.objects

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

enum class FieldType constructor(
        val ordinalValue: UByte,
        val label: String,
        val mnemonic: Char,
        private val size: Int
) {
    // TODO array and object size should be different for 32 bit
    ARRAY(1u, "Array", '[', 8),
    OBJECT(2u, "Object", 'L', 8),
    BOOLEAN(4u, "boolean", 'V', 1),
    CHAR(5u, "char", 'C', 2),
    FLOAT(6u, "float", 'F', 4),
    DOUBLE(7u, "double", 'F', 8),
    BYTE(8u, "byte", 'B', 1),
    SHORT(9u, "short", 'S', 2),
    INT(10u, "int", 'I', 4),
    LONG(11u, "long", 'L', 8),
    UNKNOWN(0u, "", ' ', -1);

    companion object {
        fun fromInt(typeCode: UByte): FieldType = values()
                .firstOrNull { it.ordinalValue == typeCode }
                ?: UNKNOWN
    }

}
