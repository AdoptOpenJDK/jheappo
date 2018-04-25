package org.adoptopenjdk.jheappo.objects;

import org.adoptopenjdk.jheappo.io.HeapDumpBuffer;
import org.adoptopenjdk.jheappo.model.BasicDataTypeValue;

public class HeapObject {

    private long id;

    public HeapObject() {
        this.id = -1;
    }

    public HeapObject(HeapDumpBuffer buffer) {
        id = buffer.extractID();
    }

    public long getId() {
        return id;
    }

    BasicDataTypeValue extractBasicType(int basicType, HeapDumpBuffer buffer) {

        switch (BasicDataTypes.fromInt(basicType)) {
            case BOOLEAN :
                return new BasicDataTypeValue(buffer.extractBoolean(),BasicDataTypes.BOOLEAN);
            case CHAR :
                return new BasicDataTypeValue(buffer.extractChar(),BasicDataTypes.CHAR);
            case BYTE :
                return new BasicDataTypeValue(buffer.extractByte(),BasicDataTypes.BYTE);
            case SHORT:
                return new BasicDataTypeValue(buffer.extractShort(),BasicDataTypes.SHORT);
            case FLOAT :
                return new BasicDataTypeValue(buffer.extractFloat(),BasicDataTypes.FLOAT);
            case INT :
                return new BasicDataTypeValue(buffer.extractInt(),BasicDataTypes.INT);
            case OBJECT :
                return new BasicDataTypeValue(buffer.extractID(),BasicDataTypes.OBJECT);
            case DOUBLE :
                return new BasicDataTypeValue(buffer.extractDouble(),BasicDataTypes.DOUBLE);
            case LONG:
                return new BasicDataTypeValue(buffer.extractLong(),BasicDataTypes.LONG);
            default:
                return new BasicDataTypeValue(null,BasicDataTypes.UNKNOWN);
        }
    }
}
