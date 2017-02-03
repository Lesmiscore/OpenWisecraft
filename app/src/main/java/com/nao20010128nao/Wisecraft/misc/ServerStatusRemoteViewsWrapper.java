package com.nao20010128nao.Wisecraft.misc;

import android.content.*;
import android.support.v4.content.*;
import android.view.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.widget.*;
import java.util.*;
import android.appwidget.*;

public class ServerStatusRemoteViewsWrapper implements ServerStatusViewController<ServerStatusRemoteViewsWrapper> 
{
	RemoteViews control;Context c;int style,wid;
	public ServerStatusRemoteViewsWrapper(Context c,int wid){
		PingWidget.WidgetData widgetData=PingWidget.getWidgetData(c,wid);
		style=widgetData.style;
		control=new RemoteViews(c.getPackageName(),PingWidget.styleToId(style));
		this.c=c;
		this.wid=wid;
	}
	public ServerStatusRemoteViewsWrapper setStatColor(int color){
		return this;
	}
	public ServerStatusRemoteViewsWrapper setServerPlayers(String s){
		control.setTextViewText(R.id.serverPlayers,s);
		return this;
	}
	public ServerStatusRemoteViewsWrapper setServerPlayers(int s){
		control.setTextViewText(R.id.serverPlayers,c.getResources().getString(s));
		return this;
	}
	public ServerStatusRemoteViewsWrapper setServerPlayers(){
		return setServerPlayers("-/-");
	}
	public ServerStatusRemoteViewsWrapper setServerPlayers(Number count,Number max){
		return setServerPlayers(count+"/"+max);
	}
	public ServerStatusRemoteViewsWrapper setServerPlayers(String count,String max){
		return setServerPlayers(count+"/"+max);
	}
	public ServerStatusRemoteViewsWrapper setServerPlayers(List<String> playersList) {
		control.setRemoteAdapter(R.id.players,new Intent(c,PingWidget.ListViewUpdater.class).putExtra("list",new ArrayList<String>(playersList)));
		AppWidgetManager.getInstance(c).notifyAppWidgetViewDataChanged(wid,R.id.players);
		return this;
	}
	public ServerStatusRemoteViewsWrapper setServerAddress(String s){
		control.setTextViewText(R.id.serverAddress,s);
		return this;
	}
	public ServerStatusRemoteViewsWrapper setServerAddress(Server s){
		return setServerAddress(s.toString());
	}
	public ServerStatusRemoteViewsWrapper setPingMillis(String s){
		control.setTextViewText(R.id.pingMillis,s);
		return this;
	}
	public ServerStatusRemoteViewsWrapper setPingMillis(long s){
		return setPingMillis(s+" ms");
	}
	public ServerStatusRemoteViewsWrapper setServerName(CharSequence s){
		control.setTextViewText(R.id.serverName,s);
		return this;
	}
	public ServerStatusRemoteViewsWrapper setDarkness(boolean dark){
		return this;
	}
	public ServerStatusRemoteViewsWrapper setTextColor(int color){
		return this;
	}
	public ServerStatusRemoteViewsWrapper setTarget(int mode){
		return setTarget(mode==0?"PE":"PC");
	}
	public ServerStatusRemoteViewsWrapper setTarget(String target){
		control.setTextViewText(R.id.target,target);
		return this;
	}
	public ServerStatusRemoteViewsWrapper setServer(Server server){
		setServerAddress(server);
		setTarget(server.mode);
		return this;
	}
	public ServerStatusRemoteViewsWrapper setServerTitle(CharSequence text){
		return this;
	}
	public ServerStatusRemoteViewsWrapper hideServerTitle(){
		return this;
	}
	public ServerStatusRemoteViewsWrapper showServerTitle(){
		return this;
	}
	public ServerStatusRemoteViewsWrapper setServerName(Server s){
		return setServerName(s.toString());
	}
	public ServerStatusRemoteViewsWrapper hideServerPlayers(){
		control.setViewVisibility(R.id.serverPlayers,View.GONE);
		return this;
	}
	public ServerStatusRemoteViewsWrapper setTag(Object o){
		return this;
	}
	public Object getTag(){
		return control;
	}
	public ServerStatusRemoteViewsWrapper setSelected(boolean selected) {
		return this;
	}
	public ServerStatusRemoteViewsWrapper pending(Server sv,Context sla){
		return setServerName(sla.getResources().getString(R.string.working))
			.setPingMillis(sla.getResources().getString(R.string.working))
			.setServer(sv).setServerPlayers()
			.setStatColor(ContextCompat.getColor(sla, R.color.stat_pending));
	}
	public ServerStatusRemoteViewsWrapper offline(Server sv,Context sla){
		return setStatColor(ContextCompat.getColor(sla, R.color.stat_error))
			.setServerName(sv.ip + ":" + sv.port)
			.setPingMillis(sla.getResources().getString(R.string.notResponding))
			.setServerPlayers().setServer(sv);
	}
	public ServerStatusRemoteViewsWrapper online(Context context){
		return setStatColor(ContextCompat.getColor(context, R.color.stat_ok));
	}
	public ServerStatusRemoteViewsWrapper unknown(Context context, Server sv) {
		return setStatColor(ContextCompat.getColor(context, R.color.stat_ok))
			.setServerName(sv.ip + ":" + sv.port)
			.setPingMillis(context.getResources().getString(R.string.notResponding))
			.setServerPlayers().setServer(sv);
	}
}
