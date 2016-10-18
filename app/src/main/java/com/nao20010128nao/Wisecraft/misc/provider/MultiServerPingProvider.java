package com.nao20010128nao.Wisecraft.misc.provider;
import com.nao20010128nao.Wisecraft.misc.*;
import java.util.*;

public class MultiServerPingProvider implements ServerPingProvider {
	List<NormalServerPingProvider> objects=new ArrayList<>();
	int count=0;
	public MultiServerPingProvider(int parallels) {
		for (int i=0;i < parallels;i++) {
			objects.add(new NormalServerPingProvider());
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
		int i=0;
		for (ServerPingProvider spp:objects) {
			i += spp.getQueueRemain();
		}
		return i;
	}
	@Override
	public void stop() {
		for (ServerPingProvider spp:objects) {
			spp.stop();
		}
	}
	@Override
	public void clearQueue() {
		for (ServerPingProvider spp:objects) {
			spp.clearQueue();
		}
	}

    @Override
    public void offline() {
        for (ServerPingProvider spp:objects) {
            spp.offline();
		}
    }

    @Override
    public void online() {
        for (ServerPingProvider spp:objects) {
            spp.online();
		}
    }
}
