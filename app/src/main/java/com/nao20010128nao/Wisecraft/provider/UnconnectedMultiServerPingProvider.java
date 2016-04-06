package com.nao20010128nao.Wisecraft.provider;
import com.nao20010128nao.Wisecraft.misc.Server;
import java.util.ArrayList;
import java.util.List;

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
}
