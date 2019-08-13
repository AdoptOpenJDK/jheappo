package org.adoptopenjdk.jheappo.io;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;


public final class HeapProfile {
    private final Path path;
    private final DataInputStream input;
    private boolean heapDumpEnd = false;

    public HeapProfile(Path path, DataInputStream input) {
        this.path = path;
        this.input = input;
    }

    public boolean isAtHeapDumpEnd() throws IOException {

        return heapDumpEnd || input.available() == 0;
    }

    public HeapProfileHeader readHeader() throws IOException {
        var header = new HeapProfileHeader();
        header.extract(input);

        return header;
    }

    private EncodedChunk readBody(DataInputStream inputStream, int bufferLength) throws IOException {
        byte[] buffer = new byte[bufferLength];
        int bytesRead = inputStream.read(buffer);

        if (bytesRead < bufferLength) {
            this.heapDumpEnd = true;

            throw new IOException("bytes request exceeded bytes read");
        } else {

            return new EncodedChunk(buffer);
        }
    }

    public HeapProfileRecord extract() throws IOException {
        byte tag = (new EncodedChunk(new byte[]{this.input.readByte()})).extractU1();
        long timeStamp = (long) this.input.readInt();
        int bodySize = this.input.readInt();

        final var body = readBody(input, bodySize);

        switch (tag) {
            case UTF8StringSegment.TAG:

                return new UTF8StringSegment(body);
            case LoadClass.TAG:

                return new LoadClass(body);
            case UnloadClass.TAG: {
                System.out.println("UnloadClass");

                return new UnloadClass(body);
            }
            case StackFrame.TAG:

                return new StackFrame(body);
            case StackTrace.TAG:

                return new StackTrace(body);
            case AllocSites.TAG: {
                System.out.println("AllocSites");

                return new AllocSites(body);
            }
            case HeapSummary.TAG: {
                System.out.println("HeapSummary");

                return new HeapSummary(body);
            }
            case StartThread.TAG: {
                System.out.println("StartThread");

                return new StartThread(body);
            }
            case HeapDumpSegment.TAG1:
            case HeapDumpSegment.TAG2:

                return new HeapDumpSegment(body);
            case HeapDumpEnd.TAG: {
                System.out.println("HeapDumpEnd");
                heapDumpEnd = true;

                return new HeapDumpEnd(body);
            }
            case CPUSamples.TAG: {
                System.out.println("CPUSamples");

                return new CPUSamples(body);
            }
            case ControlSettings.TAG: {
                System.out.println("ControlSettings");

                return new ControlSettings(body);
            }
            default:
                throw new IOException("Unknown record type " + tag);
        }

    }

    @Override
    public String toString() {

        return path.toString();
    }

    public static HeapProfile open(Path path, InputStream inputStream) {
        var input = new DataInputStream(new BufferedInputStream(inputStream));

        return new HeapProfile(path, input);
    }
}
