package org.adoptopenjdk.jheappo.io;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

import java.io.PrintStream;

public abstract class HeapDumpBuffer {

    private static final int U1 = 1;
    private static final int U2 = 2;
    private static final int U4 = 4;
    private static final int ID = 8;

    private byte[] body;
    private int index = 0;

    public HeapDumpBuffer(byte[] body) {
        this.body = body;
    }

    public boolean endOfBuffer() {
        return getBody().length <= index;
    }

    public long extractU8() {
        long value = 0;
        for ( int i = index; i < index + 8; i++) {
            value = (value << 8) | (body[i] & 0xff);
        }
        index += 8;
        return value;
    }

    public int extractU4() {
        int value = 0;
        for ( int i = index; i < index + 4; i++) {
            value = (value << 8) | (body[i] & 0xff);
        }
        index += 4;
        return value;
    }

    public int extractU2() {
        int value = 0;
        for ( int i = index; i < index + 2; i++) {
            value = (value << 8) | (body[i] & 0xff);
        }
        index += 2;
        return value;
    }

    public int extractU1() {
        return body[index++] & 0xff;
    }

    public long extractID() {
        return extractU8();
    }

    public void dump(PrintStream out) {
        out.println(body); //todo make this all abstract
    }

    protected byte[] getBody() {
        return body;
    }

    public boolean extractBoolean() {
        return body[index++] != 0;
    }

    public char extractChar() {
        return (char)(((body[index++] & 0xff) <<8) | (body[index++] & 0xff));
    }

    public byte extractByte() {
        return (byte)(body[index++] & 0xff);
    }

    public short extractShort() {
        return (short)((body[index++]<<8) | (body[index++] & 0xff));
    }

    public float extractFloat() {
        return Float.intBitsToFloat(extractInt());
    }

    public int extractInt() {
        int value = 0;
        for ( int pos = 0; pos < 4; pos++)
            value = (value << 8) | ((body[index++] & 0xff));
        return value;
    }

    public double extractDouble() {
        return Double.longBitsToDouble(extractLong());
    }

    public long extractLong() {
        long value = 0;
        for ( int pos = 0; pos < 8; pos++)
            value = (value << 8) |  (long)(body[index++] & 0xff) ;
        return value;
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
}
