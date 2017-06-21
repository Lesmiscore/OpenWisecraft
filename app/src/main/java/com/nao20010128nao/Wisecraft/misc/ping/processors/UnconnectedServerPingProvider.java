package com.nao20010128nao.Wisecraft.misc.ping.processors;

import android.util.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.ping.methods.pe.*;

import java.io.*;
import java.util.*;

public class UnconnectedServerPingProvider extends ServerPingProvider {
    Queue<Map.Entry<Server, PingHandler>> queue = Factories.newDefaultQueue();
    Thread pingThread = new PingThread();
    boolean offline = false;

    public void putInQueue(Server server, PingHandler handler) {
        Utils.requireNonNull(server);
        Utils.requireNonNull(handler);
        Utils.prepareLooper();
        queue.add(new KVP<>(server, handler));
        if (!pingThread.isAlive()) {
            pingThread = new PingThread();
            pingThread.start();
        }
    }

    @Override
    public int getQueueRemain() {
        return queue.size();
    }

    @Override
    public void stop() {
        pingThread.interrupt();
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
        return "UnconnectedServerPingProvider";
    }

    private class PingThread extends Thread implements Runnable {
        @Override
        public void run() {
            final String TAG=getLogTag();

            Map.Entry<Server, PingHandler> now = null;
            while (!(queue.isEmpty() | isInterrupted())) {
                try {
                    Log.d(TAG, "Starting ping");
                    now = queue.poll();
                    doPingFull(now.getKey(),now.getValue(),offline,true,false,true);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "Next");
            }
        }
    }
}
