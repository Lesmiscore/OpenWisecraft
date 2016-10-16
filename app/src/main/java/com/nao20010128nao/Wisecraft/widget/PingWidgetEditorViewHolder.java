package com.nao20010128nao.Wisecraft.widget;
import android.content.*;
import android.support.v4.content.*;
import android.view.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.view.*;

public class PingWidgetEditorViewHolder extends FindableViewHolder implements ServerStatusViewController<PingWidgetEditorViewHolder>
{
	public PingWidgetEditorViewHolder(Context context,ViewGroup parent){
		super(LayoutInflater.from(context).inflate(R.layout.widget_editor_widget_entry,parent,false));
	}
	public PingWidgetEditorViewHolder setStatColor(int color){
		((ExtendedImageView)findViewById(R.id.statColor)).setColor(color);
		return this;
	}
	public PingWidgetEditorViewHolder setServerPlayers(String s){
		return this;
	}
	public PingWidgetEditorViewHolder setServerPlayers(int s){
		return this;
	}
	public PingWidgetEditorViewHolder setServerPlayers(){
		return this;
	}
	public PingWidgetEditorViewHolder setServerPlayers(Number count,Number max){
		return this;
	}
	public PingWidgetEditorViewHolder setServerPlayers(String count,String max){
		return this;
	}
	public PingWidgetEditorViewHolder setServerAddress(String s){
		((TextView)findViewById(R.id.serverAddress)).setText(s);
		return this;
	}
	public PingWidgetEditorViewHolder setServerAddress(Server s){
		return setServerAddress(s.toString());
	}
	public PingWidgetEditorViewHolder setPingMillis(String s){
		return this;
	}
	public PingWidgetEditorViewHolder setPingMillis(long s){
		return this;
	}
	public PingWidgetEditorViewHolder setServerName(CharSequence s){
		return this;
	}
	public PingWidgetEditorViewHolder setDarkness(boolean dark){
		return this;
	}
	public PingWidgetEditorViewHolder setTextColor(int color){
		return this;
	}
	public PingWidgetEditorViewHolder setTarget(int mode){
		return setTarget(mode==0?"PE":"PC");
	}
	public PingWidgetEditorViewHolder setTarget(String target){
		((TextView)findViewById(R.id.target)).setText(target);
		return this;
	}
	public PingWidgetEditorViewHolder setServer(Server server){
		setServerAddress(server);
		setTarget(server.mode);
		return this;
	}
	public PingWidgetEditorViewHolder setServerTitle(CharSequence text){
		return this;
	}
	public PingWidgetEditorViewHolder hideServerTitle(){
		return this;
	}
	public PingWidgetEditorViewHolder showServerTitle(){
		return this;
	}
	
	
	//ATTENTION: Don't add this method to setServer(Server) because this is used for a unexpected mode
	public PingWidgetEditorViewHolder setServerName(Server s){
		return setServerName(s.toString());
	}
	
	public PingWidgetEditorViewHolder hideServerPlayers(){
		return this;
	}
	
	public PingWidgetEditorViewHolder setTag(Object o){
		itemView.setTag(o);
		return this;
	}
	
	public Object getTag(){
		return itemView.getTag();
	}
	
	public PingWidgetEditorViewHolder pending(Server sv,Context sla){
		return setStatColor(ContextCompat.getColor(sla, R.color.stat_pending)).setServer(sv);
	}

	public PingWidgetEditorViewHolder offline(Server sv,Context sla){
		return setStatColor(ContextCompat.getColor(sla, R.color.stat_error)).setServer(sv);
	}

	public PingWidgetEditorViewHolder online(Context context){
		return setStatColor(ContextCompat.getColor(context, R.color.stat_ok));
	}
	
	public PingWidgetEditorViewHolder setSelected(boolean selected) {
		return this;
	}
}
