package com.kodewerk.jheappo.heapdump;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */


import com.kodewerk.jheappo.io.HeapDumpBuffer;

public class UTF8String extends HeapDumpBuffer {

    /*
        ID     | ID for this string
        [u1]*  | UTF8 characters for string (NOT NULL terminated)
     */

    public final static int TAG = 0x01;

    private long id;

    public UTF8String(byte[] body) {
        super(body);
        this.id = extractID();
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return new String(getBody()).substring(8);
    }

    public String toString() {
        return "UTF8_String --> " + getId() + "-->" + getContent();
    }
}
