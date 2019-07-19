package org.adoptopenjdk.jheappo.tools

import org.adoptopenjdk.jheappo.parser.HeapDumpSegment
import org.adoptopenjdk.jheappo.parser.HeapProfile
import org.adoptopenjdk.jheappo.parser.LoadClass
import org.adoptopenjdk.jheappo.parser.StackFrame
import org.adoptopenjdk.jheappo.parser.StackTrace
import org.adoptopenjdk.jheappo.parser.UTF8StringSegment
import org.adoptopenjdk.jheappo.parser.heap.ClassMetadata
import org.adoptopenjdk.jheappo.parser.heap.InstanceObject
import org.adoptopenjdk.jheappo.parser.heap.ObjectArray
import org.adoptopenjdk.jheappo.parser.heap.PrimitiveArray
import org.adoptopenjdk.jheappo.parser.heap.RootJNIGlobal
import org.adoptopenjdk.jheappo.parser.heap.RootJNILocal
import org.adoptopenjdk.jheappo.parser.heap.RootJavaFrame
import org.adoptopenjdk.jheappo.parser.heap.RootMonitorUsed
import org.adoptopenjdk.jheappo.parser.heap.RootStickyClass
import org.adoptopenjdk.jheappo.parser.heap.RootThreadObject
import org.adoptopenjdk.jheappo.parser.heap.UTF8String
import org.adoptopenjdk.jheappo.parser.Id
import java.io.PrintStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.HashMap
import java.util.HashSet

class JavaHeap(private val outputDir: Path) {

    internal var stringTable = HashMap<Id, UTF8String>()
    internal var clazzTable = HashMap<Id, ClassMetadata>()
    internal var oopTable = HashMap<Id, InstanceObject>()
    internal var loadClassTable = HashMap<Id, LoadClass>()
    internal var rootStickClass = HashSet<Id>()
    internal var rootJNIGlobal = HashMap<Id, Id>()
    internal var rootJNILocal = HashMap<Id, Id>()
    internal var rootMonitorUsed = HashSet<Id>()
    internal var rootJavaFrame = HashMap<Id, RootJavaFrame>()
    internal var rootThreadObject = HashMap<Id, RootThreadObject>()
    internal var primitiveArray = HashMap<Id, PrimitiveArray>()
    internal var objectArray = HashMap<Id, ObjectArray>()

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
                                    out.write("${string.id}->${string.string}\n")
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
                                    if (heapObject is ClassMetadata) {
                                        clazzTable[heapObject.id] = heapObject
                                        val loadClassRecord = loadClassTable[heapObject.id]
                                        val className = stringTable[loadClassRecord?.classNameStringID]
                                        clazzFile.write("$heapObject ${className?.string}\n")
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

    fun getClazzById(cid: Id): ClassMetadata {
        return clazzTable.getValue(cid)
    }

    fun writeTo(out: PrintStream) {
        // TODO output data to stdout...
    }

    fun addInstanceObject(instanceObject: InstanceObject) {
        oopTable[instanceObject.id] = instanceObject
    }

    fun getInstanceObject(id: Id): InstanceObject {
        return oopTable.getValue(id)
    }
}
