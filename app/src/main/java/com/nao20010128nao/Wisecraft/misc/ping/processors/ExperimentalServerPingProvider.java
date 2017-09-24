package com.nao20010128nao.Wisecraft.misc.ping.processors;

import com.annimon.stream.Stream;
import com.nao20010128nao.Wisecraft.misc.*;

import java.util.*;

public class ExperimentalServerPingProvider implements ServerPingProvider {
    List<NormalServerPingProvider> objects = new ArrayList<>();

    public ExperimentalServerPingProvider(int parallels) {
        for (int i = 0; i < parallels; i++) {
            objects.add(new NormalServerPingProvider());
        }
    }

    @Override
    public void putInQueue(Server server, ServerPingProvider.PingHandler handler) {
        int delta = Integer.MAX_VALUE;
        ServerPingProvider obj = null;
        for (ServerPingProvider spp : objects) {
            if (delta > spp.getQueueRemain()) {
                delta = spp.getQueueRemain();
                obj = spp;
            }
        }
        obj.putInQueue(server, handler);
    }

    @Override
    public int getQueueRemain() {
        int i = 0;
        for (ServerPingProvider spp : objects) {
            i += spp.getQueueRemain();
        }
        return i;
    }

    @Override
    public void stop() {
        Stream.of(objects).forEach(ServerPingProvider::stop);
    }

    @Override
    public void clearQueue() {
        Stream.of(objects).forEach(ServerPingProvider::clearQueue);
    }

    @Override
    public void clearAndStop() {
        clearQueue();
        stop();
    }

    @Override
    public String getClassName() {
        return "ExperimentalServerPingProvider";
    }

    @Override
    public void offline() {
        Stream.of(objects).forEach(ServerPingProvider::offline);
    }

    @Override
    public void online() {
        Stream.of(objects).forEach(ServerPingProvider::online);
    }
}
