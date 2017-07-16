package com.nao20010128nao.Wisecraft.misc.ping.processors;

import android.util.*;
import com.nao20010128nao.Wisecraft.misc.*;

import java.util.*;

public class SinglePoolMultiServerPingProvider implements ServerPingProvider {
    final Queue<Map.Entry<Server, PingHandler>> queue = Factories.newDefaultQueue();
    final Set<PingThread> pingThread = Collections.synchronizedSet(new HashSet<PingThread>());
    final int max;
    volatile boolean offline = false;

    public SinglePoolMultiServerPingProvider(int max) {
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
        for (PingThread thread : pingThread) {
            thread.interrupt();
        }
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
            final String TAG = ProcessorUtils.getLogTag(SinglePoolMultiServerPingProvider.this);

            pingThread.add(this);
            try {
                Map.Entry<Server, PingHandler> now = null;
                while (!(queue.isEmpty() | isInterrupted())) {
                    Log.d(TAG, "Starting ping");
                    try {
                        now = queue.poll();
                        ProcessorUtils.doPingFull(SinglePoolMultiServerPingProvider.this, now.getKey(), now.getValue(), offline, true, true, false);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "Next");
                }
            } finally {
                pingThread.remove(this);
            }
        }
    }
}
