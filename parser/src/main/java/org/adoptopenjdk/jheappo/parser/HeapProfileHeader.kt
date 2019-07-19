package org.adoptopenjdk.jheappo.parser

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

import java.io.DataInputStream
import java.io.IOException

class HeapProfileHeader internal constructor() {

    companion object {
        private val SUPPORTED_VERSIONS = arrayOf("JAVA PROFILE 1.0.1", "JAVA PROFILE 1.0.2")
        private val SUPPORTED_IDENIFIER_SIZE = intArrayOf(4, 8)
    }

    private var heapDumpVersion: String? = null
    private var sizeOfIdentifiers = 0    /* u4 is unsigned 4 bytes.. which in this case is ok to assigned to signed int */

    private var millisecSinceEPOC: Long = 0

    private fun extractVersionString(buffer: DataInputStream): String {
        val string = CharArray(1024)
        var pos = 0
        var value = buffer.read()
        while (value > 0) {
            string[pos++] = value.toChar()
            value = buffer.read()
        }
        if (value < 0)
            throw IOException("Unexpected EOF")
        return String(string).trim { it <= ' ' }
    }

    fun extract(buffer: DataInputStream) {
        heapDumpVersion = extractVersionString(buffer)
        if (!(SUPPORTED_VERSIONS[0] == heapDumpVersion || SUPPORTED_VERSIONS[1] == heapDumpVersion))
            throw IOException(heapDumpVersion!! + " is not supported")
        sizeOfIdentifiers = buffer.readInt()
        if (sizeOfIdentifiers != SUPPORTED_IDENIFIER_SIZE[1] && sizeOfIdentifiers != SUPPORTED_IDENIFIER_SIZE[0])
            throw IOException("Unsupported identifier size $sizeOfIdentifiers")
        millisecSinceEPOC = buffer.readLong()
    }

    override fun toString(): String {
        return "$heapDumpVersion : $sizeOfIdentifiers : $millisecSinceEPOC"
    }
}
