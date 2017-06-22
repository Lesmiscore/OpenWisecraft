package com.nao20010128nao.Wisecraft.misc.ping.processors;

import android.annotation.*;
import android.os.*;
import android.util.*;
import com.annimon.stream.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.ping.methods.pc.*;
import com.nao20010128nao.Wisecraft.misc.ping.methods.pe.*;

import java.io.*;
import java.util.*;

public interface ServerPingProvider {
    void putInQueue(Server server, PingHandler handler);

    int getQueueRemain();

    void stop();

    void clearQueue();

    void offline();

    void online();

    void clearAndStop();

    @TargetApi(Build.VERSION_CODES.N)
    default void doPingFull(Server server, PingHandler handler, boolean offline, boolean pe, boolean pc, boolean onlyPeUcp) {
        final String TAG = getLogTag();

        Log.d(TAG, "Starting ping");
        try {
            if (offline) {
                Log.d(TAG, "Offline");
                try {
                    handler.onPingFailed(server);
                } catch (Throwable ex_) {

                }
                return;
            }
            ServerStatus stat = new ServerStatus();
            server.cloneInto(stat);
            Log.d(TAG, stat.ip + ":" + stat.port + " " + stat.mode);
            switch (server.mode) {
                case PE:
                    if (pe) {
                        Log.d(TAG, "PE");
                        if (onlyPeUcp) {
                            try {
                                UnconnectedPing.UnconnectedPingResult res = UnconnectedPing.doPing(stat.ip, stat.port);
                                stat.response = res;
                                stat.ping = res.getLatestPingElapsed();
                                Log.d(TAG, "Success: Unconnected Ping");
                            } catch (IOException ex) {
                                DebugWriter.writeToE(TAG, ex);
                                try {
                                    handler.onPingFailed(server);
                                } catch (Throwable ex_) {

                                }
                                Log.d(TAG, "Failed");
                                return;
                            }
                        } else {
                            PEQuery query = new PEQuery(stat.ip, stat.port);
                            try {
                                stat.response = query.fullStat();
                                try {
                                    UnconnectedPing.UnconnectedPingResult res = UnconnectedPing.doPing(stat.ip, stat.port);
                                    SprPair pair = new SprPair();
                                    pair.setA(stat.response);
                                    pair.setB(res);
                                    stat.response = pair;
                                    Log.d(TAG, "Success: Full Stat & Unconnected Ping");
                                } catch (IOException e) {
                                    DebugWriter.writeToE(TAG, e);
                                    Log.d(TAG, "Success: Full Stat");
                                }
                                stat.ping = query.getLatestPingElapsed();
                            } catch (Throwable e) {
                                DebugWriter.writeToE(TAG, e);
                                try {
                                    UnconnectedPing.UnconnectedPingResult res = UnconnectedPing.doPing(stat.ip, stat.port);
                                    stat.response = res;
                                    stat.ping = res.getLatestPingElapsed();
                                    Log.d(TAG, "Success: Unconnected Ping");
                                } catch (IOException ex) {
                                    DebugWriter.writeToE(TAG, ex);
                                    try {
                                        handler.onPingFailed(server);
                                    } catch (Throwable ex_) {

                                    }
                                    Log.d(TAG, "Failed");
                                    return;
                                }
                            }
                        }
                    } else {
                        try {
                            handler.onPingFailed(server);
                        } catch (Throwable ex) {

                        }
                        return;
                    }
                    break;
                case PC:
                    if (pc) {
                        Log.d(TAG, "PC");
                        PCQuery query = new PCQuery(stat.ip, stat.port);
                        try {
                            stat.response = query.fetchReply();
                            Log.d(TAG, "Success");
                        } catch (IOException e) {
                            DebugWriter.writeToE(TAG, e);
                            Log.d(TAG, "Failed");
                            try {
                                handler.onPingFailed(server);
                            } catch (Throwable ex) {

                            }
                            return;
                        }
                        stat.ping = query.getLatestPingElapsed();
                    } else {
                        try {
                            handler.onPingFailed(server);
                        } catch (Throwable ex) {

                        }
                        return;
                    }
                    break;
            }
            try {
                handler.onPingArrives(stat);
            } catch (Throwable f) {

            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    default String getLogTag() {
        return Stream.of(new Iterator<Character>() {
            final String cn = getClassName();
            int now = -1;

            @Override
            public boolean hasNext() {
                return now < cn.length();
            }

            @Override
            public Character next() {
                return cn.charAt(++now);
            }
        })
                .filter(Character::isUpperCase)
                .collect(StringBuilder::new, StringBuilder::append)
                .toString();
    }

    @TargetApi(Build.VERSION_CODES.N)
    default String getClassName() {
        return "ServerPingProvider";
    }

    interface PingHandler {
        void onPingArrives(ServerStatus stat);

        void onPingFailed(Server server);
    }
}
