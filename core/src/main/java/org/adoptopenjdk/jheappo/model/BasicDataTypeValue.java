package org.adoptopenjdk.jheappo.model;

import org.adoptopenjdk.jheappo.objects.BasicDataTypes;


public abstract class BasicDataTypeValue {
    protected final BasicDataTypes type;

    public BasicDataTypeValue(BasicDataTypes type) {
        this.type = type;
    }

    public abstract String toString();
}
