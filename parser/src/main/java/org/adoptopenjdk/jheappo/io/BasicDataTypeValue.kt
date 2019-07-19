package org.adoptopenjdk.jheappo.io

/**
 * A lightweight representation of a value used in in arrays, field lists, etc. For primitives, the actual value is
 * exposed.
 */
sealed class BasicDataTypeValue(val type: FieldType) {
    abstract override fun toString(): String
}

class ObjectValue(val objectId: Id) : BasicDataTypeValue(FieldType.OBJECT) {
    override fun toString(): String = "$objectId of type $type"
}

abstract class PrimitiveValue<T>(type: FieldType) : BasicDataTypeValue(type) {
    abstract val value: T

    override fun toString(): String = "$value of type $type"
}

class BooleanValue(override val value: Boolean) : PrimitiveValue<Boolean>(FieldType.BOOLEAN)
class CharValue(override val value: Char) : PrimitiveValue<Char>(FieldType.CHAR)
class FloatValue(override val value: Float) : PrimitiveValue<Float>(FieldType.FLOAT)
class DoubleValue(override val value: Double) : PrimitiveValue<Double>(FieldType.DOUBLE)
class ByteValue(override val value: Byte) : PrimitiveValue<Byte>(FieldType.BYTE)
class ShortValue(override val value: Short) : PrimitiveValue<Short>(FieldType.SHORT)
class IntValue(override val value: Int) : PrimitiveValue<Int>(FieldType.INT)
class LongValue(override val value: Long) : PrimitiveValue<Long>(FieldType.LONG)

// TODO what about arrays? Not currently deserialized
object ArrayValue : BasicDataTypeValue(FieldType.ARRAY) {
    override fun toString(): String = "Array"
}

object UnknownValue : BasicDataTypeValue(FieldType.UNKNOWN) {
    override fun toString(): String = "Unknown"
}
