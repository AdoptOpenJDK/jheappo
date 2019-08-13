package org.adoptopenjdk.jheappo.model;

import org.adoptopenjdk.jheappo.io.*;
import org.adoptopenjdk.jheappo.objects.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;

public class JavaHeap {

    private HashMap<Long, UTF8String> stringTable = new HashMap<>();
    private HashMap<Long, ClassObject> clazzTable = new HashMap<>();
    private HashMap<Long, InstanceObject> oopTable = new HashMap<>();
    private HashMap<Long, LoadClass> loadClassTable = new HashMap<>();
    private HashSet<Long> rootStickClass = new HashSet<>();
    private HashMap<Long, Long> rootJNIGlobal = new HashMap<>();
    private HashMap<Long, Long> rootJNILocal = new HashMap<>();
    private HashSet<Long> rootMonitorUsed = new HashSet<>();
    private HashMap<Long, RootJavaFrame> rootJavaFrame = new HashMap<>();
    private HashMap<Long, RootThreadObject> rootThreadObject = new HashMap<>();
    private HashMap<Long, PrimitiveArray> primitiveArray = new HashMap<>();
    private HashMap<Long, ObjectArray> objectArray = new HashMap<>();

    private final Path outputDir;

    public JavaHeap(Path outputDir) {
        this.outputDir = outputDir;
    }

    public void populateFrom(HeapProfile heapDump) throws IOException {

        try (BufferedWriter out = Files.newBufferedWriter(outputDir.resolve("string.table"));
             BufferedWriter clazzFile = Files.newBufferedWriter(outputDir.resolve("class.table"));
             BufferedWriter instanceFile = Files.newBufferedWriter(outputDir.resolve("instance.table"));
             BufferedWriter loadClassFile = Files.newBufferedWriter(outputDir.resolve("loadClass.table"));
        ) {

            HeapProfileHeader header = heapDump.readHeader();
            System.out.println("Header: " + header.toString());

            while (!heapDump.isAtHeapDumpEnd()) {
                HeapProfileRecord frame = heapDump.extract();
                if (frame instanceof StackFrame) {
                    // Do Nothing
                } else if (frame instanceof StackTrace) {
                    // Do Nothing
                } else if (frame instanceof UTF8StringSegment) {
                    var string = ((UTF8StringSegment) frame).toUtf8String();
                    stringTable.put(string.getId(), string);
                    out.write(Long.toString(string.getId()) + "->" + string.getString() + "\n");
                } else if (frame instanceof LoadClass) {
                    loadClassTable.put(((LoadClass) frame).getClassObjectID(), (LoadClass) frame); //store mapping of class to class name.
                    out.write(frame.toString() + "\n");
                } else if (frame instanceof HeapDumpSegment) {
                    while (!((HeapDumpSegment) frame).hasNext()) {
                        HeapObject heapObject = ((HeapDumpSegment) frame).next();
                        if (heapObject == null) {
                            System.out.println("parser error resolving type in HeapDumpSegment....");
                            continue;
                        }

                        if (heapObject instanceof ClassObject) {
                            clazzTable.put(heapObject.getId(), (ClassObject) heapObject);
                            clazzFile.write(heapObject.toString() + "\n");
                        } else if (heapObject instanceof InstanceObject) {
                            var instanceObject = (InstanceObject) heapObject;
                            instanceObject.inflate(this.getClazzById(instanceObject.classObjectID()));
                            oopTable.put(heapObject.getId(), instanceObject);
                            instanceFile.write(heapObject.toString() + "\n");
                        } else if (heapObject instanceof RootJNIGlobal) {
                            rootJNIGlobal.put(heapObject.getId(), ((RootJNIGlobal) heapObject).getJNIGlobalRefID());
                        } else if (heapObject instanceof RootJNILocal) {
                            rootJNILocal.put(heapObject.getId(), ((RootJNILocal) heapObject).getId());
                        } else if (heapObject instanceof PrimitiveArray) {
                            primitiveArray.put(heapObject.getId(), (PrimitiveArray) heapObject);
                        } else if (heapObject instanceof ObjectArray) {
                            objectArray.put(heapObject.getId(), (ObjectArray) heapObject);
                        } else if (heapObject instanceof RootJavaFrame) {
                            rootJavaFrame.put(heapObject.getId(), (RootJavaFrame) heapObject);
                        } else if (heapObject instanceof RootThreadObject) {
                            rootThreadObject.put(heapObject.getId(), (RootThreadObject) heapObject);
                        } else if (heapObject instanceof RootMonitorUsed) {
                            rootMonitorUsed.add(heapObject.getId());
                        } else if (heapObject instanceof RootStickyClass) {
                            rootStickClass.add(heapObject.getId());
                        } else {
                            System.out.println("missed : " + heapObject.toString());
                        }
                    }
                } else {
                    System.out.println("missed : " + frame.toString());
                }
            }
        }
    }

    public ClassObject getClazzById(long cid) {
        return clazzTable.get(cid);
    }

    public void writeTo(PrintStream out) {
        // TODO output data to stdout...
    }

    public void addInstanceObject(InstanceObject instanceObject) {
        oopTable.put(instanceObject.getId(), instanceObject);
    }

    public InstanceObject getInstanceObject(long id) {
        return oopTable.get(id);
    }
}
