package com.nao20010128nao.Wisecraft.misc.provider;
import android.util.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.pinger.pc.*;
import java.io.*;
import java.util.*;
public class PCServerPingProvider implements ServerPingProvider
{
	Queue<Map.Entry<Server,PingHandler>> queue=new LinkedList<>();
	Thread pingThread=new PingThread();
    boolean offline=false;
	
	public void putInQueue(Server server, PingHandler handler) {
		Utils.requireNonNull(server);
		Utils.requireNonNull(handler);
		Utils.prepareLooper();
		queue.add(new KVP<Server,PingHandler>(server, handler));
		if (!pingThread.isAlive()) {
			pingThread = new PingThread();
			pingThread.start();
		}
	}
	@Override
	public int getQueueRemain() {
		return queue.size();
	}
	@Override
	public void stop() {
		pingThread.interrupt();
	}

	@Override
	public void clearQueue() {
		queue.clear();
	}
	@Override
	public void clearAndStop() {
		clearAndStop();
		stop();
	}
    @Override
    public void offline() {
        offline=true;
    }

    @Override
    public void online() {
        offline=false;
    }

	private class PingThread extends Thread implements Runnable {
		@Override
		public void run() {
			Map.Entry<Server,PingHandler> now=null;
			while (!(queue.isEmpty()|isInterrupted())) {
				Log.d("PCSPP", "Starting ping");
				try {
					now = queue.poll();
                    if(offline){
                        Log.d("PCSPP", "Offline");
                        try {
                            now.getValue().onPingFailed(now.getKey());
                        } catch (Throwable ex_) {

                        }
                        continue;
                    }
					ServerStatus stat=new ServerStatus();
					stat.ip = now.getKey().ip;
					stat.port = now.getKey().port;
					stat.mode = now.getKey().mode;
					Log.d("PCSPP", stat.ip + ":" + stat.port + " " + stat.mode);
					switch(now.getKey().mode){
						case 0:
							try{
								now.getValue().onPingFailed(now.getKey());
							}catch(Throwable h){

							}
							continue;
						case 1:
							Log.d("PCSPP", "PC");
							PCQuery query=new PCQuery(stat.ip, stat.port);
							try {
								stat.response = query.fetchReply();
								Log.d("PCSPP", "Success");
							} catch (IOException e) {
								DebugWriter.writeToE("PCSPP",e);
								Log.d("PCSPP", "Failed");
								try {
									now.getValue().onPingFailed(now.getKey());
								} catch (Throwable ex) {

								}
								continue;
							}
							stat.ping = query.getLatestPingElapsed();
							break;
					}
					try {
                        now.getValue().onPingArrives(stat);
                    } catch (Throwable f) {

                    }
				} catch (Throwable e) {
					e.printStackTrace();
				}
				Log.d("PCSPP", "Next");
			}
		}
	}
}
