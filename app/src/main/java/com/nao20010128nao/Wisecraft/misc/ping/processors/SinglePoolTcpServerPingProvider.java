package com.nao20010128nao.Wisecraft.misc.ping.processors;

import android.text.TextUtils;
import android.util.Log;

import com.annimon.stream.Stream;
import com.google.common.io.ByteStreams;
import com.nao20010128nao.Wisecraft.WisecraftError;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.ping.methods.PingSerializeProvider;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.*;

public class SinglePoolTcpServerPingProvider implements ServerPingProvider {
    final Queue<Map.Entry<Server, PingHandler>> queue = Factories.newDefaultQueue();
    final Set<PingThread> pingThread = Collections.synchronizedSet(new HashSet<PingThread>());
    final int max;
    String host;
    int port;
    volatile boolean offline = false;

    public SinglePoolTcpServerPingProvider(int max,String host, int port) {
        this.max = max;
        if (TextUtils.isEmpty(host)) throw new IllegalArgumentException("host");
        if (port < 1 | port > 65535) throw new IllegalArgumentException("port");
        this.host = host;
        this.port = port;
    }

    public void putInQueue(Server server, PingHandler handler) {
        Utils.requireNonNull(server);
        Utils.requireNonNull(handler);
        Utils.prepareLooper();
        queue.add(new KVP<>(server, handler));
        if (pingThread.size() < max) {
            new PingThread().start();
        }
    }

    @Override
    public int getQueueRemain() {
        return queue.size();
    }

    @Override
    public void stop() {
        Stream.of(pingThread).forEach(Thread::interrupt);
    }

    @Override
    public void clearQueue() {
        queue.clear();
    }

    @Override
    public void clearAndStop() {
        clearQueue();
        stop();
    }

    @Override
    public void offline() {
        offline = true;
    }

    @Override
    public void online() {
        offline = false;
    }

    @Override
    public String getClassName() {
        return "SinglePoolMultiServerPingProvider";
    }

    private class PingThread extends Thread implements Runnable {
        @Override
        public void run() {
            pingThread.add(this);
            
            final String TAG = ProcessorUtils.getLogTag(SinglePoolTcpServerPingProvider.this);

            Map.Entry<Server, PingHandler> now = null;
            Socket sock;
            DataOutputStream dos;
            DataInputStream is;
            try {
                sock = new Socket(host, port);
                dos = new DataOutputStream(sock.getOutputStream());
                is = new DataInputStream(new BufferedInputStream(sock.getInputStream()));
                dos.write(14);
            }catch (Throwable e){
                while (!(queue.isEmpty() | isInterrupted())) {
                    Log.d(TAG, "Starting ping");
                    now = queue.poll();
                    try {
                        now.getValue().onPingFailed(now.getKey());
                    } catch (Throwable ex_) {

                    }
                }
                WisecraftError.report(TAG, e);
                return;
            }
            while (!(queue.isEmpty() | isInterrupted())) {
                Log.d(TAG, "Starting ping");
                try {
                    now = queue.poll();
                    if (offline) {
                        Log.d(TAG, "Offline");
                        try {
                            now.getValue().onPingFailed(now.getKey());
                        } catch (Throwable ex_) {

                        }
                        continue;
                    }
                    try {
                        Server s = now.getKey();
                        dos.write(1);
                        dos.writeUTF(s.ip);
                        dos.writeInt(s.port);
                        dos.writeInt(s.mode.getNumber());
                        dos.flush();
                        switch (is.readInt()){
                            case 1:
                                int payloadSize=is.readInt();
                                ServerStatus stat = PingSerializeProvider.loadFromServerDumpFile(ByteStreams.limit(is,payloadSize));
                                try {
                                    now.getValue().onPingArrives(stat);
                                } catch (Throwable f) {

                                }
                                break;
                            default:
                                is.readFully(new byte[is.readInt()]);
                                try {
                                    now.getValue().onPingFailed(now.getKey());
                                } catch (Throwable ex_) {

                                }
                                break;
                        }

                    } catch (Throwable e) {
                        WisecraftError.report(TAG, e);
                        try {
                            now.getValue().onPingFailed(now.getKey());
                        } catch (Throwable ex_) {

                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                Thread.sleep(500);
                Log.d(TAG, "Next");
            }
            try {
                dos.write(2);
                dos.close();
                is.close();
                sock.close();
            } catch (Throwable e) {
                WisecraftError.report(TAG, e);
            }
        }
    }
}
