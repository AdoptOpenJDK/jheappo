package org.adoptopenjdk.jheappo.objects

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

import org.adoptopenjdk.jheappo.heapdump.EncodedChunk
import org.adoptopenjdk.jheappo.model.BasicDataTypeValue

/*
ID object ID
u4 stack trace serial number
ID class object ID
u4 number of bytes that follow
[value]*  instance field values (this class, followed by super class, etc)
 */
class InstanceObject(buffer: EncodedChunk) : HeapObject(buffer) {

    companion object {
        const val TAG: UByte = 0x21U
    }
    val stackTraceSerialNumber: UInt
    val classObjectID: Long
    var instanceFieldValues = arrayOf<BasicDataTypeValue>()

    private var buffer: EncodedChunk? = null

    init {
        this.buffer = buffer
        stackTraceSerialNumber = buffer.extractU4()
        classObjectID = buffer.extractID()
        val bufferLength = buffer.extractU4()
        this.buffer = EncodedChunk(buffer.read(bufferLength.toInt()))
    }

    fun inflate(classObject: ClassObject) {
        val b = buffer ?: return
        if (!b.endOfBuffer()) {
            instanceFieldValues = classObject.fieldTypes
                    .map { b.extractBasicType(it) }
                    .toTypedArray()
        }
        buffer = null
    }

    override fun toString(): String {
        var prefix = "InstanceObject->$classObjectID"
        if (instanceFieldValues.size > 0)
            prefix += " fields --> "
        for (i in instanceFieldValues.indices) {
            prefix += instanceFieldValues[i].toString() + ", "
        }
        return prefix
    }
}
