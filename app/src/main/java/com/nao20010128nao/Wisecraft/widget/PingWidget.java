package com.nao20010128nao.Wisecraft.widget;

import android.app.*;
import android.appwidget.*;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.preference.*;
import android.support.v4.content.*;
import android.util.*;
import android.widget.*;
import com.google.gson.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.api.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.pinger.pc.*;
import com.nao20010128nao.Wisecraft.misc.pinger.pe.*;
import com.nao20010128nao.Wisecraft.misc.provider.*;
import java.util.*;

import static com.nao20010128nao.Wisecraft.misc.Utils.*;

public class PingWidget extends AppWidgetProvider 
{

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		SharedPreferences widgetPref = getWidgetPref(context);
		Gson gson=new Gson();
		widgetPref.edit().putInt("_version",2).putInt("_version.data",0).commit();
		{/*
			int version=widgetPref.getInt("_version", 2);
			switch (version) {
				case 0:{
						for(String key:new HashSet<>(widgetPref.getAll().keySet())){
			 				if("_version".startsWith(key))continue;
							
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
			Log.d("WisecraftWidgets","onUpdate: "+wid);
			if(!widgetPref.contains(wid+"")){
				RemoteViews rvs=new RemoteViews(context.getPackageName(),R.layout.ping_widget_init);
				appWidgetManager.updateAppWidget(wid,rvs);
				Log.d("WisecraftWidgets","none: "+wid);
				continue;
			}
			Server s=gson.fromJson(widgetPref.getString(wid+"","{}"),Server.class);
			NonBrodRevPingHandler ph=new NonBrodRevPingHandler();
			ph.id=wid;
			ph.c=context;
			ph.awm=appWidgetManager;
			nspp.putInQueue(s,ph);
			ServerStatusRemoteViewsWrapper viewHolder=new ServerStatusRemoteViewsWrapper(context,wid);
			RemoteViews rvs=(RemoteViews)viewHolder.getTag();
			viewHolder.pending(s,context);
			setupHandlers(rvs, context, wid);
			appWidgetManager.updateAppWidget(wid,rvs);
			Log.d("WisecraftWidgets","with: "+wid+": "+s);
		}
	}

	public static SharedPreferences getWidgetPref(Context context) {
		return context.getSharedPreferences("widgets", Context.MODE_PRIVATE);
	}
	
	public static WidgetData getWidgetData(Context c,int wid){
		return new Gson().fromJson(getWidgetPref(c).getString(wid+".data","{}"),WidgetData.class);
	}
	
	public static void setWidgetData(Context c,int wid,WidgetData data){
		getWidgetPref(c).edit().putString(wid+".data",new Gson().toJson(data)).commit();
	}

	static void setupHandlers(RemoteViews rvs, Context context, int wid) {
		SharedPreferences widgetPref=getWidgetPref(context);
		Server s=new Gson().fromJson(widgetPref.getString(""+wid,"{}"),Server.class);
		String addr=new StringBuilder("wisecraft://info/")
			.append(s.ip)
			.append('/')
			.append(s.port)
			.append('/')
			.append(s.mode==0?"PE":"PC")
			.toString();
			
		rvs.setOnClickPendingIntent(R.id.update, PendingIntent.getBroadcast(context, wid, new Intent(context, PingHandler.class).setAction("update").putExtra("wid", wid), 0));
		rvs.setOnClickPendingIntent(R.id.openServerStatus, PendingIntent.getActivity(context, wid*100, new Intent(context,RequestedServerInfoActivity.class).setData(Uri.parse(addr)), 0));
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		for(int i:appWidgetIds)Log.d("WisecraftWidgets","onDeleted: "+i);
		SharedPreferences widgetPref=getWidgetPref(context);
		SharedPreferences.Editor edt=widgetPref.edit();
		for(int i:appWidgetIds)edt.remove(i+"");
		edt.commit();
	}

	@Override
	public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
		SharedPreferences widgetPref=getWidgetPref(context);
		Map<String,Object> datas=new HashMap<String,Object>(widgetPref.getAll());
		SharedPreferences.Editor edt=widgetPref.edit();
		for(int i=0;i<oldWidgetIds.length;i++){
			Log.d("WisecraftWidgets","onRestored(1): "+oldWidgetIds[i]+"=>"+newWidgetIds[i]);
			edt.remove(oldWidgetIds[i]+"");
		}
		edt.commit();
		edt=widgetPref.edit();
		for(int i=0;i<oldWidgetIds.length;i++){
			Log.d("WisecraftWidgets","onRestored(2): "+oldWidgetIds[i]+"=>"+newWidgetIds[i]);
			edt.putString(newWidgetIds[i]+"",datas.get(oldWidgetIds[i]+"")+"");
		}
		edt.commit();
	}

	@Override
	public void onDisabled(Context context) {
		SharedPreferences widgetPref=getWidgetPref(context);
		SharedPreferences.Editor edt=widgetPref.edit();
		for(String key:new HashSet<String>(widgetPref.getAll().keySet())){
			if("_version".startsWith(key))continue;
			edt.remove(key);
		}
		edt.commit();
	}
	
	public static int styleToId(int style){
		switch(style){
			case 0:return R.layout.ping_widget_content;
		}
		return 0;
	}
	
	
	
	
	public static class PingHandler extends BroadcastReceiver {

		@Override
		public void onReceive(Context p1, Intent p2) {
			int wid=p2.getIntExtra("wid",0);
			Log.d("WisecraftWidgets","Update Issued: "+wid);
			SharedPreferences widgetPref=getWidgetPref(p1);
			Gson gson=new Gson();
			Server s=gson.fromJson(widgetPref.getString(wid+"","{}"),Server.class);
			NormalServerPingProvider nspp=new NormalServerPingProvider();
			NonBrodRevPingHandler ph=new NonBrodRevPingHandler();
			ph.id=wid;
			ph.c=p1;
			ph.awm=AppWidgetManager.getInstance(p1);
			ServerStatusRemoteViewsWrapper ssrvw=new ServerStatusRemoteViewsWrapper(p1,wid);
			RemoteViews rvs=(RemoteViews)ssrvw.getTag();
			ssrvw.pending(s,p1);
			setupHandlers(rvs, p1, wid);
			ph.awm.updateAppWidget(wid,rvs);
			nspp.putInQueue(s,ph);
		}
	}
	
	static class NonBrodRevPingHandler implements ServerPingProvider.PingHandler{
		int id;Context c;AppWidgetManager awm;
		@Override
		public void onPingArrives(ServerStatus s) {
			Log.d("WisecraftWidgets","Ping OK for: "+id);
			SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(c);
			
			ServerStatusRemoteViewsWrapper ssrvw=new ServerStatusRemoteViewsWrapper(c,id);
			RemoteViews rvs=(RemoteViews)ssrvw.getTag();
			
			ssrvw.setStatColor(ContextCompat.getColor(c, R.color.stat_ok));
			final String title;
			if (s.response instanceof FullStat) {//PE
				FullStat fs = (FullStat) s.response;
				Map<String, String> m = fs.getDataAsMap();
				if (m.containsKey("hostname")) {
					title = m.get("hostname");
				} else if (m.containsKey("motd")) {
					title = m.get("motd");
				} else {
					title = s.toString();
				}
				ssrvw.setServerPlayers(m.get("numplayers"), m.get("maxplayers"));
			} else if (s.response instanceof Reply19) {//PC 1.9~
				Reply19 rep = (Reply19) s.response;
				if (rep.description == null) {
					title = s.toString();
				} else {
					title = rep.description.text;
				}
				ssrvw.setServerPlayers(rep.players.online, rep.players.max);
			} else if (s.response instanceof Reply) {//PC
				Reply rep = (Reply) s.response;
				if (rep.description == null) {
					title = s.toString();
				} else {
					title = rep.description;
				}
				ssrvw.setServerPlayers(rep.players.online, rep.players.max);
			} else if (s.response instanceof SprPair) {//PE?
				SprPair sp = ((SprPair) s.response);
				if (sp.getB() instanceof UnconnectedPing.UnconnectedPingResult) {
					UnconnectedPing.UnconnectedPingResult res = (UnconnectedPing.UnconnectedPingResult) sp.getB();
					title = res.getServerName();
					ssrvw.setServerPlayers(res.getPlayersCount(), res.getMaxPlayers());
				} else if (sp.getA() instanceof FullStat) {
					FullStat fs = (FullStat) sp.getA();
					Map<String, String> m = fs.getDataAsMap();
					if (m.containsKey("hostname")) {
						title = m.get("hostname");
					} else if (m.containsKey("motd")) {
						title = m.get("motd");
					} else {
						title = s.toString();
					}
					ssrvw.setServerPlayers(m.get("numplayers"), m.get("maxplayers"));
				} else {
					title = s.toString();
					ssrvw.setServerPlayers();
				}
			} else if (s.response instanceof UnconnectedPing.UnconnectedPingResult) {//PE
				UnconnectedPing.UnconnectedPingResult res = (UnconnectedPing.UnconnectedPingResult) s.response;
				title = res.getServerName();
				ssrvw.setServerPlayers(res.getPlayersCount(), res.getMaxPlayers());
			} else {//Unreachable
				title = s.toString();
				ssrvw.setServerPlayers();
			}
			if (pref.getBoolean("serverListColorFormattedText", false)) {
				ssrvw.setServerName(parseMinecraftFormattingCode(title,Color.WHITE));
			} else {
				ssrvw.setServerName(deleteDecorations(title));
			}
			ssrvw
				.setPingMillis(s.ping)
				.setServer(s);
			
			setupHandlers(rvs, c, id);
			awm.updateAppWidget(id,rvs);
		}

		@Override
		public void onPingFailed(Server server) {
			Log.d("WisecraftWidgets","Ping NG for: "+id);
			ServerStatusRemoteViewsWrapper ssrvw=new ServerStatusRemoteViewsWrapper(c,id);
			RemoteViews rvs=(RemoteViews)ssrvw.getTag();
			ssrvw.offline(server,c);
			
			setupHandlers(rvs, c, id);
			awm.updateAppWidget(id,rvs);
		}
	}
	
	public static class WidgetData{
		public int style=0;
	}
	
	public static class Type2 extends PingWidget{}
}
