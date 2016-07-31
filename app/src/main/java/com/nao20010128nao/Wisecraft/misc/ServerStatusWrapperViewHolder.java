package com.nao20010128nao.Wisecraft.misc;
import android.content.*;
import android.view.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.view.*;
import android.support.v4.content.*;

public class ServerStatusWrapperViewHolder extends FindableViewHolder
{
	public static final int[] COLORED_TEXTVIEWS=new int[]{R.id.serverPlayers,R.id.serverAddress,R.id.pingMillis,R.id.serverName,R.id.target};
	public static final int[] ALL_VIEWS=new int[]{R.id.serverPlayers,R.id.serverAddress,R.id.pingMillis,R.id.serverName,R.id.target,R.id.statColor};
	
	PreloadedViews preload;
	
	public ServerStatusWrapperViewHolder(Context context,boolean isGrid,ViewGroup parent){
		super(LayoutInflater.from(context).inflate(isGrid?R.layout.quickstatus_grid:R.layout.quickstatus,parent,false));
		preload=new PreloadedViews((ViewGroup)itemView,ALL_VIEWS);
	}
	public ServerStatusWrapperViewHolder setStatColor(int color){
		((ExtendedImageView)preload.getView(R.id.statColor)).setColor(color);
		return this;
	}
	public ServerStatusWrapperViewHolder setServerPlayers(String s){
		((TextView)preload.getView(R.id.serverPlayers)).setText(s);
		return this;
	}
	public ServerStatusWrapperViewHolder setServerPlayers(int s){
		((TextView)preload.getView(R.id.serverPlayers)).setText(s);
		return this;
	}
	public ServerStatusWrapperViewHolder setServerPlayers(){
		return setServerPlayers("-/-");
	}
	public ServerStatusWrapperViewHolder setServerPlayers(Number count,Number max){
		return setServerPlayers(count+"/"+max);
	}
	public ServerStatusWrapperViewHolder setServerPlayers(String count,String max){
		return setServerPlayers(count+"/"+max);
	}
	public ServerStatusWrapperViewHolder setServerAddress(String s){
		((TextView)preload.getView(R.id.serverAddress)).setText(s);
		return this;
	}
	public ServerStatusWrapperViewHolder setServerAddress(Server s){
		return setServerAddress(s.toString());
	}
	public ServerStatusWrapperViewHolder setPingMillis(String s){
		((TextView)preload.getView(R.id.pingMillis)).setText(s);
		return this;
	}
	public ServerStatusWrapperViewHolder setPingMillis(long s){
		return setPingMillis(s+" ms");
	}
	public ServerStatusWrapperViewHolder setServerName(CharSequence s){
		((TextView)preload.getView(R.id.serverName)).setText(s);
		return this;
	}
	public ServerStatusWrapperViewHolder setDarkness(boolean dark){
		int color=dark?0xff_ffffff:0xff_000000;
		for(int i:COLORED_TEXTVIEWS)
			if(findViewById(i)!=null)
				((TextView)preload.getView(i)).setTextColor(color);
		return this;
	}
	public ServerStatusWrapperViewHolder setTarget(int mode){
		return setTarget(mode==0?"PE":"PC");
	}
	public ServerStatusWrapperViewHolder setTarget(String target){
		View view=preload.getView(R.id.target);
		if(view!=null)((TextView)view).setText(target);
		return this;
	}
	public ServerStatusWrapperViewHolder setServer(Server server){
		setServerAddress(server);
		setTarget(server.mode);
		return this;
	}
	
	
	//ATTENTION: Don't add this method to setServer(Server) because this is used for a unexpected mode
	public ServerStatusWrapperViewHolder setServerName(Server s){
		return setServerName(s.toString());
	}
	
	public ServerStatusWrapperViewHolder hideServerPlayers(){
		preload.getView(R.id.serverPlayers).setVisibility(View.GONE);
		return this;
	}
	
	public ServerStatusWrapperViewHolder setTag(Object o){
		itemView.setTag(o);
		return this;
	}
	
	public Object getTag(){
		return itemView.getTag();
	}
	
	public ServerStatusWrapperViewHolder pending(Server sv,Context sla){
		return setServerName(sla.getResources().getString(R.string.working))
			.setPingMillis(sla.getResources().getString(R.string.working))
			.setServer(sv).setServerPlayers()
			.setStatColor(ContextCompat.getColor(sla, R.color.stat_pending));
	}
	
	public ServerStatusWrapperViewHolder offline(Server sv,Context sla){
		return setStatColor(ContextCompat.getColor(sla, R.color.stat_error))
			.setServerName(sv.ip + ":" + sv.port)
			.setPingMillis(sla.getResources().getString(R.string.notResponding))
			.setServerPlayers("-/-").setServer(sv);
	}
}
