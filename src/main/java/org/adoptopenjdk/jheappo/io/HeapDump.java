package com.kodewerk.jheappo.io;

/*
 * Copyright (c) 2018 Kirk Pepperdine.
 * Licensed under https://github.com/AdoptOpenJDK/jheappo/blob/master/LICENSE
 * Instructions: https://github.com/AdoptOpenJDK/jheappo/wiki
 */

import com.kodewerk.jheappo.heapdump.*;
import com.kodewerk.jheappo.heapdump.EndThread;
import com.kodewerk.jheappo.heapdump.HeapDumpSegment;
import com.kodewerk.jheappo.heapdump.HeapSummary;
import com.kodewerk.jheappo.heapdump.StartThread;

import java.io.*;
import java.nio.file.Path;

public class HeapDump {


    private Path path;
    private DataInputStream input;
    private HeapDumpHeader header;

    public HeapDump(Path path) {
        this.path = path;
    }

    public void stream() throws IOException {
        try {
            input = new DataInputStream(new BufferedInputStream(new FileInputStream(path.toFile())));
            readHeader(input);
            System.out.println("Header: " + header.toString());
            while (input.available() > 0)
                readRecord(input);
        }
        catch (FileNotFoundException ex){
            System.out.println(ex.toString());
        }
    }

    private void readHeader(DataInputStream heapDump) throws IOException {
        header = new HeapDumpHeader();
        header.extract( heapDump);
    }

    private void readRecord(DataInputStream heapDump) throws IOException {
        HeapDumpBuffer record = extract(heapDump);
        System.out.println("Heap Dump Record: " + record.toString());
        if ( record instanceof StackFrame) return;
        if ( record instanceof StackTrace) return;
        if ( record instanceof UTF8String) return;
        if ( record instanceof LoadClass) return;
        if ( record instanceof HeapDumpSegment) {
            record.dump(System.out);
        }
    }

    private byte[] readBody(DataInputStream inputStream, int bufferLength) throws IOException {
        byte[] buffer = new byte[bufferLength];
        int bytesRead = inputStream.read(buffer);
        if ( bytesRead < bufferLength)
            throw new IOException("bytes request exceeded bytes read");
        return buffer;
    }

    public HeapDumpBuffer extract(DataInputStream buffer) throws IOException {
        short tag = (short)buffer.readByte();
        long timeStamp = buffer.readInt();
        int bodySize = buffer.readInt();
        switch (tag) {
            case UTF8String.TAG :
                return new UTF8String(readBody(buffer, bodySize));
            case LoadClass.TAG:
                return new LoadClass(readBody(buffer, bodySize));
            case UnloadClass.TAG:
                return new UnloadClass(readBody(buffer, bodySize));
            case StackFrame.TAG:
                return new StackFrame(readBody(buffer, bodySize));
            case StackTrace.TAG:
                return new StackTrace(readBody(buffer, bodySize));
            case AllocSites.TAG:
                return new AllocSites(readBody(buffer, bodySize));
            case HeapSummary.TAG:
                return new HeapSummary(readBody(buffer, bodySize));
            case StartThread.TAG:
                return new StartThread(readBody(buffer, bodySize));
            case EndThread.TAG:
                return new EndThread(readBody(buffer, bodySize));
            case HeapDumpSegment.TAG1:
            case HeapDumpSegment.TAG2:
                return new HeapDumpSegment(readBody(buffer, bodySize));
            case HeapDumpEnd.TAG:
                return new HeapDumpEnd(readBody(buffer, bodySize));
            case CPUSamples.TAG:
                return new CPUSamples(readBody(buffer, bodySize));
            case ControlSettings.TAG:
                return new ControlSettings(readBody(buffer, bodySize));
            default :
                throw new IOException("Unknown record type + " + tag);
        }
    }

    @Override
    public String toString() {
        return path.toString();
    }
}
