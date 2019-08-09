package org.adoptopenjdk.jheappo.model;

import org.adoptopenjdk.jheappo.objects.BasicDataTypes;


public class DoubleValue extends PrimitiveValue<Double> {

    public DoubleValue( Double value) {
        super(BasicDataTypes.DOUBLE, value);
    }
}
