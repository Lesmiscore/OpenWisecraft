package com.nao20010128nao.McServerPingPong;

public interface ServerPingProvider
{
	public void putInQueue(ServerListActivity.Server server,PingHandler handler);
	public static interface PingHandler{
		void onPingArrives(ServerListActivity.ServerStatus stat);
		void onPingFailed(ServerListActivity.Server server);
	}
}
