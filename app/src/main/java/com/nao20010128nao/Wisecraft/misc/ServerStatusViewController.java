package com.nao20010128nao.Wisecraft.misc;

import android.content.*;

public interface ServerStatusViewController<T extends ServerStatusViewController> {
	public T setStatColor(int color);
	public T setServerPlayers(String s);
	public T setServerPlayers(int s);
	public T setServerPlayers();
	public T setServerPlayers(Number count,Number max);
	public T setServerPlayers(String count,String max);
	public T setServerAddress(String s);
	public T setServerAddress(Server s);
	public T setPingMillis(String s);
	public T setPingMillis(long s);
	public T setServerName(CharSequence s);
	public T setDarkness(boolean dark);
	public T setTextColor(int color);
	public T setTarget(int mode);
	public T setTarget(String target);
	public T setServer(Server server);
	public T setServerTitle(CharSequence text);
	public T hideServerTitle();
	public T showServerTitle();
	public T setServerName(Server s);
	public T hideServerPlayers();
	public T pending(Server sv,Context sla);
	public T offline(Server sv,Context sla);
	public T online(Context context);
	public T setTag(Object o);
	public T setSelected(boolean selected);
	public Object getTag();
}
