package org.adoptopenjdk.jheappo.objects;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

import org.adoptopenjdk.jheappo.io.HeapDumpBuffer;

/*
    0x01  | ID      | object ID
          | ID      | JNI global ref ID
 */
public class RootJNIGlobal extends HeapObject {

    public static final int TAG = 0x01;

    private long jniGlobalRefID;

    public RootJNIGlobal(HeapDumpBuffer buffer) {
        super(buffer);
        jniGlobalRefID = buffer.extractID();
    }

    public String toString() {
        return "RootJNIGlobal : " + getId() + ":" + jniGlobalRefID;
    }
}
