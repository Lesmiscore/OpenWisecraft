package com.nao20010128nao.Wisecraft.misc.localServerList;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.nao20010128nao.Wisecraft.misc.Server;
import com.nao20010128nao.Wisecraft.misc.Utils;

import java.util.List;

/**
 * Created by lesmi on 17/09/24.
 */

public class PreferenceLocalServerList extends ContextWrapper implements LocalServerList {
    Gson gson=Utils.newGson();

    public PreferenceLocalServerList(Context base) {
        super(base);
    }

    @Override
    public List<Server> load() {
        SharedPreferences pref= Utils.getPreferences(this);
        return Utils.jsonToServers(pref.getString("servers", "[]"));
    }

    @Override
    public void save(List<Server> servers) {
        SharedPreferences pref= Utils.getPreferences(this);
        pref.edit().putString("servers", gson.toJson(servers)).commit();
    }
}
