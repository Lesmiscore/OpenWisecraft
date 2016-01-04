package com.nao20010128nao.Wisecraft.provider;
import com.nao20010128nao.Wisecraft.*;
import java.util.*;

public class ExperimentalServerPingProvider implements ServerPingProvider
{
	List<NormalServerPingProvider> objects=new ArrayList<>();
	public ExperimentalServerPingProvider(int parallels){
		for(int i=0;i<parallels;i++){
			objects.add(new NormalServerPingProvider());
		}
	}
	@Override
	public void putInQueue(ServerListActivity.Server server, ServerPingProvider.PingHandler handler) {
		// TODO: Implement this method
		for(ServerPingProvider spp:objects){
			
		}
	}
	@Override
	public int getQueueRemain() {
		// TODO: Implement this method
		int i=0;
		for(ServerPingProvider spp:objects){
			i+=spp.getQueueRemain();
		}
		return i;
	}
}
