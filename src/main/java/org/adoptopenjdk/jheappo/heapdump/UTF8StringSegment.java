package org.adoptopenjdk.jheappo.heapdump;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */


import org.adoptopenjdk.jheappo.io.HeapProfileRecord;

public class UTF8StringSegment extends HeapProfileRecord {

    /*
        ID     | ID for this string
        [u1]*  | UTF8 characters for string (NOT NULL terminated)
     */

    public final static int TAG = 0x01;

//    private long id;
//    private String string;

    public UTF8StringSegment(byte[] body) {
        super(body);
//        this.id = extractID();
//        this.string = new String(getBody()).substring(8);
    }

//    public long getId() {
//        return id;
//    }

//    public String getString() {
//        return string;
//    }

//    public String toString() {
//        return "UTF8_String --> " + getId() + "-->" + getString();
//    }
}
