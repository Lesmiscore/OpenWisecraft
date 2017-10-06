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
    volatile boolean offline = false;

    public SinglePoolTcpServerPingProvider(int max) {
        this.max = max;
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
        return "SinglePoolTcpServerPingProvider";
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
                sock = ProcessorUtils.tcpPingCache.get();
                dos = new DataOutputStream(sock.getOutputStream());
                is = new DataInputStream(sock.getInputStream());
            }catch (Throwable e){
                while (!(queue.isEmpty() | isInterrupted())) {
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
                                ByteStreams.skipFully(is,is.readInt());
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
                    Thread.sleep(500);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "Next");
            }
            try {
                /*dos.write(2);
                Thread.sleep(500);
                dos.close();
                is.close();
                sock.close();*/
                ProcessorUtils.tcpPingCache.cache(sock);
            } catch (Throwable e) {
                WisecraftError.report(TAG, e);
            }finally {
                pingThread.remove(this);
            }
        }
    }
}
