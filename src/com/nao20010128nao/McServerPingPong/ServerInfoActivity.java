package com.nao20010128nao.McServerPingPong;

import android.os.*;
import android.support.v4.app.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.lang.ref.*;
import java.math.*;
import java.util.*;
import query.*;

public class ServerInfoActivity extends FragmentActivity {
	static WeakReference<ServerInfoActivity> instance=new WeakReference(null);
	public static ServerListActivity.ServerStatus stat;
	String ip;
	int port;

	List<Thread> t=new ArrayList<>();
	ListView players,data,plugins;
	FragmentTabHost fth;
	TabHost.TabSpec playersF,dataF,pluginsF;

	ArrayAdapter<String> adap,adap3;
	ArrayAdapter<Map.Entry<String,String>> adap2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		instance = new WeakReference(this);

		setContentView(R.layout.tabs);
		fth = (FragmentTabHost)findViewById(android.R.id.tabhost);
		fth.setup(this, getSupportFragmentManager(), R.id.container);

		playersF = fth.newTabSpec("playersList");
		playersF.setIndicator(getResources().getString(R.string.players));
		fth.addTab(playersF, PlayersFragment.class, null);

		dataF = fth.newTabSpec("dataList");
		dataF.setIndicator(getResources().getString(R.string.data));
		fth.addTab(dataF, DataFragment.class, null);

		pluginsF = fth.newTabSpec("pluginsList");
		pluginsF.setIndicator(getResources().getString(R.string.plugins));
		fth.addTab(pluginsF, PluginsFragment.class, null);
		
		adap = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
		adap2 = new ArrayAdapter<Map.Entry<String,String>>(this, 0, new ArrayList<Map.Entry<String,String>>()){
			public View getView(int pos, View v, ViewGroup ignore) {
				if (v == null)
					v = getLayoutInflater().inflate(R.layout.data, null);
				((TextView)v.findViewById(R.id.k)).setText(getItem(pos).getKey());
				((TextView)v.findViewById(R.id.v)).setText(getItem(pos).getValue());
				return v;
			}
		};
		adap3 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
		
		update(stat.response);
	}
	public synchronized void update(final QueryResponseUniverse resp) {
		final ArrayList<String> sort=new ArrayList<>(resp.getPlayerList());
		Collections.sort(sort);
		final String title;
		Map<String,String> m=resp.getData();
		if (m.containsKey("hostname")) {
			title = deleteDecorations(m.get("hostname"));
		} else if (m.containsKey("motd")) {
			title = deleteDecorations(m.get("motd"));
		} else {
			title = ip + ":" + port;
		}
		adap.clear();
		adap.addAll(sort);
		adap2.clear();
		adap2.addAll(resp.getData().entrySet());
		adap3.clear();
		if(resp.getData().containsKey("plugins")){
			adap3.addAll(resp.getData().get("plugins").split("\\: ")[1].split("\\; "));
		}
		setTitle(title);
	}
	static void setPlayersView(ListView lv) {
		instance.get().setPlayersView_(lv);
	}
	static void setDataView(ListView lv) {
		instance.get().setDataView_(lv);
	}
	static void setPluginsView(ListView lv) {
		instance.get().setPluginsView_(lv);
	}
	
	void setPlayersView_(ListView lv) {
		players = lv;
		lv.setAdapter(adap);
	}
	void setDataView_(ListView lv) {
		data = lv;
		lv.setAdapter(adap2);
	}
	void setPluginsView_(ListView lv) {
		plugins = lv;
		lv.setAdapter(adap3);
	}

	static String deleteDecorations(String decorated) {
		StringBuilder sb=new StringBuilder();
		char[] chars=decorated.toCharArray();
		int offset=0;
		while (chars.length > offset) {
			if (chars[offset] == '§') {
				offset += 2;
				continue;
			}
			sb.append(chars[offset]);
			offset++;
		}
		Log.d("esc", sb.toString());
		return sb.toString();
	}
	public static class PlayersFragment extends android.support.v4.app.Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO: Implement this method
			ListView lv=(ListView) inflater.inflate(R.layout.players_tab, null, false);
			setPlayersView(lv);
			return lv;
		}
	}
	public static class DataFragment extends android.support.v4.app.Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO: Implement this method
			ListView lv=(ListView) inflater.inflate(R.layout.data_tab, null, false);
			setDataView(lv);
			return lv;
		}
	}
	public static class PluginsFragment extends android.support.v4.app.Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO: Implement this method
			ListView lv=(ListView) inflater.inflate(R.layout.players_tab, null, false);
			setPluginsView(lv);
			return lv;
		}
	}
}
