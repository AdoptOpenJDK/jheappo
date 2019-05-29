package org.adoptopenjdk.jheappo.objects

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

import org.adoptopenjdk.jheappo.heapdump.EncodedChunk
import org.adoptopenjdk.jheappo.model.BasicDataTypeValue


class PrimitiveArray(buffer: EncodedChunk) : HeapObject(buffer) {

    /*
        id         array object ID
        u4         stack trace serial number
        u4         number of elements
        u1         element type
        [u1]*      elements
     */
    companion object {
        const val TAG: UByte = 0x23U
    }

    private val stackTraceSerialNumber: UInt = buffer.extractU4()
    private val size: UInt = buffer.extractU4()
    private val elementType: UByte = buffer.extractU1()
    private val signature: Char

    internal val elements: Array<BasicDataTypeValue>

    init {
        val dataType = BasicDataTypes.fromInt(elementType.toInt()) ?: BasicDataTypes.UNKNOWN
        if (dataType == BasicDataTypes.UNKNOWN) {
            throw IllegalArgumentException("Unknown data type : $elementType")
        }
        signature = BasicDataTypes.fromInt(elementType.toInt())!!.mnemonic
        elements = (0U until size).map { buffer.extractBasicType(dataType) }.toTypedArray()
    }
}
