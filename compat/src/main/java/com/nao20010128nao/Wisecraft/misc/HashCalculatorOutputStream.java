package com.nao20010128nao.Wisecraft.misc;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by lesmi on 17/08/16.
 */

public class HashCalculatorOutputStream extends OutputStream {
    MessageDigest md;

    public HashCalculatorOutputStream(String alg){
        try {
            md=MessageDigest.getInstance(alg);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] digest(){
        return md.digest();
    }

    @Override
    public void write(int b) throws IOException {
        md.update((byte)(b&0xff));
    }

    @Override
    public void write(@NonNull byte[] b) throws IOException {
        md.update(b);
    }

    @Override
    public void write(@NonNull byte[] b, int off, int len) throws IOException {
        md.update(b,off,len);
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void close() throws IOException {
    }
}
