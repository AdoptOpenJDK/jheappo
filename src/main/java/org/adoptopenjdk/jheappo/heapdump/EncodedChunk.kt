package org.adoptopenjdk.jheappo.heapdump

import java.io.PrintStream
import org.adoptopenjdk.jheappo.model.BasicDataTypeValue
import org.adoptopenjdk.jheappo.objects.BasicDataTypes
import kotlin.experimental.and

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
class EncodedChunk private constructor(private val body: ByteArray, index: Int) {
    var index: Int = 0
        private set

    constructor(body: ByteArray) : this(body, 0) {}

    init {
        this.index = index
    }

    fun read(bufferLength: Int): ByteArray {
        val buffer = ByteArray(bufferLength)
        for (i in 0 until bufferLength) {
            buffer[i] = (body[index++].toInt() and 0xff).toByte()
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

    private fun read(): Int {
        return body[index++].toInt() and 0xff
    }

    fun extractU1(): Byte {
        return read().toByte()
    }

    fun extractU2(): Short {
        var value = extractU1().toInt()
        for (cursor in 1..1) {
            value = value shl 8 or read()
        }
        return value.toShort()
    }

    fun extractU4(): Int {
        var value = extractU1().toInt()
        for (cursor in 1..3) {
            value = value shl 8 or read()
        }
        return value
    }

    fun extractU8(): Long {
        var value = extractU1().toLong()
        for (cursor in 1..7) {
            value = value shl 8 or read().toLong()
        }
        return value
    }

    fun extractID(): Long {
        return extractU8()
    }

    fun extractBoolean(): Boolean {
        return extractU1().toInt() != 0
    }

    fun extractChar(): Char {
        return extractU2().toChar()
    }

    fun extractByte(): Byte {
        return extractU1()
    }

    fun extractShort(): Short {
        return extractU2()
    }

    fun extractFloat(): Float {
        return java.lang.Float.intBitsToFloat(extractInt())
    }

    fun extractInt(): Int {
        return extractU4()
    }

    fun extractDouble(): Double {
        return java.lang.Double.longBitsToDouble(extractLong())
    }

    fun extractLong(): Long {
        return extractU8()
    }

    fun extractBasicType(basicType: BasicDataTypes): BasicDataTypeValue {
        return when (basicType) {
            BasicDataTypes.BOOLEAN -> BasicDataTypeValue(this.extractBoolean(), BasicDataTypes.BOOLEAN)
            BasicDataTypes.CHAR -> BasicDataTypeValue(this.extractChar(), BasicDataTypes.CHAR)
            BasicDataTypes.BYTE -> BasicDataTypeValue(this.extractByte(), BasicDataTypes.BYTE)
            BasicDataTypes.SHORT -> BasicDataTypeValue(this.extractShort(), BasicDataTypes.SHORT)
            BasicDataTypes.FLOAT -> BasicDataTypeValue(this.extractFloat(), BasicDataTypes.FLOAT)
            BasicDataTypes.INT -> BasicDataTypeValue(this.extractInt(), BasicDataTypes.INT)
            BasicDataTypes.OBJECT -> BasicDataTypeValue(this.extractID(), BasicDataTypes.OBJECT)
            BasicDataTypes.DOUBLE -> BasicDataTypeValue(this.extractDouble(), BasicDataTypes.DOUBLE)
            BasicDataTypes.LONG -> BasicDataTypeValue(this.extractLong(), BasicDataTypes.LONG)
            else -> BasicDataTypeValue(null, BasicDataTypes.UNKNOWN)
        }
    }

    fun extractBasicType(basicType: Int): BasicDataTypeValue {
        return extractBasicType(BasicDataTypes.fromInt(basicType)!!)
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
