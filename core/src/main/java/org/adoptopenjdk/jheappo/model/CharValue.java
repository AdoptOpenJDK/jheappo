package org.adoptopenjdk.jheappo.model;

import org.adoptopenjdk.jheappo.objects.BasicDataTypes;


public class CharValue extends PrimitiveValue<Character> {

    public CharValue(Character value) {
        super(BasicDataTypes.CHAR, value);
    }
}
