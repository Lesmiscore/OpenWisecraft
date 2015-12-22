package com.nao20010128nao.McServerPingPong.provider;
import java.util.*;
import query.*;
import com.nao20010128nao.McServerPingPong.*;
import ch.jamiete.mcping.*;
import java.io.*;

public class NormalServerPingProvider implements ServerPingProvider
{
	Queue<Map.Entry<ServerListActivity.Server,PingHandler>> queue=new LinkedList<>();
	Thread pingThread=new PingThread();

	public void putInQueue(ServerListActivity.Server server,PingHandler handler){
		Objects.requireNonNull(server);
		Objects.requireNonNull(handler);
		queue.add(new KVP(server,handler));
		if(!pingThread.isAlive()){
			pingThread=new PingThread();
			pingThread.start();
		}
	}
	
	
	private class PingThread extends Thread implements Runnable {
		@Override
		public void run() {
			// TODO: Implement this method
			Map.Entry<ServerListActivity.Server,PingHandler> now=null;
			while(!queue.isEmpty()){
				now=queue.poll();
				if(now.getKey().isPC){
					MinecraftPing ping=new MinecraftPing();
					MinecraftPingReply mcp;
					try {
						mcp=ping.getPing(new MinecraftPingOptions()
																	 .setCharset("UTF-8")
																	 .setHostname(now.getKey().ip)
																	 .setPort(now.getKey().port)
																	 .setTimeout(1000));
					} catch (IOException e) {
						try {
							now.getValue().onPingFailed(now.getKey());
						} catch (Throwable f) {

						}
						continue;
					}
					ServerListActivity.ServerStatus stat=new ServerListActivity.ServerStatus();
					stat.ip = now.getKey().ip;
					stat.port = now.getKey().port;
					stat.response = new QueryResponseUniverse(mcp);
					stat.ping = ping.getLatestPingElapsed();
					try {
						now.getValue().onPingArrives(stat);
					} catch (Throwable f) {

					}
				}else{
					MCQuery q=new MCQuery(now.getKey().ip, now.getKey().port);
					QueryResponseUniverse resp;
					try {
						resp = q.fullStatUni();
					} catch (Throwable e) {
						try {
							now.getValue().onPingFailed(now.getKey());
						} catch (Throwable f) {

						}
						continue;
					}
					ServerListActivity.ServerStatus stat=new ServerListActivity.ServerStatus();
					stat.ip = now.getKey().ip;
					stat.port = now.getKey().port;
					stat.response = resp;
					stat.ping = q.getLatestPingElapsed();
					try {
						now.getValue().onPingArrives(stat);
					} catch (Throwable f) {

					}
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
