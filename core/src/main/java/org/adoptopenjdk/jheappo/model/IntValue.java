package org.adoptopenjdk.jheappo.model;

import org.adoptopenjdk.jheappo.objects.BasicDataTypes;


public class IntValue extends PrimitiveValue<Integer> {

    public IntValue(Integer value) {
        super(BasicDataTypes.INT, value);
    }
}
