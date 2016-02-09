package com.nao20010128nao.Wisecraft.provider;
import com.nao20010128nao.Wisecraft.*;
import java.io.*;
import java.util.*;
import com.nao20010128nao.MCPing.pc.*;
import com.nao20010128nao.MCPing.pe.*;

public class NormalServerPingProvider implements ServerPingProvider
{
	Queue<Map.Entry<ServerListActivity.Server,PingHandler>> queue=new LinkedList<>();
	Thread pingThread=new PingThread();

	public void putInQueue(ServerListActivity.Server server,PingHandler handler){
		Utils.requireNonNull(server);
		Utils.requireNonNull(handler);
		queue.add(new KVP(server,handler));
		if(!pingThread.isAlive()){
			pingThread=new PingThread();
			pingThread.start();
		}
	}
	@Override
	public int getQueueRemain() {
		// TODO: Implement this method
		return queue.size();
	}
	
	
	private class PingThread extends Thread implements Runnable {
		@Override
		public void run() {
			// TODO: Implement this method
			Map.Entry<ServerListActivity.Server,PingHandler> now=null;
			while(!queue.isEmpty()){
				now=queue.poll();
				ServerListActivity.ServerStatus stat=new ServerListActivity.ServerStatus();
				stat.ip = now.getKey().ip;
				stat.port = now.getKey().port;
				stat.isPC=now.getKey().isPC;
				if(now.getKey().isPC){
					PCQuery query=new PCQuery(stat.ip,stat.port);
					try {
						long s=System.currentTimeMillis();
						stat.response = query.fetchReply();
						stat.ping=System.currentTimeMillis()-s;
					} catch (IOException e) {
						e.printStackTrace();
						try {
							now.getValue().onPingFailed(now.getKey());
						} catch (Throwable ex) {
							
						}
						continue;
					}
				} else {
					PEQuery query=new PEQuery(stat.ip,stat.port);
					try {
						stat.response = query.fullStatUni();
					} catch (Throwable e) {
						e.printStackTrace();
						try {
							now.getValue().onPingFailed(now.getKey());
						} catch (Throwable ex) {
							
						}
						continue;
					}
					try {
						long s=System.currentTimeMillis();
						query.basicStat();
						stat.ping=System.currentTimeMillis()-s;
					} catch (Throwable e) {
						e.printStackTrace();
						try {
							now.getValue().onPingFailed(now.getKey());
						} catch (Throwable ex) {

						}
						continue;
					}
				}
				try {
					now.getValue().onPingArrives(stat);
				} catch (Throwable f) {

				}
			}
		}
	}
	private class KVP implements Map.Entry<ServerListActivity.Server,PingHandler> {
		ServerListActivity.Server server;
		PingHandler handler;
		public KVP(ServerListActivity.Server server,PingHandler handler){
			this.server=server;
			this.handler=handler;
		}
		@Override
		public NormalServerPingProvider.PingHandler setValue(NormalServerPingProvider.PingHandler p1) {
			// TODO: Implement this method
			PingHandler old=handler;
			handler=p1;
			return old;
		}
		@Override
		public ServerListActivity.Server getKey() {
			// TODO: Implement this method
			return server;
		}
		@Override
		public NormalServerPingProvider.PingHandler getValue() {
			// TODO: Implement this method
			return handler;
		}
	}
}
