package org.adoptopenjdk.jheappo.io;


/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */


import org.adoptopenjdk.jheappo.io.HeapProfileRecord;

    /*
        ID    | stack frame ID
        ID    | method name string ID
        ID    | method signature string ID
        ID    | source file name string ID
        u4    | class serial number
        u4    | > 0  | line number
              |   0  | no line information available
              |  -1  | unknown location
              |  -2  | compiled method (Not implemented)
              |  -3  | native method (Not implemented)
     */

public class StackFrame extends HeapProfileRecord {

    public final static int TAG = 0x04;

    private long stackFrameID;
    private long methodNameStringID;
    private long methodSignatureStringID;
    private long sourceFileNameStringID;
    private long classSerialNumber;

    public StackFrame(EncodedChunk body) {
        stackFrameID = body.extractID();
        methodNameStringID = body.extractID();
        methodSignatureStringID = body.extractID();
        sourceFileNameStringID = body.extractID();
        classSerialNumber = body.extractID();
    }

    public String toString() {
        return "StackFrame --> " + stackFrameID + ":" + methodNameStringID + ":" + methodSignatureStringID + ":" + sourceFileNameStringID + ":" + classSerialNumber;
    }
}
