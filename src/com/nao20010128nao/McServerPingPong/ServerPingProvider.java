package com.nao20010128nao.McServerPingPong;
import java.util.*;
import query.*;

public class ServerPingProvider
{
	Queue<Map.Entry<ServerListActivity.Server,PingHandler>> queue=new LinkedList<>();
	Thread pingThread;

	public void putInQueue(ServerListActivity.Server server,PingHandler handler){
		
	}
	
	public static interface PingHandler{
		void onPingArrives(ServerListActivity.ServerStatus stat);
		void onPingFailed(ServerListActivity.Server server);
	}
	private class PingThread extends Thread implements Runnable {
		@Override
		public void run() {
			// TODO: Implement this method
			Map.Entry<ServerListActivity.Server,PingHandler> now=null;
			while(!queue.isEmpty()){
				now=queue.poll();
				MCQuery q=new MCQuery(now.getKey().ip,now.getKey().port);
				QueryResponseUniverse resp;
				try {
					resp=q.fullStatUni();
				} catch (Throwable e) {
					try {
						now.getValue().onPingFailed(now.getKey());
					} catch (Throwable f) {
						
					}
					continue;
				}
				ServerListActivity.ServerStatus stat=new ServerListActivity.ServerStatus();
				stat.ip=now.getKey().ip;
				stat.port=now.getKey().port;
				stat.response=resp;
				stat.ping=q.getLatestPingElapsed();
			}
		}
	}
}
