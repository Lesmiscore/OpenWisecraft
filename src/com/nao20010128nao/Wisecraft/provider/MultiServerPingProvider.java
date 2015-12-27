package com.nao20010128nao.Wisecraft.provider;
import com.nao20010128nao.Wisecraft.*;
import java.util.*;

public class MultiServerPingProvider implements ServerPingProvider
{
	List<NormalServerPingProvider> objects=new ArrayList<>();
	int count=0;
	public MultiServerPingProvider(int parallels){
		for(int i=0;i<parallels;i++){
			objects.add(new NormalServerPingProvider());
		}
	}
	@Override
	public void putInQueue(ServerListActivity.Server server, ServerPingProvider.PingHandler handler) {
		// TODO: Implement this method
		objects.get(count).putInQueue(server,handler);
		count++;
		count=count%objects.size();
	}
}
