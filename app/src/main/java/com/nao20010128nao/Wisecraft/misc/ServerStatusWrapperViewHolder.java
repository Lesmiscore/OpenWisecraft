package com.nao20010128nao.Wisecraft.misc;
import android.content.Context;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import com.nao20010128nao.Wisecraft.R;
import com.nao20010128nao.Wisecraft.misc.view.ExtendedImageView;
import android.widget.TextView;
import android.view.View;

public class ServerStatusWrapperViewHolder extends FindableViewHolder
{
	public ServerStatusWrapperViewHolder(Context context,boolean isGrid,ViewGroup parent){
		super(LayoutInflater.from(context).inflate(isGrid?R.layout.quickstatus_grid:R.layout.quickstatus,parent,false));
	}
	public ServerStatusWrapperViewHolder setStatColor(int color){
		((ExtendedImageView)findViewById(R.id.statColor)).setColor(color);
		return this;
	}
	public ServerStatusWrapperViewHolder setServerPlayers(String s){
		((TextView)findViewById(R.id.serverPlayers)).setText(s);
		return this;
	}
	public ServerStatusWrapperViewHolder setServerPlayers(int s){
		((TextView)findViewById(R.id.serverPlayers)).setText(s);
		return this;
	}
	public ServerStatusWrapperViewHolder setServerPlayers(Number count,Number max){
		return setServerPlayers(count+"/"+max);
	}
	public ServerStatusWrapperViewHolder setServerPlayers(String count,String max){
		return setServerPlayers(count+"/"+max);
	}
	public ServerStatusWrapperViewHolder setServerAddress(String s){
		((TextView)findViewById(R.id.serverAddress)).setText(s);
		return this;
	}
	public ServerStatusWrapperViewHolder setServerAddress(Server s){
		return setServerAddress(s.toString());
	}
	public ServerStatusWrapperViewHolder setPingMillis(String s){
		((TextView)findViewById(R.id.pingMillis)).setText(s);
		return this;
	}
	public ServerStatusWrapperViewHolder setPingMillis(long s){
		return setPingMillis(s+" ms");
	}
	public ServerStatusWrapperViewHolder setServerName(CharSequence s){
		((TextView)findViewById(R.id.serverName)).setText(s);
		return this;
	}
	public ServerStatusWrapperViewHolder setDarkness(boolean dark){
		int color=dark?0xff_ffffff:0xff_000000;
		for(int i:new int[]{R.id.serverPlayers,R.id.serverAddress,R.id.pingMillis,R.id.serverName})
			((TextView)findViewById(i)).setTextColor(color);
		View target=findViewById(R.id.target);
		if(target!=null)((TextView)target).setTextColor(color);
		return this;
	}
	public ServerStatusWrapperViewHolder setTarget(int mode){
		return setTarget(mode==0?"PE":"PC");
	}
	public ServerStatusWrapperViewHolder setTarget(String target){
		View view=findViewById(R.id.target);
		if(view!=null)((TextView)view).setText(target);
		return this;
	}
	public ServerStatusWrapperViewHolder setServer(Server server){
		setServerAddress(server);
		setTarget(server.mode);
		return this;
	}
}
