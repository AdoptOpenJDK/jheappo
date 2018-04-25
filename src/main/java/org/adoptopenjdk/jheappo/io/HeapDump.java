package org.adoptopenjdk.jheappo.io;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */


import org.adoptopenjdk.jheappo.heapdump.*;

import java.io.*;
import java.nio.file.Path;

public class HeapDump {


    private Path path;
    private DataInputStream input;
    private HeapDumpHeader header;
    private boolean heapDumpEnd;

    public HeapDump(Path path) {
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

    public HeapDumpHeader readHeader() throws IOException {
        header = new HeapDumpHeader();
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

    public HeapDumpBuffer extract() throws IOException {
        short tag = (short)input.readByte();
        long timeStamp = input.readInt();
        int bodySize = input.readInt();
        switch (tag) {
            case UTF8StringSegment.TAG :
                return new UTF8StringSegment(readBody(input, bodySize));
            case LoadClass.TAG:
                return new LoadClass(readBody(input, bodySize));
            case UnloadClass.TAG:
                return new UnloadClass(readBody(input, bodySize));
            case StackFrame.TAG:
                return new StackFrame(readBody(input, bodySize));
            case StackTrace.TAG:
                return new StackTrace(readBody(input, bodySize));
            case AllocSites.TAG:
                return new AllocSites(readBody(input, bodySize));
            case HeapSummary.TAG:
                return new HeapSummary(readBody(input, bodySize));
            case StartThread.TAG:
                return new StartThread(readBody(input, bodySize));
            case EndThread.TAG:
                return new EndThread(readBody(input, bodySize));
            case HeapDumpSegment.TAG1:
            case HeapDumpSegment.TAG2:
                return new HeapDumpSegment(readBody(input, bodySize));
            case HeapDumpEnd.TAG:
                heapDumpEnd = true;
                return new HeapDumpEnd(readBody(input, bodySize));
            case CPUSamples.TAG:
                return new CPUSamples(readBody(input, bodySize));
            case ControlSettings.TAG:
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
