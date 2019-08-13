package org.adoptopenjdk.jheappo.model;

import org.adoptopenjdk.jheappo.objects.BasicDataTypes;


public class ObjectValue extends BasicDataTypeValue {
    public Long objectId;

    public ObjectValue(Long objectId) {
        super(BasicDataTypes.OBJECT);
        this.objectId = objectId;
    }

    @Override
    public String toString() {

        return String.format("%d of type %s", objectId, getType());
    }
}
