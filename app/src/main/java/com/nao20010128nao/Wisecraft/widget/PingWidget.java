package com.nao20010128nao.Wisecraft.widget;

import android.appwidget.*;
import android.content.*;
import android.widget.*;
import com.google.gson.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.provider.*;

public class PingWidget extends AppWidgetProvider 
{

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		SharedPreferences widgetPref=context.getSharedPreferences("widgets",Context.MODE_PRIVATE);
		Gson gson=new Gson();
		widgetPref.edit().putInt("_version",2).commit();
		{/*
			int version=widgetPref.getInt("_version", 2);
			switch (version) {
				case 0:{
						for(String key:new HashSet<>(widgetPref.getAll().keySet())){
							if("_version".equals(key))continue;
							
						}
						OldServer19[] sa=gson.fromJson(pref.getString("servers", "[]"), OldServer19[].class);
						List<OldServer35> ns=new ArrayList<>();
						for (OldServer19 s:sa) {
							OldServer35 nso=new OldServer35();
							nso.ip = s.ip;
							nso.port = s.port;
							nso.mode = s.isPC ?1: 0;
							ns.add(nso);
						}
						pref.edit().putInt("serversJsonVersion", 1).putString("servers", gson.toJson(ns)).commit();
					}
				case 1:{
					
					
					}
				case 2:{
						/*Server[] sa=gson.fromJson(pref.getString("servers", "[]"), Server[].class);
						int prevLen=list.size();
						list.clear();
						sl.notifyItemRangeRemoved(0, prevLen);
						int curLen=sa.length;
						list.addAll(Arrays.asList(sa));
						sl.notifyItemRangeInserted(0, curLen);
					}*/
		}
		NormalServerPingProvider nspp=new NormalServerPingProvider();
		for(int wid:appWidgetIds){
			if(widgetPref.contains(wid+"")){
				//show activity here
				continue;
			}
			Server s=gson.fromJson(widgetPref.getString(wid+"","{}"),Server.class);
			NonBrodRevPingHandler ph=new NonBrodRevPingHandler();
			ph.id=wid;
			ph.c=context;
			nspp.putInQueue(s,ph);
		}
		//appWidgetManager.updateAppWidget();
	}
	
	public static class PingHandler extends BroadcastReceiver /*implements ServerPingProvider.PingHandler*/ {

		@Override
		public void onReceive(Context p1, Intent p2) {
			// TODO: Implement this method
		}

		/*
		@Override
		public void onPingArrives(ServerStatus stat) {
			// TODO: Implement this method
		}

		@Override
		public void onPingFailed(Server server) {
			// TODO: Implement this method
		}
		*/
	}
	
	class NonBrodRevPingHandler implements ServerPingProvider.PingHandler{
		int id;Context c;
		@Override
		public void onPingArrives(ServerStatus stat) {
			ServerStatusRemoteViewsWrapper ssrvw=new ServerStatusRemoteViewsWrapper(c);
			RemoteViews rvs=(RemoteViews)ssrvw.getTag();
		}

		@Override
		public void onPingFailed(Server server) {
			
		}
	}
}
