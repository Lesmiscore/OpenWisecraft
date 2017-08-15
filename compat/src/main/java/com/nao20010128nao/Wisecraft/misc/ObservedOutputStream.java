package com.nao20010128nao.Wisecraft.misc;

import android.support.annotation.NonNull;

import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by lesmi on 17/08/16.
 */

public class ObservedOutputStream extends OutputStream{
    final List<OutputStream> o;
    public ObservedOutputStream(OutputStream... oss){
        o= Collections.unmodifiableList(Arrays.asList(oss));
    }
    public ObservedOutputStream( Function<OutputStream,OutputStream> converter,OutputStream... oss){
        o= Stream.of(oss).map(converter).toList();
    }

    @Override
    public void write(int b) throws IOException {
        for(OutputStream p:o)p.write(b);
    }

    @Override
    public void write(@NonNull byte[] b) throws IOException {
        for(OutputStream p:o)p.write(b);
    }

    @Override
    public void write(@NonNull byte[] b, int off, int len) throws IOException {
        for(OutputStream p:o)p.write(b,off,len);
    }

    @Override
    public void flush() throws IOException {
        for(OutputStream p:o)p.flush();
    }

    @Override
    public void close() throws IOException {
        for(OutputStream p:o)p.close();
    }
}
