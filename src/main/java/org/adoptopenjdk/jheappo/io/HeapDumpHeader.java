package org.adoptopenjdk.jheappo.io;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

import java.io.DataInputStream;
import java.io.IOException;

public class HeapDumpHeader {
    private static String SUPPORTED_VERSIONS[] = { "JAVA PROFILE 1.0.1", "JAVA PROFILE 1.0.2" };
    private static int[] SUPPORTED_IDENIFIER_SIZE = { 4, 8};

    private String heapDumpVersion;
    private int sizeOfIdentifiers = 0;    /* u4 is unsigned 4 bytes.. which in this case is ok to assigned to signed int */
    private long millisecSinceEPOC;

    HeapDumpHeader() {}

    private String extractVersionString( DataInputStream buffer) throws IOException {
        char[] string = new char[1024];
        int pos = 0;
        int value;
        while ( (value = buffer.read()) > 0)
            string[pos++] = (char)value;
        if ( value < 0)
            throw new IOException("Unexpected EOF");
        return new String(string).trim();
    }

    public void extract( DataInputStream buffer) throws IOException {
        heapDumpVersion = extractVersionString(buffer);
        if ( ! (SUPPORTED_VERSIONS[0].equals(heapDumpVersion) || (SUPPORTED_VERSIONS[1]).equals(heapDumpVersion)))
            throw new IOException(heapDumpVersion + " is not supported");
        sizeOfIdentifiers = buffer.readInt();
        if ( sizeOfIdentifiers != SUPPORTED_IDENIFIER_SIZE[1] && sizeOfIdentifiers != SUPPORTED_IDENIFIER_SIZE[0])
            throw new IOException("Unsupported identifier size " + sizeOfIdentifiers);
        millisecSinceEPOC = buffer.readLong();
    }

    public String toString() {
        return heapDumpVersion + " : " + sizeOfIdentifiers + " : " + millisecSinceEPOC;
    }
}
