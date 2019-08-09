package org.adoptopenjdk.jheappo.model;

import org.adoptopenjdk.jheappo.objects.BasicDataTypes;


public class BooleanValue extends PrimitiveValue<Boolean> {
    public BooleanValue(Boolean value) {
        super(BasicDataTypes.BOOLEAN, value);
    }
}
