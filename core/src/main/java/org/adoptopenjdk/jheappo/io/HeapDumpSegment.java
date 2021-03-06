package org.adoptopenjdk.jheappo.io;


import org.adoptopenjdk.jheappo.objects.*;

/*
 ROOT UNKNOWN            | 0xFF  | ID      | object ID

 ROOT JNI GLOBAL         | 0x01  | ID      | object ID
                                 | ID      | JNI global ref ID

 ROOT JNI LOCAL          | 0x02  | ID      | object ID
                                 | u4      | thread serial number
                                 | u4      | frame number in stack trace (-1 for empty)

 ROOT JAVA FRAME         | 0x03  | ID      | object ID
                                 | u4      | thread serial number
                                 | u4      | frame number in stack trace (-1 for empty)

 ROOT NATIVE STACK       | 0x04  | ID      | object ID
                                 | u4      | thread serial number

 ROOT STICKY CLASS       | 0x05  | ID      | object ID

 ROOT THREAD BLOCK       | 0x06  | ID      | object ID
                                 | u4      | thread serial number

 ROOT MONITOR USED       | 0x07  | ID      | object ID

 ROOT THREAD OBJECT      | 0x08  | ID      | thread object ID
                                 | u4      | thread serial number
                                 | u4      | stack trace serial number

 CLASS DUMP              | 0x20  | ID      | class object ID
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


 INSTANCE DUMP           | 0x21  | ID      | object ID
                                 | u4      | stack trace serial number
                                 | ID      | class object ID
                                 | u4      | number of bytes that follow
                                 |[value]* | instance field values (this class, followed by super class, etc)

 OBJECT ARRAY DUMP       | 0x22  | ID    | array object ID
                                 | u4    | stack trace serial number
                                 | u4    | number of elements
                                 | ID    | array class object ID
                                 | [ID]* | elements

 PRIMITIVE ARRAY DUMP    | 0x23  | ID    | array object ID
                                 | u4    | stack trace serial number
                                 | u4    | number of elements
                                 | u1    | element type (See Basic Type)
                                 | [u1]* | elements (packed array)



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
public class HeapDumpSegment extends HeapProfileRecord {
    public static final int TAG1 = 0x0C;
    public static final int TAG2 = 0x1C;

    private final EncodedChunk body;

    public HeapDumpSegment(EncodedChunk body) {
        this.body = body;
    }

    public boolean hasNext() {
        return body.endOfBuffer();
    }

    public HeapObject next() {
        int typeCode = body.extractU1();
        switch (typeCode) {
            case RootUnknown.TAG:
                return new RootUnknown(body);
            case RootJNIGlobal.TAG:
                return new RootJNIGlobal(body);
            case RootJNILocal.TAG:
                return new RootJNILocal(body);
            case RootJavaFrame.TAG:
                return new RootJavaFrame(body);
            case RootNativeStack.TAG:
                return new RootNativeStack(body);
            case RootStickyClass.TAG:
                return new RootStickyClass(body);
            case RootThreadBlock.TAG:
                return new RootThreadBlock(body);
            case RootMonitorUsed.TAG:
                return new RootMonitorUsed(body);
            case RootThreadObject.TAG:
                return new RootThreadObject(body);
            case ClassObject.TAG:
                return new ClassObject(body);
            case InstanceObject.TAG:
                return new InstanceObject(body);
            case ObjectArray.TAG:
                return new ObjectArray(body);
            case PrimitiveArray.TAG:
                return new PrimitiveArray(body);
            default:
                System.out.println(typeCode + " not recognized... @index=" + body.getIndex());
                return null;
        }
    }
}
