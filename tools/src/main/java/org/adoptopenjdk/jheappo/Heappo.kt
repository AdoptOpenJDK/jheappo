package org.adoptopenjdk.jheappo

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

import org.adoptopenjdk.jheappo.parser.HeapProfile
import org.adoptopenjdk.jheappo.model.HeapGraph
import org.adoptopenjdk.jheappo.model.JavaHeap

import java.io.File
import java.io.FileInputStream
import java.nio.file.Paths

object Heappo {
    @JvmStatic
    fun main(args: Array<String>) {
        val path = File(args[0]).toPath()
        val heapDump = HeapProfile.open(path, FileInputStream(path.toFile()))
        if (args.size > 1 && args[1].equals("graph", ignoreCase = true)) {
            val graph = HeapGraph(File("graph.db"))
            graph.populateFrom(heapDump)
        } else {
            val heap = JavaHeap(Paths.get("."))
            heap.populateFrom(heapDump)
            heap.writeTo(System.out)
        }
    }
}
