package com.nao20010128nao.Wisecraft.provider;
import java.util.*;

import android.util.Log;
import com.nao20010128nao.MCPing.pc.PCQuery;
import com.nao20010128nao.Wisecraft.Utils;
import com.nao20010128nao.Wisecraft.misc.Server;
import com.nao20010128nao.Wisecraft.misc.ServerStatus;
import java.io.IOException;
public class PCServerPingProvider implements ServerPingProvider
{
	Queue<Map.Entry<Server,PingHandler>> queue=new LinkedList<>();
	Thread pingThread=new PingThread();
	
	public void putInQueue(Server server, PingHandler handler) {
		Utils.requireNonNull(server);
		Utils.requireNonNull(handler);
		queue.add(new KVP(server, handler));
		if (!pingThread.isAlive()) {
			pingThread = new PingThread();
			pingThread.start();
		}
	}
	@Override
	public int getQueueRemain() {
		// TODO: Implement this method
		return queue.size();
	}
	@Override
	public void stop() {
		// TODO: Implement this method
		pingThread.interrupt();
	}

	@Override
	public void clearQueue() {
		// TODO: Implement this method
		queue.clear();
	}
	

	private class PingThread extends Thread implements Runnable {
		@Override
		public void run() {
			// TODO: Implement this method
			Map.Entry<Server,PingHandler> now=null;
			while (!(queue.isEmpty()|isInterrupted())) {
				Log.d(getClass().getName().split("\\.")[1], "Starting ping");
				now = queue.poll();
				ServerStatus stat=new ServerStatus();
				stat.ip = now.getKey().ip;
				stat.port = now.getKey().port;
				stat.isPC = now.getKey().isPC;
				Log.d(getClass().getName().split("\\.")[1], stat.ip + ":" + stat.port + " " + stat.isPC);
				if (now.getKey().isPC) {
					Log.d(getClass().getName().split("\\.")[1], "PC");
					PCQuery query=new PCQuery(stat.ip, stat.port);
					try {
						stat.response = query.fetchReply();
						Log.d(getClass().getName().split("\\.")[1], "Success");
					} catch (IOException e) {
						e.printStackTrace();
						Log.d(getClass().getName().split("\\.")[1], "Failed");
						try {
							now.getValue().onPingFailed(now.getKey());
						} catch (Throwable ex) {

						}
						continue;
					}
					stat.ping = query.getLatestPingElapsed();
				} else {
					try{
						now.getValue().onPingFailed(now.getKey());
					}catch(Throwable h){
						
					}
					continue;
				}
				try {
					now.getValue().onPingArrives(stat);
				} catch (Throwable f) {

				}
				Log.d(getClass().getName().split("\\.")[1], "Next");
			}
		}
	}
	private class KVP implements Map.Entry<Server,PingHandler> {
		Server server;
		PingHandler handler;
		public KVP(Server server, PingHandler handler) {
			this.server = server;
			this.handler = handler;
		}
		@Override
		public NormalServerPingProvider.PingHandler setValue(NormalServerPingProvider.PingHandler p1) {
			// TODO: Implement this method
			PingHandler old=handler;
			handler = p1;
			return old;
		}
		@Override
		public Server getKey() {
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
