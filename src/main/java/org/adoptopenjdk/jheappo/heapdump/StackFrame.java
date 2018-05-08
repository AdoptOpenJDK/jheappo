package org.adoptopenjdk.jheappo.heapdump;

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

    long stackFrameID;
    long methodNameStringID;
    long methodSignatureStringID;
    long sourceFileNameStringID;
    long classSerialNumber;

    public StackFrame(byte[] body) {
        super(body);
        stackFrameID = extractID();
        methodNameStringID = extractID();
        methodSignatureStringID = extractID();
        sourceFileNameStringID = extractID();
        classSerialNumber = extractID();
    }

    public String toString() {
        return "StackFrame --> " + stackFrameID + ":" + methodNameStringID + ":" + methodSignatureStringID + ":" + sourceFileNameStringID + ":" + classSerialNumber;
    }
}
