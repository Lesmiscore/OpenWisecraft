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

/**
 * Created by nao on 2017/06/22.
 */
public class ProcessorUtils {

    public static void doPingFull(ServerPingProvider spp, Server server, ServerPingProvider.PingHandler handler, boolean offline, boolean pe, boolean pc, boolean onlyPeUcp) {
        final String TAG = getLogTag(spp);

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

    public static String getLogTag(final ServerPingProvider serverPingProvider) {
        return Stream.of(new Iterator<Character>() {
            final String cn = serverPingProvider.getClassName();
            int now = 0;

            @Override
            public boolean hasNext() {
                return now < cn.length();
            }

            @Override
            public Character next() {
                return cn.charAt(now++);
            }
        })
                .filter(Character::isUpperCase)
                .collect(StringBuilder::new, StringBuilder::append)
                .toString();
    }
}
