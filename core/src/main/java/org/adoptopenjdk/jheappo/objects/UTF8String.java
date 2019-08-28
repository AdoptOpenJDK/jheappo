package org.adoptopenjdk.jheappo.objects;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */


import org.adoptopenjdk.jheappo.io.EncodedChunk;

public class UTF8String extends HeapObject {

    private String string;

    public UTF8String(EncodedChunk buffer) {
        super(buffer);
        this.string = new String(buffer.readRemaining());
    }

    public String getString() {

        return string;
    }

    public String toString() {

        return getId() + " : " + string;
    }
}
