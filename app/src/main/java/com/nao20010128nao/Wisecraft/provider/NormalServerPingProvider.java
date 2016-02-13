package com.nao20010128nao.Wisecraft.provider;
import com.nao20010128nao.Wisecraft.*;
import java.io.*;
import java.util.*;
import com.nao20010128nao.MCPing.pc.*;
import com.nao20010128nao.MCPing.pe.*;
import com.nao20010128nao.Wisecraft.pingEngine.*;
import com.nao20010128nao.Wisecraft.misc.*;
import android.util.*;

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
				Log.d(getClass().getName(),"Starting ping");
				now=queue.poll();
				ServerListActivity.ServerStatus stat=new ServerListActivity.ServerStatus();
				stat.ip = now.getKey().ip;
				stat.port = now.getKey().port;
				stat.isPC=now.getKey().isPC;
				Log.d(getClass().getName(),stat.ip+":"+stat.port+" "+stat.isPC);
				if(now.getKey().isPC){
					Log.d(getClass().getName(),"PC");
					PCQuery query=new PCQuery(stat.ip,stat.port);
					try {
						stat.response = query.fetchReply();
						Log.d(getClass().getName(),"Success");
					} catch (IOException e) {
						e.printStackTrace();
						Log.d(getClass().getName(),"Failed");
						try {
							now.getValue().onPingFailed(now.getKey());
						} catch (Throwable ex) {
							
						}
						continue;
					}
					stat.ping=query.getLatestPingElapsed();
				} else {
					Log.d(getClass().getName(),"PE");
					PEQuery query=new PEQuery(stat.ip,stat.port);
					try {
						stat.response = query.fullStatUni();
						Log.d(getClass().getName(),"Success: Full Stat");
						try{
							UnconnectedPing.UnconnectedPingResult res=UnconnectedPing.doPing(stat.ip,stat.port);
							SprPair pair=new SprPair();
							pair.setA(stat.response);
							pair.setB(res);
							stat.response=pair;
							Log.d(getClass().getName(),"Success: Full Stat & Unconnected Ping");
						}catch(IOException e){
							Log.d(getClass().getName(),"Success: Full Stat");
						}
						stat.ping=query.getLatestPingElapsed();
					} catch (Throwable e) {
						e.printStackTrace();
						try{
							UnconnectedPing.UnconnectedPingResult res=UnconnectedPing.doPing(stat.ip,stat.port);
							stat.response=res;
							stat.ping=res.getLatestPingElapsed();
							Log.d(getClass().getName(),"Success: Unconnected Ping");
						}catch(IOException ex){
							try {
								now.getValue().onPingFailed(now.getKey());
							} catch (Throwable ex_) {

							}
							Log.d(getClass().getName(),"Failed");
							continue;
						}
					}
				}
				try {
					now.getValue().onPingArrives(stat);
				} catch (Throwable f) {

				}
				Log.d(getClass().getName(),"Next");
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
