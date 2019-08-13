package org.adoptopenjdk.jheappo.model;


import org.adoptopenjdk.jheappo.io.*;
import org.adoptopenjdk.jheappo.model.HeapGraphExtras.*;
import org.adoptopenjdk.jheappo.objects.*;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.io.fs.FileUtils;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;

public class HeapGraph {

    private static final int BATCH_SIZE = 50000;
    private final File path;

    private HashMap<Long, UTF8String> stringTable = new HashMap<>();
    private HashMap<Long, ClassObject> clazzTable = new HashMap<>();
    private HashMap<Long, Node> clazzNodes = new HashMap<>();
    private HashMap<Long, Long> clazzNames = new HashMap<>();
    private HashMap<Long, Node> instanceNodes = new HashMap<>();
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

    public HeapGraph(File path) {
        this.path = path;
    }

    public void populateFrom(HeapProfile heapDump) throws IOException {
        FileUtils.deleteRecursively(path);

        var db = new GraphDatabaseFactory().newEmbeddedDatabase(path);

        createIndexes(db);
        int count = 0;
        Transaction tx = db.beginTx();

        try {
            HeapProfileHeader header = heapDump.readHeader();
            System.out.println("Header: " + header.toString());

            while (!heapDump.isAtHeapDumpEnd()) {
                HeapProfileRecord frame = heapDump.extract();


                if (frame instanceof StackFrame) {
                } else if (frame instanceof StackTrace) {
                } else if (frame instanceof UTF8StringSegment) {
                    var string = (((UTF8StringSegment) frame).toUtf8String());
                    stringTable.put(string.getId(), string);
                    // out.write(Long.toString(string.getId()) + "->" + string.getString() + "\n");
                } else if (frame instanceof LoadClass) {
                    LoadClass loadClass = (LoadClass) frame;
                    // loadClassTable.put(((LoadClass) frame).getClassObjectID(), (LoadClass) frame); //store mapping of class to class name.
                    // out.write(frame.toString() + "\n");
                    clazzNames.put(loadClass.getClassObjectID(), loadClass.classNameStringID());
                } else if (frame instanceof HeapDumpSegment) {
                    while (!((HeapDumpSegment) frame).hasNext()) {
                        var heapObject = ((HeapDumpSegment) frame).next();
                        if (heapObject == null) {
                            System.out.println("parser error resolving type in HeapDumpSegment....");
                            continue;
                        }
                        if (heapObject instanceof ClassObject) {
                            var classObject = (ClassObject) heapObject;
                            clazzTable.put(heapObject.getId(), classObject);
                            // clazzFile.write(heapObject.toString() + "\n");
                            Node node = mergeNode(db, clazzNodes, Labels.Class, heapObject.getId());
                            count++;
                            node.setProperty("name", className(classObject.getId()));
                            node.setProperty("size", classObject.instanceSizeInBytes());

                            for (int i = 0; i < classObject.fieldNameIndicies().length; i++) {
                                long index = classObject.fieldNameIndicies()[i];
                                int type = classObject.fieldTypes()[i];
                                // todo string resolution
                                node.setProperty(fieldName(index), type);
                            }
                            Node parent = mergeNode(db, clazzNodes, Labels.Class, classObject.superClassObjectID());
                            count++;
                            node.createRelationshipTo(parent, Relationships.SUPERCLASS);
                            count++;
                        } else if (heapObject instanceof InstanceObject) {
                            var instanceObject = (InstanceObject) heapObject;
                            instanceObject.inflate(this.getClazzById(instanceObject.classObjectID()));
                            // oopTable.put(heapObject.getId(), instanceObject);
                            // instanceFile.write(heapObject.toString() + "\n");
                            Node node = mergeNode(db, instanceNodes, Labels.Instance, heapObject.getId());
                            count++;
                            node.setProperty("stackSerial", instanceObject.stackTraceSerialNumber());
                            ClassObject classObject = getClazzById(instanceObject.classObjectID());
                            Node classNode = mergeNode(db, clazzNodes, Labels.Class, instanceObject.classObjectID());
                            count++;
                            node.createRelationshipTo(classNode, HeapGraphExtras.Relationships.IS_CLASS);
                            count++;

                            for (int i = 0; i < classObject.fieldNameIndicies().length; i++) {
                                long index = classObject.fieldNameIndicies()[i];
                                BasicDataTypeValue type = instanceObject.instanceFieldValues()[i];
                                switch (type.type) {
                                    case OBJECT:
                                        Node other = mergeNode(db, instanceNodes, Labels.Instance, (Long) type.value);
                                        count++;
                                        Relationship rel = node.createRelationshipTo(other, Relationships.CONTAINS);
                                        count++;
                                        rel.setProperty("name", fieldName(index));
                                        break;
                                    case BOOLEAN:
                                    case CHAR:
                                    case FLOAT:
                                    case DOUBLE:
                                    case BYTE:
                                    case SHORT:
                                    case INT:
                                    case LONG:
                                        node.setProperty(fieldName(index), type.value); // todo type + value
                                        break;
                                    case ARRAY:
                                        break;
                                    case UNKNOWN:
                                        break;
                                }
                            }
                        }
/*
                        else if (heapObject instanceof RootJNIGlobal) {
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
                        } else
                            System.out.println("missed : " + heapObject.toString());
*/
                    }
                } else {
//                    System.out.println("missed : " + frame.toString());
                }
                if (count > BATCH_SIZE) {
                    tx.success();
                    tx.close();
                    tx = db.beginTx();
                    count = 0;
                }
            }
        } finally {
            if (tx != null) {
                tx.success();
                tx.close();
            }
            db.shutdown();
        }
    }

    private String className(long id) {

        return stringTable.get(clazzNames.get(id)).getString();
    }

    private String fieldName(long index) {

        return "_" + stringTable.get(index).getString();
    }

    private Node mergeNode(GraphDatabaseService db, HashMap<Long, Node> cache, Labels type, long objectId) {

        return cache.computeIfAbsent(objectId, (id) -> {
            Node n = db.createNode(type);
            n.setProperty("id", id);
            return n;
        });
    }

    private void createIndexes(GraphDatabaseService db) {
        try (Transaction tx = db.beginTx()) {
            db.schema().constraintFor(Labels.Instance).assertPropertyIsUnique("id").create();
            db.schema().constraintFor(Labels.Class).assertPropertyIsUnique("id").create();
            db.schema().indexFor(Labels.Class).on("name").create();
            tx.success();
        }
    }

    public ClassObject getClazzById(long cid) {

        return clazzTable.get(cid);
    }

    public void writeTo(PrintStream out) {
        //todo output data to stdout...
    }

    public void addInstanceObject(InstanceObject instanceObject) {
        oopTable.put(instanceObject.getId(), instanceObject);
    }

    public InstanceObject getInstanceObject(long id) {
        return oopTable.get(id);
    }
}
