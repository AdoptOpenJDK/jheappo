package org.adoptopenjdk.jheappo.model;

import org.adoptopenjdk.jheappo.objects.BasicDataTypes;


public class UnknownValue extends BasicDataTypeValue {
    public UnknownValue() {
        super(BasicDataTypes.UNKNOWN);
    }

    @Override
    public String toString() {

        return "Unknown";
    }
}
