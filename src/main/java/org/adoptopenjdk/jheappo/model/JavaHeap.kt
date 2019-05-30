package org.adoptopenjdk.jheappo.model

import org.adoptopenjdk.jheappo.heapdump.*
import org.adoptopenjdk.jheappo.io.HeapProfile
import org.adoptopenjdk.jheappo.io.HeapProfileRecord
import org.adoptopenjdk.jheappo.io.HeapProfileHeader
import org.adoptopenjdk.jheappo.objects.*

import java.io.BufferedWriter
import java.io.FileWriter
import java.io.IOException
import java.io.PrintStream
import java.util.HashMap
import java.util.HashSet

class JavaHeap {

    internal var stringTable = HashMap<Long, UTF8String>()
    internal var clazzTable = HashMap<Long, ClassObject>()
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
        BufferedWriter(FileWriter("string.table")).use { out ->
            BufferedWriter(FileWriter("class.table")).use { clazzFile ->
                BufferedWriter(FileWriter("instance.table")).use { instanceFile ->
                    BufferedWriter(FileWriter("loadClass.table")).use { loadClassFile ->
                        val header = heapDump.readHeader()
                        println("Header: $header")

                        while (!heapDump.isAtHeapDumpEnd) {
                            when (val frame = heapDump.extract()) {
                                is StackFrame -> {
                                    // Do Nothing
                                }
                                is StackTrace -> {
                                    // Do Nothing
                                }
                                is UTF8StringSegment -> {
                                    val string = frame.toUtf8String()
                                    stringTable[string.id] = string
                                    out.write(java.lang.Long.toString(string.id) + "->" + string.string + "\n")
                                }
                                is LoadClass -> {
                                    loadClassTable[frame.classObjectID] = frame //store mapping of class to class name.
                                    out.write(frame.toString() + "\n")
                                }
                                is HeapDumpSegment -> while (!frame.hasNext()) {
                                    val heapObject = frame.next()
                                    if (heapObject == null) {
                                        println("parser error resolving type in HeapDumpSegment....")
                                        continue
                                    }
                                    if (heapObject is ClassObject) {
                                        clazzTable[heapObject.id] = heapObject
                                        clazzFile.write(heapObject.toString() + "\n")
                                    } else if (heapObject is InstanceObject) {
                                        val instanceObject = heapObject as InstanceObject?
                                        instanceObject!!.inflate(this.getClazzById(instanceObject.classObjectID()))
                                        oopTable[heapObject.id] = instanceObject
                                        instanceFile.write(heapObject.toString() + "\n")
                                    } else if (heapObject is RootJNIGlobal) {
                                        rootJNIGlobal[heapObject.id] = heapObject.jniGlobalRefID
                                    } else if (heapObject is RootJNILocal) {
                                        rootJNILocal[heapObject.id] = heapObject.id
                                    } else if (heapObject is PrimitiveArray) {
                                        primitiveArray[heapObject.id] = heapObject
                                    } else if (heapObject is ObjectArray) {
                                        objectArray[heapObject.id] = heapObject
                                    } else if (heapObject is RootJavaFrame) {
                                        rootJavaFrame[heapObject.id] = heapObject
                                    } else if (heapObject is RootThreadObject) {
                                        rootThreadObject[heapObject.id] = heapObject
                                    } else if (heapObject is RootMonitorUsed) {
                                        rootMonitorUsed.add(heapObject.id)
                                    } else if (heapObject is RootStickyClass) {
                                        rootStickClass.add(heapObject.id)
                                    } else
                                        println("missed : $heapObject")
                                }
                                else -> println("missed : $frame")
                            }
                        }
                    }
                }
            }
        }
    }

    fun getClazzById(cid: Long): ClassObject {
        return clazzTable.getValue(cid)
    }

    fun writeTo(out: PrintStream) {
        // TODO output data to stdout...
    }

    fun addInstanceObject(instanceObject: InstanceObject) {
        oopTable[instanceObject.id] = instanceObject
    }

    fun getInstanceObject(id: Long): InstanceObject {
        return oopTable.getValue(id)
    }
}
