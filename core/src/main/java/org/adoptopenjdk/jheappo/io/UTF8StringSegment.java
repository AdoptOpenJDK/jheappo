package org.adoptopenjdk.jheappo.io;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

import org.adoptopenjdk.jheappo.io.HeapProfileRecord;
import org.adoptopenjdk.jheappo.objects.UTF8String;

public class UTF8StringSegment extends HeapProfileRecord {

    /*
        ID     | ID for this string
        [u1]*  | UTF8 characters for string (NOT NULL terminated)
     */

    public final static int TAG = 0x01;

    private final EncodedChunk body;

    public UTF8StringSegment(EncodedChunk body) {
        this.body = body;
    }

    public UTF8String toUtf8String() {
        // defer UTF8 parsing
        return new UTF8String(body.copy());
    }
}
