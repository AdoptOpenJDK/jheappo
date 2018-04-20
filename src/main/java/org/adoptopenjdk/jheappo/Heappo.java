package org.adoptopenjdk.jheappo;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

import com.kodewerk.jheappo.io.HeapDump;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Heappo {
    public static void main(String[] args) throws IOException {
        Path path = new File(args[0]).toPath();
        HeapDump heapDump = new HeapDump(path);
        heapDump.stream();
        //heapDump.stream().forEach(System.out::println);
    }
}
