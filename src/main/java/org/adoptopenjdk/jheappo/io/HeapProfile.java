package org.adoptopenjdk.jheappo.io;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */


import org.adoptopenjdk.jheappo.heapdump.*;

import java.io.*;
import java.nio.file.Path;

public class HeapProfile {


    private Path path;
    private DataInputStream input;
    private HeapProfileHeader header;
    private boolean heapDumpEnd;

    public HeapProfile(Path path) {
        this.path = path;
    }

    public void open() throws IOException {
        try {
            input = new DataInputStream(new BufferedInputStream(new FileInputStream(path.toFile())));
            heapDumpEnd = false;
        }
        catch (FileNotFoundException ex){
            System.out.println(ex.toString());
        }
    }

    public HeapProfileHeader readHeader() throws IOException {
        header = new HeapProfileHeader();
        header.extract( input);
        return header;
    }

    private byte[] readBody(DataInputStream inputStream, int bufferLength) throws IOException {
        byte[] buffer = new byte[bufferLength];
        int bytesRead = inputStream.read(buffer);
        if ( bytesRead < bufferLength) {
            heapDumpEnd = true;
            throw new IOException("bytes request exceeded bytes read");
        }
        return buffer;
    }

    public HeapProfileRecord extract() throws IOException {
        short tag = (short)input.readByte();
        long timeStamp = input.readInt();
        int bodySize = input.readInt();
        switch (tag) {
            case UTF8StringSegment.TAG :
                return new UTF8StringSegment(readBody(input, bodySize));
            case LoadClass.TAG:
                return new LoadClass(readBody(input, bodySize));
            case UnloadClass.TAG:
                System.out.println("UnloadClass");
                return new UnloadClass(readBody(input, bodySize));
            case StackFrame.TAG:
                return new StackFrame(readBody(input, bodySize));
            case StackTrace.TAG:
                return new StackTrace(readBody(input, bodySize));
            case AllocSites.TAG:
                System.out.println("AllocSites");
                return new AllocSites(readBody(input, bodySize));
            case HeapSummary.TAG:
                System.out.println("HeapSummary");
                return new HeapSummary(readBody(input, bodySize));
            case StartThread.TAG:
                System.out.println("StartThread");
                return new StartThread(readBody(input, bodySize));
            case EndThread.TAG:
                System.out.println("EndThread");
                return new EndThread(readBody(input, bodySize));
            case HeapDumpSegment.TAG1:
            case HeapDumpSegment.TAG2:
                return new HeapDumpSegment(readBody(input, bodySize));
            case HeapDumpEnd.TAG:
                System.out.println("HeapDumpEnd");
                heapDumpEnd = true;
                return new HeapDumpEnd(readBody(input, bodySize));
            case CPUSamples.TAG:
                System.out.println("CPUSamples");
                return new CPUSamples(readBody(input, bodySize));
            case ControlSettings.TAG:
                System.out.println("ControlSettings");
                return new ControlSettings(readBody(input, bodySize));
            default :
                throw new IOException("Unknown record type + " + tag);
        }
    }

    @Override
    public String toString() {
        return path.toString();
    }

    public boolean isAtHeapDumpEnd() throws IOException {
        return heapDumpEnd || input.available() == 0;
    }
}
