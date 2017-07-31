package com.nao20010128nao.Wisecraft.api;

import android.content.*;
import android.net.*;
import android.os.*;
import android.preference.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import com.annimon.stream.*;
import com.google.gson.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.activity.*;
import com.nao20010128nao.Wisecraft.misc.*;

import java.util.*;

public class AddMultipleServersActivity extends ApiBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent input = getIntent();
        if (!input.hasExtra(ApiActions.SERVERS_COUNT)) {
            finish();
            return;
        }
        List<Server> servers = Stream.range(0, input.getIntExtra(ApiActions.SERVERS_COUNT, -1) - 1)
            .map(a -> ApiActions.SERVER_ + a)
            .map(input::getBundleExtra)
            .map(entry -> {
                final Server result = new Server();
                if (entry.containsKey(ApiActions.ADD_SERVER_IP) && entry.containsKey(ApiActions.ADD_SERVER_MODE)) {
                    result.mode = Utils.getModeFromObject(entry.get(ApiActions.ADD_SERVER_MODE));
                    int port = -1;
                    if (entry.containsKey(ApiActions.ADD_SERVER_PORT)) {
                        port = entry.getInt(ApiActions.ADD_SERVER_PORT, -1);
                    } else {
                        switch (result.mode) {
                            case PE:
                                port = 19132;
                                break;
                            case PC:
                                port = 25565;
                                break;
                        }
                    }
                    if (port < 1) {
                        return null;
                    }
                    result.port = port;
                    result.ip = entry.getString(ApiActions.ADD_SERVER_IP);
                    return result;
                }
                return null;
            })
            .filter(Utils::nonNull)
            .filterNot((ServerListActivity.instance.get() != null ? ServerListActivity.instance.get().getServers() :
                Utils.newGson().fromJson(Utils.getPreferences(AddMultipleServersActivity.this).getString("servers", "[]"), ServerListArrayList.class))::contains)
            .toList();
        if(servers.isEmpty()){
            finish();
            return;
        }
        List<Boolean> selections = Stream.of(servers).map(a -> true).toList();


        setContentView(R.layout.api_server_add_multiple_activity);
        RecyclerView lv = (RecyclerView) findViewById(R.id.list);

        lv.setAdapter(new RecyclerView.Adapter<ServerStatusWrapperViewHolder>() {
            @Override
            public ServerStatusWrapperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ServerStatusWrapperViewHolder(AddMultipleServersActivity.this, false, parent);
            }

            @Override
            public void onBindViewHolder(ServerStatusWrapperViewHolder holder, int position) {
                if (selections.get(position)) {
                    holder
                        .online(AddMultipleServersActivity.this)
                        .setServerPlayers(getResources().getString(R.string.thisWillBeAddedApi));
                } else {
                    holder
                        .offline(servers.get(position), AddMultipleServersActivity.this)
                        .setServerPlayers(getResources().getString(R.string.thisWontBeAddedApi));
                }
                holder
                    .hideServerPlayers()
                    .hideServerTitle()
                    .setPingMillis("")
                    .setServerAddress(servers.get(position))
                    .setServerName(getResources().getString(R.string.noDot, position + 1))
                    .setTarget(servers.get(position).mode);
                Utils.applyHandlersForViewTree(holder.itemView, v -> {
                    selections.set(position, selections.get(position));
                    notifyItemChanged(position);
                });
            }

            @Override
            public int getItemCount() {
                return servers.size();
            }
        });

        findViewById(R.id.yes).setOnClickListener(v -> {
            if (ServerListActivity.instance.get() != null) {
                //add servers at ServerListActivity for consistency
                Stream.of(servers)
                    .filter(IteratorUtils.booleanListToPredicate(selections))
                    .forEach(ServerListActivity.instance.get()::addIntoList);
            } else {
                //deserialize a json, add servers, then save it
                Gson gson = Utils.newGson();
                SharedPreferences pref = Utils.getPreferences(AddMultipleServersActivity.this);
                List<Server> slave = gson.fromJson(pref.getString("servers", "[]"), ServerListArrayList.class);
                Stream.of(servers)
                    .filter(IteratorUtils.booleanListToPredicate(selections))
                    .forEach(slave::add);
                pref.edit().putString("servers", gson.toJson(slave)).commit();
            }
            finish();
        });
        findViewById(R.id.no).setOnClickListener(v -> finish());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(lp);
    }
}
