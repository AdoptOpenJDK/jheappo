package org.adoptopenjdk.jheappo

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

import org.adoptopenjdk.jheappo.io.HeapProfile
import org.adoptopenjdk.jheappo.model.HeapGraph
import org.adoptopenjdk.jheappo.model.JavaHeap

import java.io.File

object Heappo {
    @JvmStatic
    fun main(args: Array<String>) {
        val path = File(args[0]).toPath()
        val heapDump = HeapProfile.open(path)
        if (args.size > 1 && args[1].equals("graph", ignoreCase = true)) {
            val graph = HeapGraph(File("graph.db"))
            graph.populateFrom(heapDump)
        } else {
            val heap = JavaHeap()
            heap.populateFrom(heapDump)
            heap.writeTo(System.out)
        }
    }
}
