package com.kodewerk.jheappo.objects;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

import com.kodewerk.jheappo.io.HeapDumpBuffer;

import java.util.ArrayList;

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

public class ClassObject extends HeapData {

    public static final int TAG = 0x20;

    long classObjectID;
    int stackTraceSerialNumber;
    long superClassObjectID;
    long classLoaderObjectID;
    long signersObjectID;
    long protectionDomainObjectID;
    long[] reserved = new long[2];
    int instanceSizeInBytes;

    int constantPoolSizeInBytes = 0;

    public ClassObject(HeapDumpBuffer buffer) {
        extractPoolData( buffer);
    }

    private void extractPoolData( HeapDumpBuffer buffer) {
        classObjectID = buffer.extractID();
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
            extractBasicType(basicType, buffer);
            System.out.println( "Constant Pool: " + constantPoolIndex + ":" + PRIMITIVE_TYPES[basicType]);
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
        for (int i = 0; i < numberOfRecords; i++) {
            long staticFieldNameStringID = buffer.extractID();
            int basicType = buffer.extractU1();
            extractBasicType(basicType, buffer);
            System.out.println( "Static Fields: " + staticFieldNameStringID + ":" + PRIMITIVE_TYPES[basicType]);
        }
    }

    /*
        | u2  | Number of instance fields (not including super class's)
              | ID    | field name string ID
              | u1    | type of field: (See Basic Type)
     */
    private void extractInstanceFields( HeapDumpBuffer buffer) {
        int numberOfInstanceFields = buffer.extractU2();
        for (int i = 0; i < numberOfInstanceFields; i++) {
            long instanceFieldNameStringID = buffer.extractID();
            if ( instanceFieldNameStringID < 1)
                System.out.println("error: " + instanceFieldNameStringID);
            int basicType = buffer.extractU1();
            System.out.println( "Instance Fields: " + instanceFieldNameStringID + ":" + PRIMITIVE_TYPES[basicType]);
        }
    }

    private void extractBasicType(int basicType, HeapDumpBuffer buffer) {

        switch (basicType) {
            case BOOLEAN : {
                boolean value = buffer.extractBoolean();
                System.out.println("boolean : " + value);
            }
            break;
            case CHAR : {
                char value = buffer.extractChar();
                System.out.println("char : " + value);
            }
            break;
            case BYTE : {
                byte value = buffer.extractByte();
                System.out.println("byte : " + value);
            }
            break;
            case SHORT: {
                short value = buffer.extractShort();
                System.out.println("short : " + value);
            }
            break;
            case FLOAT : {
                float value = buffer.extractFloat();
                System.out.println("float : " + value);
            }
            break;
            case INT : {
                int value = buffer.extractInt();
                System.out.println("int : " + value);
            }
            break;
            case OBJECT : {
                long value = buffer.extractID();
                System.out.println("Object : " + value);
            }
            break;
            case DOUBLE : {
                double value = buffer.extractDouble();
                System.out.println("double : " + value);
            }
            break;
            case LONG: {
                long value = buffer.extractLong();
                System.out.println("long : " + value);
            }
            break;
            default:
                System.out.println("primitive types[" + basicType + "] = unknown basic type");

        }
    }

    public String toString() {
        return "Class Object: " + classObjectID;
    }
}
