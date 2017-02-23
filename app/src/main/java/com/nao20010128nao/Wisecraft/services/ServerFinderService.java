package com.nao20010128nao.Wisecraft.services;
import android.app.*;
import android.content.*;
import android.os.*;
import android.preference.*;
import android.support.v4.app.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.provider.*;
import java.util.*;

public class ServerFinderService extends Service
{
	public static final String EXTRA_IP="ip";
	public static final String EXTRA_MODE="mode";
	public static final String EXTRA_START_PORT="sport";
	public static final String EXTRA_END_PORT="eport";
	
	static Map<String,Map<Integer,ServerStatus>> detected=SuppliedHashMap.fromClass((Class<Map<Integer,ServerStatus>>)HashMap.class,true);
	
	ServerPingProvider spp;
	@Override
	public IBinder onBind(Intent p1) {
		// TODO: Implement this method
		return new InternalBinder();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO: Implement this method
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void updateNotification(String tag,int now,int max){
		int id=tag.hashCode();
		Notification ntf=createBaseNotification(this,now,max,detected.get(tag));
		NotificationManagerCompat.from(this).notify(id,ntf);
	}
	
	private static Notification createBaseNotification(Context c,int now,int max,Map<Integer,ServerStatus> servers){
		NotificationCompat.Builder ntf=new NotificationCompat.Builder(c);
		// Add title like "Server Finder - ** servers found"
		ntf.setContentTitle("Server Finder - [COUNT] servers found".replace("[COUNT]",servers.size()+""));
		ntf.setProgress(now,max,false);
		if(servers!=null || servers.size()!=0){
			List<Integer> l=Factories.arrayList(servers.keySet());
			Collections.sort(l);
			NotificationCompat.InboxStyle bts=new NotificationCompat.InboxStyle();
			for(int port:l){
				bts.addLine(servers.get(port).toString());
			}
			ntf.setStyle(bts);
		}
		return ntf.build();
	}
	
	private void explore(final String ip, final int startPort, final int endPort, final int mode) {
		final String tag=Utils.randomText();
		new AsyncTask<Void,ServerStatus,Void>(){
			public Void doInBackground(Void... l) {
				final int max=endPort - startPort;

				int threads=Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(ServerFinderService.this).getString("parallels", "6"));
				if (mode==1) {
					spp = new PCMultiServerPingProvider(threads);
				} else {
					spp = new UnconnectedMultiServerPingProvider(threads);
				}

				for (int p=startPort;p < endPort;p++) {
					final int p2=p;
					Server s=new Server();
					s.ip = ip;
					s.port = p;
					s.mode = mode;
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
				detected.get(tag).put(ss.port,ss);
			}
			private void update(final int now,final int max) {
				updateNotification(tag,now,max);
			}
		}.execute();
	}
	
	class InternalBinder extends Binder{
		public void startExploration(String ip,int mode,int start,int end){
			explore(ip,start,end,mode);
		}
	}
}
