package com.nao20010128nao.Wisecraft.widget;
import android.app.*;
import android.os.Bundle;
import android.view.View;
import com.nao20010128nao.Wisecraft.misc.AppBaseArrayAdapter;
import android.widget.AdapterView;
import com.nao20010128nao.Wisecraft.ServerListActivity.*;
import java.util.*;
import android.view.ViewGroup;
import com.nao20010128nao.Wisecraft.R;
import android.widget.TextView;
import com.nao20010128nao.Wisecraft.Constant;
import com.nao20010128nao.Wisecraft.ServerListActivity;
import com.google.gson.Gson;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.appwidget.AppWidgetManager;
import android.widget.RemoteViews;
import com.nao20010128nao.Wisecraft.provider.NormalServerPingProvider;
import com.nao20010128nao.Wisecraft.provider.ServerPingProvider;
import com.nao20010128nao.MCPing.pe.FullStat;

import static com.nao20010128nao.Wisecraft.Utils.*;
import com.nao20010128nao.MCPing.pc.Reply;
import com.nao20010128nao.Wisecraft.misc.SprPair;
import com.nao20010128nao.Wisecraft.pingEngine.UnconnectedPing;
import android.support.v4.app.ActivityCompat;
import android.content.Intent;

public class AskServerActivity extends ListActivity
{
	List<Server> list;
	Map<String,ServerListActivity.Server> servers;
	int widId;
	SharedPreferences pref;
	ServerList sl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		widId=getIntent().getIntExtra("widid",0);
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		setListAdapter(sl=new ServerList());
		getListView().setOnItemClickListener(sl);
		loadServers();
		if(servers.containsKey(widId)){
			finish();
			startActivity(new Intent(this,ServerListActivity.class));
		}
	}
	public void loadServers() {
		Server[] sa=new Gson().fromJson(pref.getString("servers", "[]"), Server[].class);
		sl.clear();
		sl.addAll(sa);
		servers=new Gson().fromJson(pref.getString("widget","{}"),HashMap.class);
	}
	
	class ServerList extends AppBaseArrayAdapter<Server> implements AdapterView.OnItemClickListener {
		List<View> cached=new ArrayList();
		
		public ServerList() {
			super(AskServerActivity.this, 0, list = new ArrayList<Server>());
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO: Implement this method
			if (cached.size() > position) {
				View v=cached.get(position);
				if (v != null) {
					return v;
				}
			}
			//if(convertView!=null)return convertView;
			final View layout=getLayoutInflater().inflate(R.layout.quickstatus, null, false);
			Server s=getItem(position);
			((TextView)layout.findViewById(R.id.serverName)).setText(R.string.working);
			((TextView)layout.findViewById(R.id.pingMillis)).setText("");
			((TextView)layout.findViewById(R.id.serverAddress)).setText(s.ip + ":" + s.port);
			while (cached.size() <= position)
				cached.addAll(Constant.TEN_LENGTH_NULL_LIST);
			cached.set(position, layout);
			return layout;
		}
		public View getCachedView(int position) {
			return cached.get(position);
		}
		public View getViewQuick(int pos) {
			return getView(pos, null, null);
		}
		@Override
		public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
			// TODO: Implement this method
			Server s=getItem(p3);
			servers.put(widId+"",s);
			pref.edit().putString("widget",new Gson().toJson(servers)).commit();
			final AppWidgetManager awm=AppWidgetManager.getInstance(AskServerActivity.this);
			final RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.widget_layout);
			NormalServerPingProvider spp=new NormalServerPingProvider();
			spp.putInQueue(s,new ServerPingProvider.PingHandler(){
					public void onPingArrives(ServerListActivity.ServerStatus res){
						updateTitle(remoteViews,res);
						remoteViews.setTextViewText(R.id.serverIp,res+"");
						awm.updateAppWidget(widId,remoteViews);
					}
					public void onPingFailed(ServerListActivity.Server res){
						remoteViews.setTextViewText(R.id.serverName,getResources().getString(R.string.notResponding));
						remoteViews.setTextViewText(R.id.serverPlayersCount,getResources().getString(R.string.notResponding));
						awm.updateAppWidget(widId,remoteViews);
					}
				});
			finish();
		}

		@Override
		public void add(ServerListActivity.Server object) {
			// TODO: Implement this method
			if (!list.contains(object))super.add(object);
		}

		@Override
		public void addAll(ServerListActivity.Server[] items) {
			// TODO: Implement this method
			for (Server s:items)add(s);
		}

		@Override
		public void addAll(Collection<? extends ServerListActivity.Server> collection) {
			// TODO: Implement this method
			for (Server s:collection)add(s);
		}

		@Override
		public void remove(ServerListActivity.Server object) {
			// TODO: Implement this method
			cached.remove(list.indexOf(object));
			super.remove(object);
		}
	}
	
	
	private static void updateTitle(RemoteViews rv,ServerListActivity.ServerStatus s){
		final String title;
		if (s.response instanceof FullStat) {//PE
			FullStat fs=(FullStat)s.response;
			Map<String,String> m=fs.getData();
			if (m.containsKey("hostname")) {
				title = deleteDecorations(m.get("hostname"));
			} else if (m.containsKey("motd")) {
				title = deleteDecorations(m.get("motd"));
			} else {
				title = s.ip + ":" + s.port;
			}
		} else if (s.response instanceof Reply) {//PC
			Reply rep=(Reply)s.response;
			if (rep.description == null) {
				title = s.ip + ":" + s.port;
			} else {
				title = deleteDecorations(rep.description);
			}
		} else if (s.response instanceof SprPair) {//PE?
			SprPair sp=((SprPair)s.response);
			if (sp.getB() instanceof UnconnectedPing.UnconnectedPingResult) {
				title = ((UnconnectedPing.UnconnectedPingResult)sp.getB()).getServerName();
			} else if (sp.getA() instanceof FullStat) {
				FullStat fs=(FullStat)sp.getA();
				Map<String,String> m=fs.getData();
				if (m.containsKey("hostname")) {
					title = deleteDecorations(m.get("hostname"));
				} else if (m.containsKey("motd")) {
					title = deleteDecorations(m.get("motd"));
				} else {
					title = s.ip + ":" + s.port;
				}
			} else {
				title = s.ip + ":" + s.port;
			}
		} else if (s.response instanceof UnconnectedPing.UnconnectedPingResult) {
			title = ((UnconnectedPing.UnconnectedPingResult)s.response).getServerName();
		} else {//Unreachable
			title = s.ip + ":" + s.port;
		}

		rv.setTextViewText(R.id.serverName,title);
	}
}
