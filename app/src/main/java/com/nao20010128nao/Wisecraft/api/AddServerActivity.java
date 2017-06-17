package com.nao20010128nao.Wisecraft.api;

import android.content.*;
import android.net.*;
import android.os.*;
import android.preference.*;
import android.view.*;
import android.widget.*;
import com.google.gson.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.activity.*;
import com.nao20010128nao.Wisecraft.misc.*;

import java.util.*;

public class AddServerActivity extends ApiBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent values = getIntent();
        final Server result = new Server();
        if (values.hasExtra(ApiActions.ADD_SERVER_IP) & (values.hasExtra(ApiActions.ADD_SERVER_ISPC) | values.hasExtra(ApiActions.ADD_SERVER_MODE))) {
            result.mode = Utils.getModeFromIntent(values);
            int port = -1;
            if (values.hasExtra(ApiActions.ADD_SERVER_PORT)) {
                port = values.getIntExtra(ApiActions.ADD_SERVER_PORT, -1);
            } else {
                switch (result.mode) {
                    case 0:
                        port = 19132;
                        break;
                    case 1:
                        port = 25565;
                        break;
                }
            }
            if (port < 1) {
                finish();
                return;
            }
            result.port = port;
            result.ip = values.getStringExtra(ApiActions.ADD_SERVER_IP);
        } else if (values.getData() != null) {
            Uri data = values.getData();
            if ("wisecraft".equalsIgnoreCase(data.getScheme())) {
                List<String> path = data.getPathSegments();
                if (path.size() < 3) {
                    finish();
                    return;
                }
                result.ip = path.get(0);
                result.port = Integer.valueOf(path.get(1));
                result.mode = Utils.parseModeName(path.get(2));
            } else if ("mccqp".equalsIgnoreCase(data.getScheme())) {
                result.ip = data.getHost();
                result.port = data.getPort();
                result.mode = 1;//always PC
                if (result.port == -1) result.port = 25565;
            } else {
                finish();
                return;
            }
        }
        if (result.mode < 0 | result.mode > 1) {
            finish();
            return;
        }

        setContentView(R.layout.api_server_add_activity);
        ListView lv = (ListView) findViewById(R.id.list);
        KVListAdapter<String, String> adapter = new KVListAdapter<>(this);
        lv.setAdapter(adapter);
        adapter.add(getResources().getString(R.string.ipAddress), result.ip);
        adapter.add(getResources().getString(R.string.port_single), result.port + "");
        adapter.add(getResources().getString(R.string.kind), getResources().getString(result.mode == 0 ? R.string.peServer : R.string.pcServer));

        findViewById(R.id.yes).setOnClickListener(v -> {
            if (ServerListActivity.instance.get() != null) {
                //add a server at ServerListActivity for consistency
                ServerListActivity.instance.get().addIntoList(result);
            } else {
                //deserialize a json, add a server, then save it
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(AddServerActivity.this);
                List<Server> servers = new Gson().fromJson(pref.getString("servers", "[]"), ServerListArrayList.class);
                servers.add(result);
                pref.edit().putString("servers", new Gson().toJson(servers)).commit();
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
