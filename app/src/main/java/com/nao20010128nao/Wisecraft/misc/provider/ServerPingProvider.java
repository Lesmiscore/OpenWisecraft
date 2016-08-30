package com.nao20010128nao.Wisecraft.misc.provider;
import com.nao20010128nao.Wisecraft.misc.*;

public interface ServerPingProvider {
	public void putInQueue(Server server, PingHandler handler);
	public int getQueueRemain();
	public void stop();
	public void clearQueue();
    public void offline();
    public void online();
	public static interface PingHandler {
		void onPingArrives(ServerStatus stat);
		void onPingFailed(Server server);
	}
}
