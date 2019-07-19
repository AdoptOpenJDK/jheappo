package org.adoptopenjdk.jheappo.parser

import org.adoptopenjdk.jheappo.parser.heap.BooleanArrayWrapper
import org.adoptopenjdk.jheappo.parser.heap.ByteArrayWrapper
import org.adoptopenjdk.jheappo.parser.heap.CharArrayWrapper
import org.adoptopenjdk.jheappo.parser.heap.DoubleArrayWrapper
import org.adoptopenjdk.jheappo.parser.heap.FloatArrayWrapper
import org.adoptopenjdk.jheappo.parser.heap.IntArrayWrapper
import org.adoptopenjdk.jheappo.parser.heap.LongArrayWrapper
import org.adoptopenjdk.jheappo.parser.heap.PrimitiveArrayWrapper
import org.adoptopenjdk.jheappo.parser.heap.ShortArrayWrapper
import java.io.PrintStream

/**
 * A wrapper around bytes that represents the encoding used by the hprof binary format.
 *
 * This is used by various hprof record types representing stack frames, objects in the heap, etc to decode the
 * corresponding components (e.g. id numbers). Notably, most things are represented as unsigned ints, which we cannot
 * represent well (yet).
 *
 * See https://hg.openjdk.java.net/jdk/jdk/file/9a73a4e4011f/src/hotspot/share/services/heapDumper.cpp for details on
 * the format.
 */
internal class EncodedChunk private constructor(private val body: ByteArray, index: Int) {
    var index: Int = 0
        private set

    constructor(body: ByteArray) : this(body, 0)

    init {
        this.index = index
    }

    fun read(bufferLength: Int): ByteArray {
        val buffer = ByteArray(bufferLength)
        for (i in 0 until bufferLength) {
            buffer[i] = body[index++]
        }
        return buffer
    }

    fun skip(skipOver: Int) {
        index += skipOver
    }

    fun readRemaining(): ByteArray {
        return read(body.size - index)
    }

    fun endOfBuffer(): Boolean {
        return body.size <= index
    }

    private fun read(): UByte {
        return body[index++].toUByte()
    }

    fun extractU1(): UByte {
        return read()
    }

    fun extractU2(): UShort {
        // shifts only implemented for int and long
        var value = extractU1().toUInt()
        for (cursor in 1..1) {
            value = value shl 8 or read().toUInt()
        }
        return value.toUShort()
    }

    fun extractU4(): UInt {
        var value = extractU1().toUInt()
        for (cursor in 1..3) {
            value = value shl 8 or read().toUInt()
        }
        return value
    }

    fun extractU8(): ULong {
        var value = extractU1().toULong()
        for (cursor in 1..7) {
            value = value shl 8 or read().toULong()
        }
        return value
    }

    fun extractID(): Id {
        // TODO what if identifiers are 4 bytes?
        return Id(extractU8())
    }

    private fun extractBoolean(): Boolean {
        return extractU1().toInt() != 0
    }

    private fun extractChar(): Char {
        // no toChar() on UShort
        return extractU2().toInt().toChar()
    }

    private fun extractByte(): Byte {
        return extractU1().toByte()
    }

    private fun extractShort(): Short {
        return extractU2().toShort()
    }

    private fun extractFloat(): Float {
        return java.lang.Float.intBitsToFloat(extractInt())
    }

    private fun extractInt(): Int {
        return extractU4().toInt()
    }

    private fun extractDouble(): Double {
        return java.lang.Double.longBitsToDouble(extractLong())
    }

    private fun extractLong(): Long {
        return extractU8().toLong()
    }

    fun extractBasicType(basicType: FieldType): BasicDataTypeValue {
        return when (basicType) {
            FieldType.BOOLEAN -> BooleanValue(this.extractBoolean())
            FieldType.CHAR -> CharValue(this.extractChar())
            FieldType.BYTE -> ByteValue(this.extractByte())
            FieldType.SHORT -> ShortValue(this.extractShort())
            FieldType.FLOAT -> FloatValue(this.extractFloat())
            FieldType.INT -> IntValue(this.extractInt())
            FieldType.OBJECT -> ObjectValue(this.extractID())
            FieldType.DOUBLE -> DoubleValue(this.extractDouble())
            FieldType.LONG -> LongValue(this.extractLong())
            FieldType.ARRAY -> ArrayValue
            FieldType.UNKNOWN -> UnknownValue
        }
    }

    fun extractPrimitiveArray(type: FieldType, size: UInt): PrimitiveArrayWrapper {
        val sizeInt = size.toInt()
        return when (type) {
            FieldType.ARRAY, FieldType.OBJECT, FieldType.UNKNOWN -> throw IllegalArgumentException(
                    "Must be a primitive type")
            FieldType.BOOLEAN -> {
                val arr = BooleanArray(size.toInt())
                (0 until sizeInt).forEach { arr[it] = extractBoolean() }
                BooleanArrayWrapper(arr)
            }
            FieldType.CHAR -> {
                val arr = CharArray(size.toInt())
                (0 until sizeInt).forEach { arr[it] = extractChar() }
                CharArrayWrapper(arr)
            }
            FieldType.FLOAT -> {
                val arr = FloatArray(size.toInt())
                (0 until sizeInt).forEach { arr[it] = extractFloat() }
                FloatArrayWrapper(arr)
            }
            FieldType.DOUBLE -> {
                val arr = DoubleArray(size.toInt())
                (0 until sizeInt).forEach { arr[it] = extractDouble() }
                DoubleArrayWrapper(arr)
            }
            FieldType.BYTE -> {
                val arr = ByteArray(size.toInt())
                (0 until sizeInt).forEach { arr[it] = extractByte() }
                ByteArrayWrapper(arr)
            }
            FieldType.SHORT -> {
                val arr = ShortArray(size.toInt())
                (0 until sizeInt).forEach { arr[it] = extractShort() }
                ShortArrayWrapper(arr)
            }
            FieldType.INT -> {
                val arr = IntArray(size.toInt())
                (0 until sizeInt).forEach { arr[it] = extractInt() }
                IntArrayWrapper(arr)
            }
            FieldType.LONG -> {
                val arr = LongArray(size.toInt())
                (0 until sizeInt).forEach { arr[it] = extractLong() }
                LongArrayWrapper(arr)
            }
        }
    }

    /**
     * Useful when you want to read some bytes without affecting the internal cursor position.
     *
     * @return A chunk with the same byte array and offset as this object.
     */
    fun copy(): EncodedChunk {
        return EncodedChunk(body, index)
    }

    /*
    Debugging aid
     */
    fun dump(out: PrintStream) {
        val max = if (body.size > 1000) 1000 else body.size
        for (cursor in 0 until max) {
            out.print(Integer.toHexString(body[cursor].toInt() and 255))
            out.print(" ")
        }
        out.println("")
        for (cursor in 0 until max) {
            out.print((body[cursor].toInt() and 255).toChar())
            out.print(" ")
        }
        out.println("")
    }

}

// eventually, a sealed class hierarchy for different id sizes
data class Id(val id: ULong) {
    override fun toString(): String = id.toString()
}
