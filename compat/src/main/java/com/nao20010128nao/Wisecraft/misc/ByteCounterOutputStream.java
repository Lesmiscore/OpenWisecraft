package com.nao20010128nao.Wisecraft.misc;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by lesmi on 17/08/16.
 */

public class ByteCounterOutputStream extends OutputStream {
    long size=0;

    public long getSize() {
        return size;
    }

    @Override
    public void write(int b) throws IOException {
        size++;
    }

    @Override
    public void write(@NonNull byte[] b) throws IOException {
        size+=b.length;
    }

    @Override
    public void write(@NonNull byte[] b, int off, int len) throws IOException {
        size+=len;
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void close() throws IOException {
    }
}
