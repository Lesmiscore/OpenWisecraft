package com.nao20010128nao.Wisecraft.services;
import android.app.*;
import android.content.*;
import android.os.*;
import android.preference.*;
import android.support.design.widget.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.activity.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.provider.*;
import java.util.*;

public class ServerFinderService extends Service
{
	static Map<String,Map<Integer,ServerStatus>> detected=SuppliedHashMap.fromClass(HashMap.class,true);
	
	ServerPingProvider spp;
	@Override
	public IBinder onBind(Intent p1) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO: Implement this method
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void explore(final String ip, final int startPort, final int endPort, final boolean isPC,final String sessionId) {
		new AsyncTask<Void,ServerStatus,Void>(){
			public Void doInBackground(Void... l) {
				final int max=endPort - startPort;

				int threads=Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(ServerFinderService.this).getString("parallels", "6"));
				if (isPC) {
					spp = new PCMultiServerPingProvider(threads);
				} else {
					spp = new UnconnectedMultiServerPingProvider(threads);
				}

				for (int p=startPort;p < endPort;p++) {
					final int p2=p;
					Server s=new Server();
					s.ip = ip;
					s.port = p;
					s.mode = isPC ?1: 0;
					spp.putInQueue(s, new ServerPingProvider.PingHandler(){
							public void onPingArrives(ServerStatus s) {
								publishProgress(s);
								update(endPort-p2,max);
							}
							public void onPingFailed(Server s) {
								update(endPort-p2,max);
							}
						});
				}
				return null;
			}
			public void onProgressUpdate(ServerStatus... s) {
				ServerStatus ss=s[0];
				detected.get(sessionId).put(ss.port,ss);
			}
			private void update(final int now,final int max) {
				
			}
		}.execute();
	}
}
