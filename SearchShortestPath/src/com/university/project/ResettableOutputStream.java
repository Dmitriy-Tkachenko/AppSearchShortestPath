package com.university.project;

import java.io.*;

public final class ResettableOutputStream extends OutputStream {

    private final RandomAccessFile raf;

    public ResettableOutputStream(File f) throws FileNotFoundException {
        raf = new RandomAccessFile(f, "rw");
    }

    public void write(int b) throws IOException {
        raf.write(b);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        raf.write(b, off, len);
    }

    public void close() throws IOException {
        raf.setLength(raf.getFilePointer());
    }

    public void reset() throws IOException {
        raf.seek(0);
    }
}
