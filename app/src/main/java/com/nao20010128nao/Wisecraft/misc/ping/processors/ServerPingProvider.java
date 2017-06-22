package com.nao20010128nao.Wisecraft.misc.ping.processors;

import android.annotation.*;
import android.os.*;
import com.annimon.stream.*;
import com.nao20010128nao.Wisecraft.misc.*;

import java.util.*;

public interface ServerPingProvider {
    void putInQueue(Server server, PingHandler handler);

    int getQueueRemain();

    void stop();

    void clearQueue();

    void offline();

    void online();

    void clearAndStop();

    String getClassName();

    interface PingHandler {
        void onPingArrives(ServerStatus stat);

        void onPingFailed(Server server);
    }
}
