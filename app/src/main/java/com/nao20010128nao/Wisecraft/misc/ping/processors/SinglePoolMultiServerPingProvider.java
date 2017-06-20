package com.nao20010128nao.Wisecraft.misc.pingMethods;

import android.util.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.pinger.pc.*;
import com.nao20010128nao.Wisecraft.misc.pinger.pe.*;

import java.io.*;
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

    private class PingThread extends Thread implements Runnable {
        @Override
        public void run() {
            pingThread.add(this);
            try {
                Map.Entry<Server, PingHandler> now = null;
                while (!(queue.isEmpty() | isInterrupted())) {
                    Log.d("SPMSPP", "Starting ping");
                    try {
                        now = queue.poll();
                        if (offline) {
                            Log.d("SPMSPP", "Offline");
                            try {
                                now.getValue().onPingFailed(now.getKey());
                            } catch (Throwable ex_) {

                            }
                            continue;
                        }
                        ServerStatus stat = new ServerStatus();
                        now.getKey().cloneInto(stat);
                        Log.d("SPMSPP", stat.ip + ":" + stat.port + " " + stat.mode);
                        switch (now.getKey().mode) {
                            case PE: {
                                Log.d("SPMSPP", "PE");
                                PEQuery query = new PEQuery(stat.ip, stat.port);
                                try {
                                    stat.response = query.fullStat();
                                    try {
                                        UnconnectedPing.UnconnectedPingResult res = UnconnectedPing.doPing(stat.ip, stat.port);
                                        SprPair pair = new SprPair();
                                        pair.setA(stat.response);
                                        pair.setB(res);
                                        stat.response = pair;
                                        Log.d("SPMSPP", "Success: Full Stat & Unconnected Ping");
                                    } catch (IOException e) {
                                        DebugWriter.writeToE("SPMSPP", e);
                                        Log.d("SPMSPP", "Success: Full Stat");
                                    }
                                    stat.ping = query.getLatestPingElapsed();
                                } catch (Throwable e) {
                                    DebugWriter.writeToE("SPMSPP", e);
                                    try {
                                        UnconnectedPing.UnconnectedPingResult res = UnconnectedPing.doPing(stat.ip, stat.port);
                                        stat.response = res;
                                        stat.ping = res.getLatestPingElapsed();
                                        Log.d("SPMSPP", "Success: Unconnected Ping");
                                    } catch (IOException ex) {
                                        DebugWriter.writeToE("NSPP", ex);
                                        try {
                                            now.getValue().onPingFailed(now.getKey());
                                        } catch (Throwable ex_) {

                                        }
                                        Log.d("SPMSPP", "Failed");
                                        continue;
                                    }
                                }
                            }
                            break;
                            case PC: {
                                Log.d("SPMSPP", "PC");
                                PCQuery query = new PCQuery(stat.ip, stat.port);
                                try {
                                    stat.response = query.fetchReply();
                                    Log.d("SPMSPP", "Success");
                                } catch (IOException e) {
                                    DebugWriter.writeToE("NSPP", e);
                                    Log.d("SPMSPP", "Failed");
                                    try {
                                        now.getValue().onPingFailed(now.getKey());
                                    } catch (Throwable ex) {

                                    }
                                    continue;
                                }
                                stat.ping = query.getLatestPingElapsed();
                            }
                            break;
                        }
                        try {
                            now.getValue().onPingArrives(stat);
                        } catch (Throwable f) {

                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    Log.d("SPMSPP", "Next");
                }
            } finally {
                pingThread.remove(this);
            }
        }
    }
}
