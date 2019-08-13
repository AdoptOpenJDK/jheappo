package org.adoptopenjdk.jheappo.model;

import org.adoptopenjdk.jheappo.objects.BasicDataTypes;


public class BasicDataTypeValue {
    Object value;
    BasicDataTypes type;

    public BasicDataTypeValue(BasicDataTypes type) {
        this.type = type;
    }

    public BasicDataTypeValue(Object value, BasicDataTypes type) {
        this.value = value;
        this.type = type;
    }

    public BasicDataTypes getType() {
        return type;
    }

    public Object getValue() {
        return this.value;
    }

    public String toString() {

        return value + " of type " + type.toString();
    }
}
