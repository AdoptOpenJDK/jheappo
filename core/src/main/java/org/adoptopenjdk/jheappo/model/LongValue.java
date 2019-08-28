package org.adoptopenjdk.jheappo.model;

import org.adoptopenjdk.jheappo.objects.BasicDataTypes;


public class LongValue extends PrimitiveValue<Long> {

    public LongValue(Long value) {
        super(BasicDataTypes.LONG, value);
    }
}
