package org.adoptopenjdk.jheappo.model

import org.adoptopenjdk.jheappo.objects.BasicDataTypes

class BasicDataTypeValue(val value: Any?, val type: BasicDataTypes) {
    override fun toString(): String {
        return "$value of type $type"
    }
}
