package org.adoptopenjdk.jheappo.model;

import org.adoptopenjdk.jheappo.heapdump.*;
import org.adoptopenjdk.jheappo.io.HeapDump;
import org.adoptopenjdk.jheappo.io.HeapDumpBuffer;
import org.adoptopenjdk.jheappo.io.HeapDumpHeader;
import org.adoptopenjdk.jheappo.objects.*;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;

public class JavaHeap {

    HashMap<Long,UTF8String> stringTable = new HashMap<>();
    HashMap<Long,ClassObject> clazzTable = new HashMap<>();
    HashMap<Long,InstanceObject> oopTable = new HashMap<>();

    public JavaHeap() {}

    public void populateFrom(HeapDump heapDump) throws IOException {
        heapDump.open();
        HeapDumpHeader header = heapDump.readHeader();
        System.out.println("Header: " + header.toString());
        while (! heapDump.isAtHeapDumpEnd()) {
            HeapDumpBuffer frame = heapDump.extract();
            if (frame instanceof StackFrame) {
            }
            else if (frame instanceof StackTrace) {
            }
            else if (frame instanceof UTF8StringSegment) {
                UTF8String string = new UTF8String(frame);
                stringTable.put(string.getId(), string);
            }
            else if (frame instanceof LoadClass) {
            }
            else if (frame instanceof HeapDumpSegment) {
                while (!frame.endOfBuffer()) {
                    HeapObject heapObject = ((HeapDumpSegment) frame).next();
                    if ( heapObject == null) {
                        System.out.println("parser error resolving type in HeapDumpSegment....");
                        continue;
                    }
                    if (heapObject instanceof ClassObject) {
                        clazzTable.put(heapObject.getId(), (ClassObject) heapObject);
                        long[] fieldNameIndexes = ((ClassObject)heapObject).fieldNameIndicies();
                    } else if (heapObject instanceof InstanceObject) {
                        ((InstanceObject) heapObject).inflate(this);
                        oopTable.put(heapObject.getId(),(InstanceObject)heapObject);
                    } else if (heapObject instanceof RootJNIGlobal) {
                    } else if (heapObject instanceof PrimitiveArray) {
                    } else if (heapObject instanceof ObjectArray) {
                    } else if ( heapObject instanceof RootJavaFrame) {
                    }
                    else if ( heapObject instanceof RootThreadObject) {
                    }
                    else if (heapObject instanceof RootJNILocal) {
                    }
                    else if ( heapObject instanceof RootMonitorUsed) {
                    }
                    else if (heapObject instanceof RootStickyClass) {
                    }
                    else
                        System.out.println("missed : " + heapObject.toString());
                }
            } else
                System.out.println("missed : " + frame.toString());
        }
    }

    public ClassObject getClazzById(long cid) {
        return clazzTable.get(cid);
    }

    public void writeTo(PrintStream out) {
        //todo output data to stdout...
    }
}
