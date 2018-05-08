package org.adoptopenjdk.jheappo.objects;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

import org.adoptopenjdk.jheappo.io.HeapProfileRecord;
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

    public ClassObject(HeapProfileRecord buffer) {
        super(buffer); //classObjectID;
        extractPoolData( buffer);
    }

    private void extractPoolData( HeapProfileRecord buffer) {
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
    private void extractConstantPool( HeapProfileRecord buffer) {
        int numberOfRecords = buffer.extractU2();
        for ( int i = 0; i < numberOfRecords; i++) {
            int constantPoolIndex = buffer.extractU2();
            BasicDataTypeValue value = buffer.extractBasicType(buffer.extractU1());
            System.out.println( "Constant Pool: " + constantPoolIndex + ":" + value.toString());
        }
    }

    /*
        | u2  | Number of static fields:
              | ID    | static field name string ID
              | u1    | type of field: (See Basic Type)
              | value | value of entry (u1, u2, u4, or u8 based on type of field)
     */
    private void extractStaticFields( HeapProfileRecord buffer) {
        int numberOfRecords = buffer.extractU2();
        staticFieldNameIndicies = new long[numberOfRecords];
        staticValues = new BasicDataTypeValue[numberOfRecords];

        for (int i = 0; i < numberOfRecords; i++) {
            staticFieldNameIndicies[i] = buffer.extractID();
            staticValues[i] = buffer.extractBasicType(buffer.extractU1());
        }
    }

    /*
        | u2  | Number of instance fields (not including super class's)
              | ID    | field name string ID
              | u1    | type of field: (See Basic Type)
     */
    private void extractInstanceFields( HeapProfileRecord buffer) {
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
        String fields = ", Fields --> ";
        for ( int i = 0; i < fieldNamesIndicies.length; i++) {
            fields += ", " + fieldNamesIndicies[i];
        }

        return "Class Object -->" + getId() +
                ", stackTraceSerialNumber -->" + stackTraceSerialNumber +
                ", superClassObjectID -->" + superClassObjectID +
                ", classLoaderObjectID -->" + classLoaderObjectID +
                ", signersObjectID -->" + signersObjectID +
                ", protectionDomainObjectID -->" + protectionDomainObjectID +
                fields;
    }

    public int[] fieldTypes() {
        return fieldTypes;
    }

    public long[] fieldNameIndicies() {
        return fieldNamesIndicies;
    }
}
