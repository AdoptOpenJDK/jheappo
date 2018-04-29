package org.adoptopenjdk.jheappo.io;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

import java.io.PrintStream;

public class HeapDumpBuffer {

    private byte[] body;
    private int index = 0;

    public HeapDumpBuffer(byte[] body) {
        this.body = body;
    }

    public int getIndex() { return index; }

    public boolean endOfBuffer() {
        return getBody().length <= index;
    }

    private int read() {
        return body[index++] & 0xff;
    }

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

    protected byte[] getBody() {
        return body;
    }

    public boolean extractBoolean() {
        return extractU1() != 0;
    }

    public char extractChar() {
        return (char)extractU2();
    }

    public byte extractByte() {
        return (byte)extractU1();
    }

    public short extractShort() {
        return (short)extractU2();
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

    public byte[] read(int bufferLength) {
        byte[] buffer = new byte[bufferLength];
        for (int i = 0; i < bufferLength; i++) {
            buffer[i] = (byte) (body[index++] & 0xff);
        }
        return buffer;
    }

    public void skip(int skipOver) {
        index += skipOver;
    }

    public byte[] readRemaining() {
        return read( body.length - index);
    }

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
