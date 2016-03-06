package com.nao20010128nao.Wisecraft.widget;
import android.content.*;
import android.appwidget.AppWidgetProvider;
import android.widget.RemoteViews;
import android.appwidget.AppWidgetManager;
import android.app.PendingIntent;
import com.nao20010128nao.Wisecraft.R;
import com.nao20010128nao.Wisecraft.ServerListActivity;
import java.util.Map;
import com.google.gson.Gson;
import android.preference.PreferenceManager;
import java.util.HashMap;
import com.nao20010128nao.Wisecraft.provider.MultiServerPingProvider;
import com.nao20010128nao.Wisecraft.provider.ServerPingProvider;
import com.nao20010128nao.MCPing.pe.FullStat;

import static com.nao20010128nao.Wisecraft.Utils.*;
import com.nao20010128nao.MCPing.pc.Reply;
import com.nao20010128nao.Wisecraft.misc.SprPair;
import com.nao20010128nao.Wisecraft.pingEngine.UnconnectedPing;

public class ServerInfoWidget extends AppWidgetProvider {
	Map<String,ServerListActivity.Server> servers;
	MultiServerPingProvider spp=new MultiServerPingProvider(2);
    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // ウィジェットレイアウトの初期化
		servers=new Gson().fromJson(PreferenceManager.getDefaultSharedPreferences(context).getString("widget","{}"),HashMap.class);
		for(int awi:appWidgetIds){
			final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			
			if(servers.containsKey(awi+"")){
				final int awiL=awi;
				spp.putInQueue(servers.get(awi),new ServerPingProvider.PingHandler(){
					public void onPingArrives(ServerListActivity.ServerStatus res){
						updateTitle(remoteViews,res);
						remoteViews.setTextViewText(R.id.serverIp,res+"");
						appWidgetManager.updateAppWidget(awiL,remoteViews);
					}
					public void onPingFailed(ServerListActivity.Server res){
						remoteViews.setTextViewText(R.id.serverName,context.getResources().getString(R.string.notResponding));
						remoteViews.setTextViewText(R.id.serverPlayersCount,context.getResources().getString(R.string.notResponding));
						appWidgetManager.updateAppWidget(awiL,remoteViews);
					}
				});
			}else{
				context.startActivity(new Intent(context,AskServerActivity.class).putExtra("widid",awi).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
			}
			
			remoteViews.setTextViewText(R.id.serverName,context.getResources().getString(R.string.working));
			remoteViews.setTextViewText(R.id.serverIp,servers.get(awi+"")+"");
			remoteViews.setTextViewText(R.id.serverPlayersCount,context.getResources().getString(R.string.working));
			
			appWidgetManager.updateAppWidget(awi,remoteViews);
		}
    }

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO: Implement this method
		super.onDeleted(context, appWidgetIds);
		servers=new Gson().fromJson(PreferenceManager.getDefaultSharedPreferences(context).getString("widget","{}"),HashMap.class);
		for(int awi:appWidgetIds)servers.remove(awi+"");
		PreferenceManager.getDefaultSharedPreferences(context).edit().putString("widget",new Gson().toJson(servers)).commit();
	}

	@Override
	public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
		// TODO: Implement this method
		super.onRestored(context, oldWidgetIds, newWidgetIds);
		servers=new Gson().fromJson(PreferenceManager.getDefaultSharedPreferences(context).getString("widget","{}"),HashMap.class);
		for(int i=0;i<oldWidgetIds.length;i++){
			int old_=oldWidgetIds[i];
			int new_=newWidgetIds[i];
			servers.put(new_+"",servers.remove(old_+""));
		}
		PreferenceManager.getDefaultSharedPreferences(context).edit().putString("widget",new Gson().toJson(servers)).commit();
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
