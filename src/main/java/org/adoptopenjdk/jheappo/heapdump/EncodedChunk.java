package org.adoptopenjdk.jheappo.heapdump;

import java.io.PrintStream;
import org.adoptopenjdk.jheappo.model.BasicDataTypeValue;
import org.adoptopenjdk.jheappo.objects.BasicDataTypes;

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
        return read( body.length - index);
    }
    public int getIndex() { return index; }

    public boolean endOfBuffer() {  return getBody().length <= index; }

    private int read() { return body[index++] & 0xff; }

    public byte extractU1() {
        return (byte)read();
    }

    public short extractU2() {
        int value = extractU1();
        for (int cursor = 1; cursor < 2; cursor++) {
            value = (value << 8) | (short)read();
        }
        return (short)value;
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
            value = (value << 8) | (long)read();
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
        return (char)extractU2();
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
            case BOOLEAN :
                return new BasicDataTypeValue(this.extractBoolean(),BasicDataTypes.BOOLEAN);
            case CHAR :
                return new BasicDataTypeValue(this.extractChar(),BasicDataTypes.CHAR);
            case BYTE :
                return new BasicDataTypeValue(this.extractByte(),BasicDataTypes.BYTE);
            case SHORT:
                return new BasicDataTypeValue(this.extractShort(),BasicDataTypes.SHORT);
            case FLOAT :
                return new BasicDataTypeValue(this.extractFloat(),BasicDataTypes.FLOAT);
            case INT :
                return new BasicDataTypeValue(this.extractInt(),BasicDataTypes.INT);
            case OBJECT :
                return new BasicDataTypeValue(this.extractID(),BasicDataTypes.OBJECT);
            case DOUBLE :
                return new BasicDataTypeValue(this.extractDouble(),BasicDataTypes.DOUBLE);
            case LONG:
                return new BasicDataTypeValue(this.extractLong(),BasicDataTypes.LONG);
            default:
                return new BasicDataTypeValue(null,BasicDataTypes.UNKNOWN);
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
        for( int cursor = 0; cursor < max; cursor++) {
            System.out.print(Integer.toHexString(body[cursor] & 255));
            System.out.print( " ");
        }
        out.println("");
        for( int cursor = 0; cursor < max; cursor++) {
            System.out.print((char)(body[cursor] & 255));
            System.out.print( " ");
        }
        out.println("");
    }

}
