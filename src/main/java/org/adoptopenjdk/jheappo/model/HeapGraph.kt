package org.adoptopenjdk.jheappo.model

import org.adoptopenjdk.jheappo.heapdump.*
import org.adoptopenjdk.jheappo.io.HeapProfile
import org.adoptopenjdk.jheappo.model.HeapGraphExtras.*
import org.adoptopenjdk.jheappo.objects.*
import org.neo4j.graphdb.*
import org.neo4j.graphdb.factory.GraphDatabaseFactory
import org.neo4j.io.fs.FileUtils

import java.io.*
import java.util.HashMap
import java.util.HashSet

class HeapGraph(private val path: File) {
    internal var stringTable = HashMap<Long, UTF8String>()
    internal var clazzTable = HashMap<Long, ClassObject>()
    internal var clazzNodes = HashMap<Long, Node>()
    internal var clazzNames = HashMap<Long, Long>()
    internal var instanceNodes = HashMap<Long, Node>()
    internal var oopTable = HashMap<Long, InstanceObject>()
    internal var loadClassTable = HashMap<Long, LoadClass>()
    internal var rootStickClass = HashSet<Long>()
    internal var rootJNIGlobal = HashMap<Long, Long>()
    internal var rootJNILocal = HashMap<Long, Long>()
    internal var rootMonitorUsed = HashSet<Long>()
    internal var rootJavaFrame = HashMap<Long, RootJavaFrame>()
    internal var rootThreadObject = HashMap<Long, RootThreadObject>()
    internal var primitiveArray = HashMap<Long, PrimitiveArray>()
    internal var objectArray = HashMap<Long, ObjectArray>()

    fun populateFrom(heapDump: HeapProfile) {
        FileUtils.deleteRecursively(path)
        val db = GraphDatabaseFactory().newEmbeddedDatabase(path)
        createIndexes(db)
        var count = 0
        var tx: Transaction? = db.beginTx()
        try {
            val header = heapDump.readHeader()
            println("Header: $header")
            while (!heapDump.isAtHeapDumpEnd) {
                when (val frame = heapDump.extract()) {
                    is StackFrame -> { }
                    is StackTrace -> { }
                    is UTF8StringSegment -> {
                        val string = frame.toUtf8String()
                        stringTable[string.id] = string
                        // out.write(Long.toString(string.getId()) + "->" + string.getString() + "\n");
                    }
                    is LoadClass -> // loadClassTable.put(((LoadClass) frame).getClassObjectID(), (LoadClass) frame); //store mapping of class to class name.
                        // out.write(frame.toString() + "\n");
                        clazzNames[frame.classObjectID] = frame.classNameStringID
                    is HeapDumpSegment -> while (!frame.hasNext()) {
                        val heapObject = frame.next()
                        if (heapObject == null) {
                            println("parser error resolving type in HeapDumpSegment....")
                            continue
                        }
                        when (heapObject) {
                            is ClassObject -> {
                                clazzTable[heapObject.id] = heapObject
                                // clazzFile.write(heapObject.toString() + "\n");
                                val node = mergeNode(db, clazzNodes, Labels.Class, heapObject.id)
                                count++
                                node.setProperty("name", className(heapObject.id))
                                node.setProperty("size", heapObject.instanceSizeInBytes)
                                for (i in 0 until heapObject.fieldNamesIndicies.size) {
                                    val index = heapObject.fieldNamesIndicies[i]
                                    val type = heapObject.fieldTypes[i]
                                    // todo string resolution
                                    node.setProperty(fieldName(index), type)
                                }
                                val parent = mergeNode(db, clazzNodes, Labels.Class, heapObject.superClassObjectID)
                                count++
                                node.createRelationshipTo(parent, Relationships.SUPERCLASS)
                                count++
                            }
                            is InstanceObject -> {
                                heapObject.inflate(this.getClazzById(heapObject.classObjectID()))
                                // oopTable.put(heapObject.getId(), instanceObject);
                                // instanceFile.write(heapObject.toString() + "\n");
                                val node = mergeNode(db, instanceNodes, Labels.Instance, heapObject.id)
                                count++
                                node.setProperty("stackSerial", heapObject.stackTraceSerialNumber())
                                val classObject = getClazzById(heapObject.classObjectID())
                                val classNode = mergeNode(db, clazzNodes, Labels.Class, heapObject.classObjectID())
                                count++
                                node.createRelationshipTo(classNode, Relationships.IS_CLASS)
                                count++
                                for (i in 0 until classObject.fieldNamesIndicies.size) {
                                    val index = classObject.fieldNamesIndicies[i]
                                    when (val value = heapObject.instanceFieldValues()[i]) {
                                        is ObjectValue -> {
                                            val other = mergeNode(db, instanceNodes, Labels.Instance, value.objectId)
                                            count++
                                            val rel = node.createRelationshipTo(other, Relationships.CONTAINS)
                                            count++
                                            rel.setProperty("name", fieldName(index))
                                        }
                                        is PrimitiveValue<*> ->
                                            node.setProperty(fieldName(index), value.value) // todo type + value
                                        is ArrayValue -> {
                                        }
                                        UnknownValue -> {
                                        }
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
                    else -> {
                        //                    System.out.println("missed : " + frame.toString());
                    }
                }
                if (count > BATCH_SIZE) {
                    tx!!.success()
                    tx.close()
                    tx = db.beginTx()
                    count = 0
                }
            }
        } finally {
            if (tx != null) {
                tx.success()
                tx.close()
            }
            db.shutdown()
        }
    }

    private fun className(id: Long): String {
        return stringTable[clazzNames[id]]!!.string
    }

    private fun fieldName(index: Long): String {
        return "_" + stringTable[index]!!.string
    }

    private fun mergeNode(db: GraphDatabaseService, cache: HashMap<Long, Node>, type: Labels, objectId: Long): Node {
        return cache.computeIfAbsent(objectId) { id ->
            val n = db.createNode(type)
            n.setProperty("id", id)
            n
        }
    }

    private fun createIndexes(db: GraphDatabaseService) {
        db.beginTx().use { tx ->
            db.schema().constraintFor(Labels.Instance).assertPropertyIsUnique("id").create()
            db.schema().constraintFor(Labels.Class).assertPropertyIsUnique("id").create()
            db.schema().indexFor(Labels.Class).on("name").create()
            tx.success()
        }
    }

    fun getClazzById(cid: Long): ClassObject {
        return clazzTable.getValue(cid)
    }

    fun writeTo(out: PrintStream) {
        //todo output data to stdout...
    }

    fun addInstanceObject(instanceObject: InstanceObject) {
        oopTable[instanceObject.id] = instanceObject
    }

    fun getInstanceObject(id: Long): InstanceObject {
        return oopTable.getValue(id)
    }

    companion object {

        private val BATCH_SIZE = 50000
    }
}
