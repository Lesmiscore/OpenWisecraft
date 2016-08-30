package com.nao20010128nao.Wisecraft.misc.provider;
import com.nao20010128nao.Wisecraft.misc.*;
import java.util.*;

public class UnconnectedMultiServerPingProvider implements ServerPingProvider {
	List<UnconnectedServerPingProvider> objects=new ArrayList<>();
	int count=0;
	public UnconnectedMultiServerPingProvider(int parallels) {
		for (int i=0;i < parallels;i++) {
			objects.add(new UnconnectedServerPingProvider());
		}
	}
	@Override
	public void putInQueue(Server server, ServerPingProvider.PingHandler handler) {
		// TODO: Implement this method
		objects.get(count).putInQueue(server, handler);
		count++;
		count = count % objects.size();
	}

	@Override
	public int getQueueRemain() {
		// TODO: Implement this method
		int i=0;
		for (ServerPingProvider spp:objects) {
			i += spp.getQueueRemain();
		}
		return i;
	}
	@Override
	public void stop() {
		// TODO: Implement this method
		for (ServerPingProvider spp:objects) {
			spp.stop();
		}
	}
	@Override
	public void clearQueue() {
		// TODO: Implement this method
		for (ServerPingProvider spp:objects) {
			spp.clearQueue();
		}
	}
    
    @Override
    public void offline() {
        // TODO: Implement this method
        for (ServerPingProvider spp:objects) {
            spp.offline();
        }
    }
    
    @Override
    public void online() {
        // TODO: Implement this method
        for (ServerPingProvider spp:objects) {
            spp.online();
        }
    }
}
