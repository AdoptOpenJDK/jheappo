package org.adoptopenjdk.jheappo.heaptextoutput

import org.adoptopenjdk.jheappo.parser.HeapProfile
import org.adoptopenjdk.jheappo.model.JavaHeap
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class HeapDumpTextTest {
    @TempDir
    lateinit var tmp: Path;

    @Test
    fun textOutputFromHeapToolUnchanged() {
        HeapDumpTextTest::class.java.getResourceAsStream("heap.dump").use { stream ->
            JavaHeap(tmp).apply {
                populateFrom(HeapProfile.open(Paths.get("test"), stream))
            }
        }

        val contents = tmp.toFile().list().toList()
        assertEquals(setOf("string.table", "class.table", "instance.table", "loadClass.table"), contents.toSet())

        contents.forEach { filename ->
            val actual = Files.newBufferedReader(tmp.resolve(filename), StandardCharsets.UTF_8).readText()
            val expected = HeapDumpTextTest::class.java.getResourceAsStream(filename).use { stream ->
                InputStreamReader(stream, StandardCharsets.UTF_8).use { isr ->
                    BufferedReader(isr).use { br ->
                        br.readText()
                    }
                }
            }

            assertEquals(expected, actual)
        }
    }
}
