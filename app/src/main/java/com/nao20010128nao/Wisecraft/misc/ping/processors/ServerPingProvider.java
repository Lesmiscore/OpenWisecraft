package com.nao20010128nao.Wisecraft.misc.ping.processors;

import com.nao20010128nao.Wisecraft.misc.*;

public interface ServerPingProvider {
    void putInQueue(Server server, PingHandler handler);

    int getQueueRemain();

    void stop();

    void clearQueue();

    void offline();

    void online();

    void clearAndStop();

    interface PingHandler {
        void onPingArrives(ServerStatus stat);

        void onPingFailed(Server server);
    }
}
