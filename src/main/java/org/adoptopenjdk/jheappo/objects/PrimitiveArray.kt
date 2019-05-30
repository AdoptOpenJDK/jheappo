package org.adoptopenjdk.jheappo.objects

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

import org.adoptopenjdk.jheappo.heapdump.EncodedChunk
import org.adoptopenjdk.jheappo.model.BasicDataTypeValue


class PrimitiveArray(buffer: EncodedChunk) : HeapObject(buffer) {

    companion object {
        const val TAG = 0x23
    }

    private val stackTraceSerialNumber: Int = buffer.extractInt()
    private val size: Int = buffer.extractInt()
    private val elementType: Byte = buffer.extractByte()
    private val signature: Char

    internal val elements: Array<BasicDataTypeValue>

    init {
        val dataType = BasicDataTypes.fromInt(elementType.toInt()) ?: BasicDataTypes.UNKNOWN
        if (dataType == BasicDataTypes.UNKNOWN) {
            throw IllegalArgumentException("Unknown data type : $elementType")
        }
        signature = BasicDataTypes.fromInt(elementType.toInt())!!.mnemonic
        elements = (0 until size).map { buffer.extractBasicType(dataType) }.toTypedArray()
    }
}
