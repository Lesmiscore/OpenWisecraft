package com.nao20010128nao.Wisecraft.misc;

import android.content.*;

import java.util.*;

public interface ServerStatusViewController<T extends ServerStatusViewController> {
    T setStatColor(int color);

    T setServerPlayers(String s);

    T setServerPlayers(int s);

    T setServerPlayers();

    T setServerPlayers(Number count, Number max);

    T setServerPlayers(String count, String max);

    T setServerPlayers(List<String> playersList);

    T setServerAddress(String s);

    T setServerAddress(Server s);

    T setPingMillis(String s);

    T setPingMillis(long s);

    T setServerName(CharSequence s);

    T setDarkness(boolean dark);

    T setTextColor(int color);

    T setTarget(int mode);

    T setTarget(String target);

    T setServer(Server server);

    T setServerTitle(CharSequence text);

    T hideServerTitle();

    T showServerTitle();

    T setServerName(Server s);

    T hideServerPlayers();

    T pending(Server sv, Context sla);

    T offline(Server sv, Context sla);

    T online(Context context);

    T unknown(Context context, Server sv);

    T setTag(Object o);

    T setSelected(boolean selected);

    Object getTag();
}
