package org.adoptopenjdk.jheappo.model;

import org.adoptopenjdk.jheappo.objects.BasicDataTypes;


public abstract class PrimitiveValue<T> extends BasicDataTypeValue {
    protected T value;

    public PrimitiveValue(BasicDataTypes type, T value) {
        super(type);
        this.value = value;
    }

    @Override
    public String toString() {

        return String.format("%s of type %s", value, type);
    }
}
