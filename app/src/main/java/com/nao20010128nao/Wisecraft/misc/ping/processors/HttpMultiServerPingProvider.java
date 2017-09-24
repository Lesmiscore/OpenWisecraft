package com.nao20010128nao.Wisecraft.misc.ping.processors;

import com.annimon.stream.Stream;
import com.nao20010128nao.Wisecraft.misc.*;

import java.util.*;

public class HttpMultiServerPingProvider implements ServerPingProvider {
    List<HttpServerPingProvider> objects = new ArrayList<>();
    int count = 0;

    public HttpMultiServerPingProvider(String head, int parallels) {
        for (int i = 0; i < parallels; i++) {
            objects.add(new HttpServerPingProvider(head));
        }
    }

    @Override
    public void putInQueue(Server server, ServerPingProvider.PingHandler handler) {
        objects.get(count).putInQueue(server, handler);
        count++;
        count = count % objects.size();
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
        return "HttpMultiServerPingProvider";
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
