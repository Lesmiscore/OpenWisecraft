package com.nao20010128nao.Wisecraft.misc.pingMethods;

import android.util.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.pinger.pc.*;
import com.nao20010128nao.Wisecraft.misc.pinger.pe.*;

import java.io.*;
import java.util.*;

public class NormalServerPingProvider implements ServerPingProvider {
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


    private class PingThread extends Thread implements Runnable {
        @Override
        public void run() {
            Map.Entry<Server, PingHandler> now = null;
            while (!(queue.isEmpty() | isInterrupted())) {
                Log.d("NSPP", "Starting ping");
                try {
                    now = queue.poll();
                    if (offline) {
                        Log.d("NSPP", "Offline");
                        try {
                            now.getValue().onPingFailed(now.getKey());
                        } catch (Throwable ex_) {

                        }
                        continue;
                    }
                    ServerStatus stat = new ServerStatus();
                    now.getKey().cloneInto(stat);
                    Log.d("NSPP", stat.ip + ":" + stat.port + " " + stat.mode);
                    switch (now.getKey().mode) {
                        case PE: {
                            Log.d("NSPP", "PE");
                            PEQuery query = new PEQuery(stat.ip, stat.port);
                            try {
                                stat.response = query.fullStat();
                                try {
                                    UnconnectedPing.UnconnectedPingResult res = UnconnectedPing.doPing(stat.ip, stat.port);
                                    SprPair pair = new SprPair();
                                    pair.setA(stat.response);
                                    pair.setB(res);
                                    stat.response = pair;
                                    Log.d("NSPP", "Success: Full Stat & Unconnected Ping");
                                } catch (IOException e) {
                                    DebugWriter.writeToE("NSPP", e);
                                    Log.d("NSPP", "Success: Full Stat");
                                }
                                stat.ping = query.getLatestPingElapsed();
                            } catch (Throwable e) {
                                DebugWriter.writeToE("NSPP", e);
                                try {
                                    UnconnectedPing.UnconnectedPingResult res = UnconnectedPing.doPing(stat.ip, stat.port);
                                    stat.response = res;
                                    stat.ping = res.getLatestPingElapsed();
                                    Log.d("NSPP", "Success: Unconnected Ping");
                                } catch (IOException ex) {
                                    DebugWriter.writeToE("NSPP", ex);
                                    try {
                                        now.getValue().onPingFailed(now.getKey());
                                    } catch (Throwable ex_) {

                                    }
                                    Log.d("NSPP", "Failed");
                                    continue;
                                }
                            }
                        }
                        break;
                        case PC: {
                            Log.d("NSPP", "PC");
                            PCQuery query = new PCQuery(stat.ip, stat.port);
                            try {
                                stat.response = query.fetchReply();
                                Log.d("NSPP", "Success");
                            } catch (IOException e) {
                                DebugWriter.writeToE("NSPP", e);
                                Log.d("NSPP", "Failed");
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
                Log.d("NSPP", "Next");
            }
        }
    }
}
