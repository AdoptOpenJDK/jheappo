package org.adoptopenjdk.jheappo.model;

import org.adoptopenjdk.jheappo.objects.BasicDataTypes;


public class ArrayValue extends BasicDataTypeValue {
    public ArrayValue() {
        super(BasicDataTypes.ARRAY);
    }

    @Override
    public String toString() {
        return "Array";
    }
}
