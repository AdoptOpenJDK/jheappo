package org.adoptopenjdk.jheappo.io

import org.adoptopenjdk.jheappo.model.ArrayValue
import org.adoptopenjdk.jheappo.model.BasicDataTypeValue
import org.adoptopenjdk.jheappo.model.BooleanValue
import org.adoptopenjdk.jheappo.model.ByteValue
import org.adoptopenjdk.jheappo.model.CharValue
import org.adoptopenjdk.jheappo.model.DoubleValue
import org.adoptopenjdk.jheappo.model.FloatValue
import org.adoptopenjdk.jheappo.model.IntValue
import org.adoptopenjdk.jheappo.model.LongValue
import org.adoptopenjdk.jheappo.model.ObjectValue
import org.adoptopenjdk.jheappo.model.ShortValue
import org.adoptopenjdk.jheappo.model.UnknownValue
import org.adoptopenjdk.jheappo.objects.FieldType
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

    fun extractID(): Long {
        // TODO what if identifiers are 4 bytes?
        return extractU8().toLong()
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

    fun extractBasicType(basicType: UByte): BasicDataTypeValue {
        return extractBasicType(FieldType.fromInt(basicType))
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
            print(Integer.toHexString(body[cursor].toInt() and 255))
            print(" ")
        }
        out.println("")
        for (cursor in 0 until max) {
            print((body[cursor].toInt() and 255).toChar())
            print(" ")
        }
        out.println("")
    }

}
