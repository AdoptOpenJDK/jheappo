package org.adoptopenjdk.jheappo.objects

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

import org.adoptopenjdk.jheappo.heapdump.EncodedChunk
import org.adoptopenjdk.jheappo.model.ObjectValue

/*
ID array object ID
u4 stack trace serial number
u4 number of elements
ID array class object ID
[ID]* elements
 */
class ObjectArray(buffer: EncodedChunk) : HeapObject(buffer) {
    companion object {
        const val TAG: UByte = 0x22U
    }

    private val stackTraceSerialNumber: UInt = buffer.extractU4()
    val size: UInt = buffer.extractU4()
    private val elementsObjectID: Long = buffer.extractID()

    private val elements: Array<ObjectValue>

    init {
        elements = (0U until size).map { buffer.extractBasicType(BasicDataTypes.OBJECT) }
                .map { it as ObjectValue }
                .toTypedArray()
    }

    fun getValueObjectIDAt(index: Int): Long {
        return elements[index].objectId
    }
}
