package org.adoptopenjdk.jheappo.model;

import org.adoptopenjdk.jheappo.heapdump.*;
import org.adoptopenjdk.jheappo.io.HeapProfile;
import org.adoptopenjdk.jheappo.io.HeapProfileRecord;
import org.adoptopenjdk.jheappo.io.HeapProfileHeader;
import org.adoptopenjdk.jheappo.objects.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;

public class JavaHeap {

    HashMap<Long,UTF8String> stringTable = new HashMap<>();
    HashMap<Long,ClassObject> clazzTable = new HashMap<>();
    HashMap<Long,InstanceObject> oopTable = new HashMap<>();
    HashMap<Long,LoadClass> loadClassTable = new HashMap<>();
    HashSet<Long> rootStickClass = new HashSet<>();
    HashMap<Long,Long> rootJNIGlobal = new HashMap<>();
    HashMap<Long,Long> rootJNILocal = new HashMap<>();
    HashSet<Long> rootMonitorUsed = new HashSet<>();
    HashMap<Long,RootJavaFrame> rootJavaFrame = new HashMap<>();
    HashMap<Long,RootThreadObject> rootThreadObject = new HashMap<>();
    HashMap<Long,PrimitiveArray> primitiveArray = new HashMap<>();
    HashMap<Long,ObjectArray> objectArray = new HashMap<>();

    public JavaHeap() {}

    public void populateFrom(HeapProfile heapDump) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter("string.table"));
        BufferedWriter clazzFile = new BufferedWriter(new FileWriter("class.table"));
        BufferedWriter instanceFile = new BufferedWriter(new FileWriter("instance.table"));
        BufferedWriter loadClassFile = new BufferedWriter(new FileWriter("loadClass.table"));
        heapDump.open();
        HeapProfileHeader header = heapDump.readHeader();
        System.out.println("Header: " + header.toString());
        while (! heapDump.isAtHeapDumpEnd()) {
            HeapProfileRecord frame = heapDump.extract();
            if (frame instanceof StackFrame) {
            }
            else if (frame instanceof StackTrace) {
            }
            else if (frame instanceof UTF8StringSegment) {
                UTF8String string = new UTF8String(frame);
                stringTable.put(string.getId(), string);
                out.write(Long.toString(string.getId()) + "->" + string.getString() + "\n");
            }
            else if (frame instanceof LoadClass) {
                loadClassTable.put(((LoadClass)frame).getClassObjectID(),(LoadClass)frame); //store mapping of class to class name.
                out.write(frame.toString() + "\n");
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
                        clazzFile.write(heapObject.toString() + "\n");
                    }
                    else if (heapObject instanceof InstanceObject) {
                        InstanceObject instanceObject = (InstanceObject) heapObject;
                        instanceObject.inflate(this.getClazzById(instanceObject.classObjectID()));
                        oopTable.put(heapObject.getId(), instanceObject);
                        instanceFile.write(heapObject.toString() + "\n");
                    }
                    else if (heapObject instanceof RootJNIGlobal) {
                        rootJNIGlobal.put(heapObject.getId(),((RootJNIGlobal) heapObject).getJNIGlobalRefID());
                    }
                    else if (heapObject instanceof RootJNILocal) {
                        rootJNILocal.put(heapObject.getId(),((RootJNILocal) heapObject).getId());
                    }
                    else if (heapObject instanceof PrimitiveArray) {
                        primitiveArray.put(heapObject.getId(),(PrimitiveArray)heapObject);
                    }
                    else if (heapObject instanceof ObjectArray) {
                        objectArray.put(heapObject.getId(),(ObjectArray)heapObject);
                    }
                    else if ( heapObject instanceof RootJavaFrame) {
                        rootJavaFrame.put(heapObject.getId(),(RootJavaFrame) heapObject);
                    }
                    else if ( heapObject instanceof RootThreadObject) {
                        rootThreadObject.put(heapObject.getId(),(RootThreadObject) heapObject);
                    }
                    else if ( heapObject instanceof RootMonitorUsed) {
                        rootMonitorUsed.add(heapObject.getId());
                    }
                    else if (heapObject instanceof RootStickyClass) {
                        rootStickClass.add(heapObject.getId());
                    }
                    else
                        System.out.println("missed : " + heapObject.toString());
                }
            } else
                System.out.println("missed : " + frame.toString());
        }
        out.close();
        clazzFile.close();
        instanceFile.close();
        loadClassFile.close();
    }

    public ClassObject getClazzById(long cid) {
        return clazzTable.get(cid);
    }

    public void writeTo(PrintStream out) {
        //todo output data to stdout...
    }

    public void addInstanceObject(InstanceObject instanceObject) {
        oopTable.put(instanceObject.getId(),instanceObject);
    }

    public InstanceObject getInstanceObject(long id) {
        return oopTable.get(id);
    }
}
