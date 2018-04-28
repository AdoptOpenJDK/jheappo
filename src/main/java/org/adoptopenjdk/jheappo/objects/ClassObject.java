package org.adoptopenjdk.jheappo.objects;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

import org.adoptopenjdk.jheappo.io.HeapDumpBuffer;
import org.adoptopenjdk.jheappo.model.BasicDataTypeValue;

/*
        0x20    | ID      | class object ID
                | u4      | stack trace serial number
                | ID      | super class object ID
                | ID      | class loader object ID
                | ID      | signers object ID
                | ID      | protection domain object ID
                | ID      | reserved
                | ID      | reserved
                | u4      | instance size (in bytes)
                | u2      | size of constant pool and number of records that follow:
                          | u2    | constant pool index
                          | u1    | type of entry: (See Basic Type)
                          | value | value of entry (u1, u2, u4, or u8 based on type of entry)
                | u2      | Number of static fields:
                          | ID    | static field name string ID
                          | u1    | type of field: (See Basic Type)
                          | value | value of entry (u1, u2, u4, or u8 based on type of field)
                | u2      | Number of instance fields (not including super class's)
                          | ID    | field name string ID
                          | u1    | type of field: (See Basic Type)

        Basic Types
         2  | object
         4  | boolean
         5  | char
         6  | float
         7  | double
         8  | byte
         9  | short
        10  | int
        11  | long
 */

public class ClassObject extends HeapObject {

    public static final int TAG = 0x20;

    int stackTraceSerialNumber;
    long superClassObjectID;
    long classLoaderObjectID;
    long signersObjectID;
    long protectionDomainObjectID;
    long[] reserved = new long[2];
    int instanceSizeInBytes;

    long[] staticFieldNameIndicies;
    BasicDataTypeValue[] staticValues;

    long[] fieldNamesIndicies;
    int[] fieldTypes;

    public ClassObject(HeapDumpBuffer buffer) {
        super(buffer); //classObjectID;
        extractPoolData( buffer);
    }

    private void extractPoolData( HeapDumpBuffer buffer) {
        if ( buffer.getIndex() > 265000)
            System.out.println("here @" + buffer.getIndex());
        stackTraceSerialNumber = buffer.extractU4();
        superClassObjectID = buffer.extractID();
        classLoaderObjectID = buffer.extractID();
        signersObjectID = buffer.extractID();;
        protectionDomainObjectID = buffer.extractID();;
        reserved[0] = buffer.extractID();;
        reserved[1] = buffer.extractID(); ;
        instanceSizeInBytes = buffer.extractU4();
        extractConstantPool(buffer);
        extractStaticFields(buffer);
        extractInstanceFields(buffer);
    }

    /*
        | u2      | size of constant pool and number of records that follow:
                  | u2    | constant pool index
                  | u1    | type of entry: (See Basic Type)
                  | value | value of entry (u1, u2, u4, or u8 based on type of entry)
     */
    private void extractConstantPool( HeapDumpBuffer buffer) {
        int numberOfRecords = buffer.extractU2();
        for ( int i = 0; i < numberOfRecords; i++) {
            int constantPoolIndex = buffer.extractU2();
            int basicType = buffer.extractU1();
            BasicDataTypeValue value = extractBasicType(basicType, buffer);
            System.out.println( "Constant Pool: " + constantPoolIndex + ":" + BasicDataTypes.fromInt(basicType).getName() + "=" + value.toString());
        }
    }

    /*
        | u2  | Number of static fields:
              | ID    | static field name string ID
              | u1    | type of field: (See Basic Type)
              | value | value of entry (u1, u2, u4, or u8 based on type of field)
     */
    private void extractStaticFields( HeapDumpBuffer buffer) {
        int numberOfRecords = buffer.extractU2();
        staticFieldNameIndicies = new long[numberOfRecords];
        staticValues = new BasicDataTypeValue[numberOfRecords];

        for (int i = 0; i < numberOfRecords; i++) {
            staticFieldNameIndicies[i] = buffer.extractID();
            int basicType = buffer.extractU1();
            staticValues[i] = extractBasicType(basicType, buffer);
        }
    }

    /*
        | u2  | Number of instance fields (not including super class's)
              | ID    | field name string ID
              | u1    | type of field: (See Basic Type)
     */
    private void extractInstanceFields( HeapDumpBuffer buffer) {
        int numberOfInstanceFields = buffer.extractU2();
        if ( numberOfInstanceFields > -1) {
            fieldNamesIndicies = new long[numberOfInstanceFields];
            fieldTypes = new int[numberOfInstanceFields];
        }
        for (int i = 0; i < numberOfInstanceFields; i++) {
            fieldNamesIndicies[i] = buffer.extractID();
            if ( fieldNamesIndicies[i] < 1)
                System.out.println("field name invalid id: " + fieldNamesIndicies[i]);
            fieldTypes[i] = buffer.extractU1();
        }
    }

    public String toString() {
        return "Class Object: " + getId();
    }

    public int[] fieldTypes() {
        return fieldTypes;
    }

    public long[] fieldNameIndicies() {
        return fieldNamesIndicies;
    }
}
