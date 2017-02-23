package com.nao20010128nao.Wisecraft.misc.provider;
import com.nao20010128nao.Wisecraft.misc.*;
import java.util.*;

public class ExperimentalServerPingProvider implements ServerPingProvider {
	List<NormalServerPingProvider> objects=new ArrayList<>();
	public ExperimentalServerPingProvider(int parallels) {
		for (int i=0;i < parallels;i++) {
			objects.add(new NormalServerPingProvider());
		}
	}
	@Override
	public void putInQueue(Server server, ServerPingProvider.PingHandler handler) {
		int delta=Integer.MAX_VALUE;
		ServerPingProvider obj=null;
		for (ServerPingProvider spp:objects) {
			if (delta > spp.getQueueRemain()) {
				delta = spp.getQueueRemain();
				obj = spp;
			}
		}
		obj.putInQueue(server, handler);
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
	public void clearAndStop() {
		clearAndStop();
		stop();
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
