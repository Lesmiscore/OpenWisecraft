package com.nao20010128nao.Wisecraft.misc;

import java.io.*;

/**
 * Created by nao on 2017/07/02.
 */
public interface ByteHandler {
    void processBytes(byte[] buf, int off, int len) throws IOException;
}
