package com.nao20010128nao.Wisecraft.misc.pinger.pe;

import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.pinger.*;

import java.io.*;

public class Request {
    private ByteArrayOutputStream byteStream;
    private DataOutputStream dataStream;

    static byte[] MAGIC = {(byte) 0xFE, (byte) 0xFD};
    public byte type;
    public int sessionID;
    public byte[] payload;

    public Request() {
        int size = 1460;
        byteStream = new ByteArrayOutputStream(size);
        dataStream = new DataOutputStream(byteStream);
    }

    public Request(byte type) {
        this.type = type;
    }

    public byte[] toBytes() {
        byteStream.reset();

        try {
            dataStream.write(MAGIC);
            dataStream.write(type);
            dataStream.writeInt(sessionID);
            dataStream.write(payloadBytes());
        } catch (IOException e) {
            DebugWriter.writeToE("PEQuery", e);
        }

        return byteStream.toByteArray();
    }

    private byte[] payloadBytes() {
        if (type == PEQuery.HANDSHAKE) {
            return new byte[0];
        } else {
            return payload;
        }
    }

    public void setPayload(int load) {
        this.payload = PingerUtils.intToBytes(load);
    }
}
