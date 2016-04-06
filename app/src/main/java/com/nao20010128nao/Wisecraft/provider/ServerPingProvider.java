package com.nao20010128nao.Wisecraft.provider;
import com.nao20010128nao.Wisecraft.misc.Server;
import com.nao20010128nao.Wisecraft.misc.ServerStatus;

public interface ServerPingProvider {
	public void putInQueue(Server server, PingHandler handler);
	public int getQueueRemain();
	public void stop();
	public void clearQueue();
	public static interface PingHandler {
		void onPingArrives(ServerStatus stat);
		void onPingFailed(Server server);
	}
}
