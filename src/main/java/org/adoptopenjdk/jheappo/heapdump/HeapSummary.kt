package org.adoptopenjdk.jheappo.heapdump

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

import org.adoptopenjdk.jheappo.io.HeapProfileRecord

class HeapSummary(body: EncodedChunk) : HeapProfileRecord() {
    companion object {
        const val TAG: UByte = 0x07U
    }
}
