package org.adoptopenjdk.jheappo.model;

import org.adoptopenjdk.jheappo.objects.BasicDataTypes;


public class FloatValue extends PrimitiveValue<Float> {

    public FloatValue(Float value) {
        super(BasicDataTypes.FLOAT, value);
    }
}
