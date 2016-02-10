package com.nao20010128nao.Wisecraft;

import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.view.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.misc.*;
import java.lang.ref.*;
import java.util.*;
import uk.co.chrisjenx.calligraphy.*;

import static com.nao20010128nao.Wisecraft.Utils.*;
import android.graphics.drawable.*;
import com.nao20010128nao.MCPing.*;
import com.nao20010128nao.MCPing.pe.*;
import com.nao20010128nao.MCPing.pc.*;

public class ServerInfoActivity extends FragmentActivity {
	static WeakReference<ServerInfoActivity> instance=new WeakReference(null);
	public static ServerListActivity.ServerStatus stat;
	String ip;
	int port;
	boolean nonUpd,hidePlayer,hideData,hidePlugins;
	
	TipController tc;

	List<Thread> t=new ArrayList<>();
	ListView players,data,plugins;
	FragmentTabHost fth;
	TabHost.TabSpec playersF,dataF,pluginsF;

	ArrayAdapter<String> adap,adap3;
	ArrayAdapter<Map.Entry<String,String>> adap2;
	
	/*Only for PC servers*/
	ImageView serverIcon;
	TextView serverName;
	Drawable serverIconObj;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		instance = new WeakReference(this);
		
		if(stat==null){
			finish();
			return;
		}
		
		setContentView(R.layout.tabs);
		fth = (FragmentTabHost)findViewById(android.R.id.tabhost);
		fth.setup(this, getSupportFragmentManager(), R.id.container);

		hideData   =getIntent().getBooleanExtra("nonDetails",false);
		hidePlayer =getIntent().getBooleanExtra("nonPlayers",false);
		hidePlugins=getIntent().getBooleanExtra("nonPlugins",false);
		
		if(!hidePlayer){
			playersF = fth.newTabSpec("playersList");
			playersF.setIndicator(getResources().getString(R.string.players));
			fth.addTab(playersF, PlayersFragment.class, null);
		}

		if(!hideData){
			dataF = fth.newTabSpec("dataList");
			dataF.setIndicator(getResources().getString(R.string.data));
			fth.addTab(dataF, DataFragment.class, null);
		}

		if(!hidePlugins){
			pluginsF = fth.newTabSpec("pluginsList");
			pluginsF.setIndicator(getResources().getString(R.string.plugins));
			fth.addTab(pluginsF, PluginsFragment.class, null);
		}
		
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
		
		nonUpd=getIntent().getBooleanExtra("nonUpd",false);
		
		/*tc=new TipController(this);
		if(stat.isPC){
			tc.visible(true).text(R.string.serverInfoPCMessage);
		}*/
		
		ip=stat.ip;
		port=stat.port;
		
		update(stat.response);
	}
	public synchronized void update(final ServerPingResult resp) {
		if(resp instanceof FullStat){
			FullStat fs=(FullStat)resp;
			final ArrayList<String> sort=new ArrayList<>(fs.getPlayerList());
			Collections.sort(sort);
			final String title;
			Map<String,String> m=fs.getData();
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
			adap2.addAll(fs.getData().entrySet());
			adap3.clear();
			if(fs.getData().containsKey("plugins")){
				String[] data=fs.getData().get("plugins").split("\\: ");
				if(data.length>=2)
					adap3.addAll(data[1].split("\\; "));
			}
			setTitle(title);
		}else if(resp instanceof Reply){
			Reply rep=(Reply)resp;
			if(rep.description==null){
				setTitle(stat.ip + ":" + stat.port);
			}else{
				setTitle(deleteDecorations(rep.description));
			}
			final ArrayList<String> sort=new ArrayList<>();
			for(Reply.Player o:rep.players.getSample()){
				sort.add(o.getName());
			}
			Collections.sort(sort);
			adap.clear();
			adap.addAll(sort);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO: Implement this method
		if(!nonUpd)
			menu.add(Menu.NONE, 0, 0, R.string.update);
		//menu.add(Menu.NONE, 0, 1, "メニュー2");
		
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO: Implement this method
		switch(featureId){
			case 0://Update
				setResult(Constant.ACTIVITY_RESULT_UPDATE);
				finish();//ServerListActivity updates the stat
				return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	static void setPlayersView(ListView lv) {
		instance.get().setPlayersView_(lv);
	}
	static void setDataView(View lv) {
		instance.get().setDataView_(lv);
	}
	static void setPluginsView(ListView lv) {
		instance.get().setPluginsView_(lv);
	}
	
	void setPlayersView_(ListView lv) {
		players = lv;
		lv.setAdapter(adap);
	}
	void setDataView_(View lv) {
		data = (ListView)lv.findViewById(R.id.data);
		if(stat.isPC){
			serverIcon=(ImageView)lv.findViewById(R.id.serverIcon);
			serverName=(TextView)lv.findViewById(R.id.serverTitle);
		}
		data.setAdapter(adap2);
	}
	void setPluginsView_(ListView lv) {
		plugins = lv;
		lv.setAdapter(adap3);
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}
	public static class PlayersFragment extends BaseFragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO: Implement this method
			ListView lv=(ListView) inflater.inflate(R.layout.players_tab, null, false);
			setPlayersView(lv);
			return lv;
		}
	}
	public static class DataFragment extends BaseFragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO: Implement this method
			View lv= inflater.inflate(stat.isPC?R.layout.data_tab_pc:R.layout.data_tab, null, false);
			setDataView(lv);
			return lv;
		}
	}
	public static class PluginsFragment extends BaseFragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO: Implement this method
			ListView lv=(ListView) inflater.inflate(R.layout.players_tab, null, false);
			setPluginsView(lv);
			return lv;
		}
	}
}
