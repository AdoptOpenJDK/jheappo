package org.adoptopenjdk.jheappo.model

import org.adoptopenjdk.jheappo.objects.BasicDataTypes

sealed class BasicDataTypeValue(val type: BasicDataTypes) {
    abstract override fun toString(): String
}

class ObjectValue(val objectId: Long) : BasicDataTypeValue(BasicDataTypes.OBJECT) {
    override fun toString(): String = "$objectId of type $type"
}

abstract class PrimitiveValue<T>(type: BasicDataTypes) : BasicDataTypeValue(type) {
    abstract val value: T

    override fun toString(): String = "$value of type $type"
}

class BooleanValue(override val value: Boolean) : PrimitiveValue<Boolean>(BasicDataTypes.BOOLEAN)
class CharValue(override val value: Char) : PrimitiveValue<Char>(BasicDataTypes.CHAR)
class FloatValue(override val value: Float) : PrimitiveValue<Float>(BasicDataTypes.FLOAT)
class DoubleValue(override val value: Double) : PrimitiveValue<Double>(BasicDataTypes.DOUBLE)
class ByteValue(override val value: Byte) : PrimitiveValue<Byte>(BasicDataTypes.BYTE)
class ShortValue(override val value: Short) : PrimitiveValue<Short>(BasicDataTypes.SHORT)
class IntValue(override val value: Int) : PrimitiveValue<Int>(BasicDataTypes.INT)
class LongValue(override val value: Long) : PrimitiveValue<Long>(BasicDataTypes.LONG)

// TODO what about arrays? Not currently deserialized
object ArrayValue : BasicDataTypeValue(BasicDataTypes.ARRAY) {
    override fun toString(): String = "Array"
}

object UnknownValue : BasicDataTypeValue(BasicDataTypes.UNKNOWN) {
    override fun toString(): String = "Unknown"
}
