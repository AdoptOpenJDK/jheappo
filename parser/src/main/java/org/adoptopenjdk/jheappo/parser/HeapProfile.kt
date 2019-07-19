package org.adoptopenjdk.jheappo.parser

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */


import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.*
import java.nio.file.Path

class HeapProfile(private val path: Path, private val input: DataInputStream) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(HeapProfile::class.java)

        fun open(path: Path, inputStream: InputStream): HeapProfile {
            val input = DataInputStream(BufferedInputStream(inputStream))
            return HeapProfile(path, input)
        }
    }

    private var heapDumpEnd: Boolean = false

    val isAtHeapDumpEnd: Boolean
        get() = heapDumpEnd || input.available() == 0


    fun readHeader(): HeapProfileHeader {
        val header = HeapProfileHeader()
        header.extract(input)
        return header
    }

    private fun readBody(inputStream: DataInputStream, bufferLength: Int): EncodedChunk {
        val buffer = ByteArray(bufferLength)
        // TODO allow short reads
        val bytesRead = inputStream.read(buffer)
        if (bytesRead < bufferLength) {
            heapDumpEnd = true
            throw IOException("bytes request exceeded bytes read")
        }
        return EncodedChunk(buffer)
    }

    fun extract(): HeapProfileRecord {
        val tag = EncodedChunk(byteArrayOf(input.readByte())).extractU1()
        val timeStamp = input.readInt().toLong()
        val bodySize = input.readInt()
        when (tag) {
            UTF8StringSegment.TAG -> return UTF8StringSegment(readBody(input, bodySize))
            LoadClass.TAG -> return LoadClass(readBody(input, bodySize))
            UnloadClass.TAG -> {
                logger.debug("UnloadClass")
                return UnloadClass(readBody(input, bodySize))
            }
            StackFrame.TAG -> return StackFrame(readBody(input, bodySize))
            StackTrace.TAG -> return StackTrace(readBody(input, bodySize))
            AllocSites.TAG -> {
                logger.debug("AllocSites")
                return AllocSites(readBody(input, bodySize))
            }
            HeapSummary.TAG -> {
                logger.debug("HeapSummary")
                return HeapSummary(readBody(input, bodySize))
            }
            StartThread.TAG -> {
                logger.debug("StartThread")
                return StartThread(readBody(input, bodySize))
            }
            EndThread.TAG -> {
                logger.debug("EndThread")
                return EndThread(readBody(input, bodySize))
            }
            HeapDumpSegment.TAG1, HeapDumpSegment.TAG2 -> return HeapDumpSegment(readBody(input, bodySize))
            HeapDumpEnd.TAG -> {
                logger.debug("HeapDumpEnd")
                heapDumpEnd = true
                return HeapDumpEnd(readBody(input, bodySize))
            }
            CPUSamples.TAG -> {
                logger.debug("CPUSamples")
                return CPUSamples(readBody(input, bodySize))
            }
            ControlSettings.TAG -> {
                logger.debug("ControlSettings")
                return ControlSettings(readBody(input, bodySize))
            }
            else -> throw IOException("Unknown record type + $tag")
        }
    }

    override fun toString(): String {
        return path.toString()
    }
}
