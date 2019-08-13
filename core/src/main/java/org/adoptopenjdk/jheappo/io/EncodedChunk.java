package org.adoptopenjdk.jheappo.io;

import java.io.PrintStream;
import java.util.stream.IntStream;

import org.adoptopenjdk.jheappo.model.*;
import org.adoptopenjdk.jheappo.objects.BasicDataTypes;

/**
 * A wrapper around bytes that represents the encoding used by the hprof binary format.
 * <p>
 * This is used by various hprof record types representing stack frames, objects in the heap, etc to decode the
 * corresponding components (e.g. id numbers). Notably, most things are represented as unsigned ints, which we cannot
 * represent well (yet).
 * <p>
 * See https://hg.openjdk.java.net/jdk/jdk/file/9a73a4e4011f/src/hotspot/share/services/heapDumper.cpp for details on
 * the format.
 */
public class EncodedChunk {
    private final byte[] body;
    private int index;

    public EncodedChunk(byte[] body) {
        this(body, 0);
    }

    private EncodedChunk(byte[] body, int index) {
        this.body = body;
        this.index = index;
    }

    public byte[] read(int bufferLength) {
        byte[] buffer = new byte[bufferLength];
        for (int i = 0; i < bufferLength; i++) {
            buffer[i] = (byte) (body[index++] & 0xff);
        }

        return buffer;
    }

    protected byte[] getBody() {

        return body;
    }

    public void skip(int skipOver) {

        index += skipOver;
    }

    public byte[] readRemaining() {

        return read(body.length - index);
    }

    public int getIndex() {
        return index;
    }

    public boolean endOfBuffer() {

        return getBody().length <= index;
    }

    private int read() {

        return body[index++] & 0xff;
    }

    public byte extractU1() {

        return (byte) read();
    }

    public short extractU2() {
        int value = extractU1();

        for (int cursor = 1; cursor < 2; cursor++) {
            value = (value << 8) | (short) read();
        }

        return (short) value;
    }

    public int extractU4() {
        int value = extractU1();
        for (int cursor = 1; cursor < 4; cursor++) {
            value = (value << 8) | read();
        }

        return value;
    }

    public long extractU8() {
        long value = extractU1();
        for (int cursor = 1; cursor < 8; cursor++) {
            value = (value << 8) | (long) read();
        }

        return value;
    }

    public long extractID() {

        return extractU8();
    }

    public boolean extractBoolean() {

        return extractU1() != 0;
    }

    public char extractChar() {

        return (char) extractU2();
    }

    public byte extractByte() {

        return extractU1();
    }

    public short extractShort() {

        return extractU2();
    }

    public float extractFloat() {

        return Float.intBitsToFloat(extractInt());
    }

    public int extractInt() {

        return extractU4();
    }

    public double extractDouble() {

        return Double.longBitsToDouble(extractLong());
    }

    public long extractLong() {
        return extractU8();
    }

    public BasicDataTypeValue extractBasicType(BasicDataTypes basicType) {

        switch (basicType) {
            case BOOLEAN:

                return new BooleanValue(this.extractBoolean());
            case CHAR:

                return new CharValue(this.extractChar());
            case BYTE:

                return new ByteValue(this.extractByte());
            case SHORT:

                return new ShortValue(this.extractShort());
            case FLOAT:

                return new FloatValue(this.extractFloat());
            case INT:

                return new IntValue(this.extractInt());
            case OBJECT:

                return new ObjectValue(this.extractID());
            case DOUBLE:

                return new DoubleValue(this.extractDouble());
            case LONG:

                return new LongValue(this.extractLong());
            default:

                return new UnknownValue();
        }
    }

    public BasicDataTypeValue extractBasicType(int basicType) {
        return extractBasicType(BasicDataTypes.fromInt(basicType));
    }

    /**
     * Useful when you want to read some bytes without affecting the internal cursor position.
     *
     * @return A chunk with the same byte array and offset as this object.
     */
    public EncodedChunk copy() {

        return new EncodedChunk(body, index);
    }

    /*
    Debugging aid
     */
    public void dump(PrintStream out) {
        int max = (body.length > 1000) ? 1000 : body.length;
        IntStream.range(0, max)
                .forEach(cursor -> {
                    System.out.print(Integer.toHexString(body[cursor] & 255));
                    System.out.print(" ");
                });
        out.println();
        IntStream.range(0, max)
                .forEach(cursor -> {
                    System.out.print((char) (body[cursor] & 255));
                    System.out.print(" ");
                });
        out.println();
    }
}
