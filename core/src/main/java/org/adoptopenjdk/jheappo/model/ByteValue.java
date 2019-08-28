package org.adoptopenjdk.jheappo.model;

import org.adoptopenjdk.jheappo.objects.BasicDataTypes;


public class ByteValue extends PrimitiveValue<Byte> {

    public ByteValue(Byte value) {
        super(BasicDataTypes.BYTE, value);
    }
}
