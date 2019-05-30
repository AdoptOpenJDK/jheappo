package org.adoptopenjdk.jheappo.objects

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

import org.adoptopenjdk.jheappo.heapdump.EncodedChunk
import org.adoptopenjdk.jheappo.model.BasicDataTypeValue

/*
ID array object ID
u4 stack trace serial number
u4 number of elements
ID array class object ID
[ID]* elements
 */
class ObjectArray(buffer: EncodedChunk) : HeapObject(buffer) {
    companion object {
        const val TAG = 0x22
    }

    private val stackTraceSerialNumber: Int = buffer.extractInt()
    val size: Int = buffer.extractInt()
    private val elementsObjectID: Long = buffer.extractID()

    private val elements: Array<BasicDataTypeValue>

    init {
        elements = (0 until size).map { buffer.extractBasicType(BasicDataTypes.OBJECT) }.toTypedArray()
    }

    fun getValueObjectIDAt(index: Int): Long {
        return elements[index].value as Long
    }
}
