package org.adoptopenjdk.jheappo.objects;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

import org.adoptopenjdk.jheappo.io.HeapDumpBuffer;
import org.adoptopenjdk.jheappo.model.BasicDataTypeValue;
import org.adoptopenjdk.jheappo.model.JavaHeap;

/*
ID object ID
u4 stack trace serial number
ID class object ID
u4 number of bytes that follow
[value]*  instance field values (this class, followed by super class, etc)
 */
public class InstanceObject extends HeapObject {

    public final static int TAG = 0x21;

    private int stackTraceSerialNumber;
    private long classObjectID;
    private BasicDataTypeValue[] instanceFieldValues;
    private HeapDumpBuffer buffer;
    private int bufferLength;

    public InstanceObject(HeapDumpBuffer buffer) {
        super(buffer);
        this.buffer = buffer;
        stackTraceSerialNumber = buffer.extractU4();
        classObjectID = buffer.extractID();
        bufferLength = buffer.extractU4();
    }

    public void inflate(JavaHeap javaHeap) {
        if (bufferLength > 0) {
            ClassObject co = javaHeap.getClazzById(classObjectID);
            int[] fieldTypes = co.fieldTypes();
            instanceFieldValues = new BasicDataTypeValue[fieldTypes.length];
            for (int i = 0; i < fieldTypes.length; i++) {
                instanceFieldValues[i] = extractBasicType(fieldTypes[i], buffer);
            }
            buffer = null;
        } else {
            instanceFieldValues = new BasicDataTypeValue[0];
        }
    }

    public String toString() {
        String prefix = "InstanceObject->" + classObjectID;
        for( int i = 0; i < instanceFieldValues.length; i++) {
            prefix += ":" + instanceFieldValues[i].toString();
        }
        return prefix;
    }
}
