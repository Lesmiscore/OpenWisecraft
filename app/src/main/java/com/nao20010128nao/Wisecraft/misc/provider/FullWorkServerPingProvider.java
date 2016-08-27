package com.nao20010128nao.Wisecraft.misc.provider;
import com.nao20010128nao.Wisecraft.misc.Server;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Map;
import com.nao20010128nao.Wisecraft.misc.ServerStatus;
import com.nao20010128nao.Wisecraft.misc.KVP;

public class FullWorkServerPingProvider extends ExperimentalServerPingProvider
{
	Queue<Map.Entry<Server,PingHandler>> queue=new LinkedList<>();
	boolean finished=true;
	
	public FullWorkServerPingProvider(int parallel){
		super(parallel);
	}

	@Override
	public void putInQueue(Server server, ServerPingProvider.PingHandler handler) {
		// TODO: Implement this method
		queue.offer(new KVP<Server,PingHandler>(server,handler));
		if(finished){
			finished=false;
			Map.Entry<Server,PingHandler> kvp=queue.poll();
			super.putInQueue(kvp.getKey(), new PingHandlerWrapper(kvp.getValue()));
		}
	}
	
	
	
	private class PingHandlerWrapper implements PingHandler {
		PingHandler hand;
		public PingHandlerWrapper(PingHandler ph){
			hand=ph;
		}
		@Override
		public void onPingArrives(ServerStatus stat) {
			// TODO: Implement this method
			next();
			hand.onPingArrives(stat);
		}
		@Override
		public void onPingFailed(Server server) {
			// TODO: Implement this method
			next();
			hand.onPingFailed(server);
		}
		
		private void next(){
			if(queue.size()!=0){
				Map.Entry<Server,PingHandler> kvp=queue.poll();
				FullWorkServerPingProvider.super.putInQueue(kvp.getKey(), new PingHandlerWrapper(kvp.getValue()));
				finished=false;
			}else{
				finished=true;
			}
		}
	}
}
