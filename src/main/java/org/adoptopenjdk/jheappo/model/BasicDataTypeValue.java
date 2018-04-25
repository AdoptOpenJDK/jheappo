package org.adoptopenjdk.jheappo.model;

import org.adoptopenjdk.jheappo.objects.BasicDataTypes;

public class BasicDataTypeValue {
    Object value;
    BasicDataTypes type;

    public BasicDataTypeValue(Object value, BasicDataTypes type) {
        this.value = value;
        this.type = type;
    }

    public String toString() {
        return "Basic Type value: " + value + " of type " + type.toString();
    }
}
