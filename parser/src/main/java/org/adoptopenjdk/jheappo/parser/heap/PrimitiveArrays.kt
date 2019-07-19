package org.adoptopenjdk.jheappo.parser.heap

/**
 * Represent primitive arrays as-is, rather than as `Array<BasicDataTypeValue>`, avoiding a lot of overhead
 */
sealed class PrimitiveArrayWrapper

class BooleanArrayWrapper(val array: BooleanArray) : PrimitiveArrayWrapper()
class CharArrayWrapper(val array: CharArray) : PrimitiveArrayWrapper()
class FloatArrayWrapper(val array: FloatArray) : PrimitiveArrayWrapper()
class DoubleArrayWrapper(val array: DoubleArray) : PrimitiveArrayWrapper()
class ByteArrayWrapper(val array: ByteArray) : PrimitiveArrayWrapper()
class ShortArrayWrapper(val array: ShortArray) : PrimitiveArrayWrapper()
class IntArrayWrapper(val array: IntArray) : PrimitiveArrayWrapper()
class LongArrayWrapper(val array: LongArray) : PrimitiveArrayWrapper()
