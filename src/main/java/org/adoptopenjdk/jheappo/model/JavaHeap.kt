package org.adoptopenjdk.jheappo.model

import org.adoptopenjdk.jheappo.io.HeapDumpSegment
import org.adoptopenjdk.jheappo.io.HeapProfile
import org.adoptopenjdk.jheappo.io.LoadClass
import org.adoptopenjdk.jheappo.io.StackFrame
import org.adoptopenjdk.jheappo.io.StackTrace
import org.adoptopenjdk.jheappo.io.UTF8StringSegment
import org.adoptopenjdk.jheappo.objects.ClassObject
import org.adoptopenjdk.jheappo.objects.InstanceObject
import org.adoptopenjdk.jheappo.objects.ObjectArray
import org.adoptopenjdk.jheappo.objects.PrimitiveArray
import org.adoptopenjdk.jheappo.objects.RootJNIGlobal
import org.adoptopenjdk.jheappo.objects.RootJNILocal
import org.adoptopenjdk.jheappo.objects.RootJavaFrame
import org.adoptopenjdk.jheappo.objects.RootMonitorUsed
import org.adoptopenjdk.jheappo.objects.RootStickyClass
import org.adoptopenjdk.jheappo.objects.RootThreadObject
import org.adoptopenjdk.jheappo.objects.UTF8String
import java.io.PrintStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.HashMap
import java.util.HashSet

class JavaHeap(private val outputDir: Path) {

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
        Files.newBufferedWriter(outputDir.resolve("string.table")).use { out ->
            Files.newBufferedWriter(outputDir.resolve("class.table")).use { clazzFile ->
                Files.newBufferedWriter(outputDir.resolve("instance.table")).use { instanceFile ->
                    Files.newBufferedWriter(outputDir.resolve("loadClass.table")).use { loadClassFile ->
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
                                        instanceObject!!.inflate(this.getClazzById(instanceObject.classObjectID))
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
