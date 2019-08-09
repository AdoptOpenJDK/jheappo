package org.adoptopenjdk.jheappo.model;

import org.adoptopenjdk.jheappo.objects.BasicDataTypes;


public class ShortValue extends PrimitiveValue<Short> {
    public ShortValue(Short value) {
        super(BasicDataTypes.SHORT, value);
    }
}
