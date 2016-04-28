package com.nao20010128nao.Wisecraft.provider;
import com.nao20010128nao.Wisecraft.misc.*;
import java.util.*;

import android.util.Log;
import com.nao20010128nao.Wisecraft.Utils;
import com.nao20010128nao.Wisecraft.misc.pinger.pc.PCQuery;
import com.nao20010128nao.Wisecraft.misc.pinger.pe.PEQuery;
import com.nao20010128nao.Wisecraft.pingEngine.UnconnectedPing;
import java.io.IOException;

public class NormalServerPingProvider implements ServerPingProvider {
	Queue<Map.Entry<Server,PingHandler>> queue=new LinkedList<>();
	Thread pingThread=new PingThread();

	public void putInQueue(Server server, PingHandler handler) {
		Utils.requireNonNull(server);
		Utils.requireNonNull(handler);
		queue.add(new KVP<Server,PingHandler>(server, handler));
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
				Log.d("NSPP", "Starting ping");
				try {
					now = queue.poll();
					ServerStatus stat=new ServerStatus();
					stat.ip = now.getKey().ip;
					stat.port = now.getKey().port;
					stat.mode = now.getKey().mode;
					Log.d("NSPP", stat.ip + ":" + stat.port + " " + stat.mode);
					switch(now.getKey().mode){
						case 0:{
							Log.d("NSPP", "PE");
							PEQuery query=new PEQuery(stat.ip, stat.port);
							try {
								stat.response = query.fullStat();
								Log.d("NSPP", "Success: Full Stat");
								try {
									UnconnectedPing.UnconnectedPingResult res=UnconnectedPing.doPing(stat.ip, stat.port);
									SprPair pair=new SprPair();
									pair.setA(stat.response);
									pair.setB(res);
									stat.response = pair;
									Log.d("NSPP", "Success: Full Stat & Unconnected Ping");
								} catch (IOException e) {
									Log.d("NSPP", "Success: Full Stat");
								}
								stat.ping = query.getLatestPingElapsed();
							} catch (Throwable e) {
								DebugWriter.writeToE("NSPP",e);
								try {
									UnconnectedPing.UnconnectedPingResult res=UnconnectedPing.doPing(stat.ip, stat.port);
									stat.response = res;
									stat.ping = res.getLatestPingElapsed();
									Log.d("NSPP", "Success: Unconnected Ping");
								} catch (IOException ex) {
									try {
										now.getValue().onPingFailed(now.getKey());
									} catch (Throwable ex_) {

									}
									Log.d("NSPP", "Failed");
									continue;
								}
							}
							}break;
						case 1:{
								Log.d("NSPP", "PC");
								PCQuery query=new PCQuery(stat.ip, stat.port);
								try {
									stat.response = query.fetchReply();
									Log.d("NSPP", "Success");
								} catch (IOException e) {
									DebugWriter.writeToE("PCSPP",e);
									Log.d("NSPP", "Failed");
									try {
										now.getValue().onPingFailed(now.getKey());
									} catch (Throwable ex) {

									}
									continue;
								}
								stat.ping = query.getLatestPingElapsed();
							}break;
					}
					try {
                        now.getValue().onPingArrives(stat);
                    } catch (Throwable f) {

                    }
				} catch (Throwable e) {
					e.printStackTrace();
				}
				Log.d("NSPP", "Next");
			}
		}
	}
}
