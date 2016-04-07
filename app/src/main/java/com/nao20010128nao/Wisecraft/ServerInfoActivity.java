package com.nao20010128nao.Wisecraft;

import android.graphics.*;
import android.graphics.drawable.*;
import android.view.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.misc.*;
import java.util.*;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Base64;
import com.nao20010128nao.MCPing.ServerPingResult;
import com.nao20010128nao.MCPing.pc.Reply;
import com.nao20010128nao.MCPing.pc.Reply19;
import com.nao20010128nao.MCPing.pe.FullStat;
import com.nao20010128nao.Wisecraft.misc.compat.CompatArrayAdapter;
import com.nao20010128nao.Wisecraft.pingEngine.UnconnectedPing;
import java.lang.ref.WeakReference;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.nao20010128nao.Wisecraft.Utils.*;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ServerInfoActivity extends FragmentActivity {
	static WeakReference<ServerInfoActivity> instance=new WeakReference(null);
	public static List<ServerStatus> stat=new ArrayList<>();
	SharedPreferences pref;
	
	ServerStatus localStat;
	Bundle keeping;
	
	String ip;
	int port;
	boolean nonUpd,hidePlayer,hideData,hidePlugins;

	TipController tc;
	MenuItem updateBtn;

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
	String serverNameStr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		instance = new WeakReference(this);
		pref=PreferenceManager.getDefaultSharedPreferences(this);

		int statOfs=getIntent().getIntExtra("statListOffset",-1);
		
		if(stat.size()>statOfs&statOfs!=-1)localStat=stat.get(statOfs);
		
		if (localStat == null) {
			finish();
			return;
		}

		if(getIntent().hasExtra("object")){
			keeping=getIntent().getBundleExtra("object");
		}
		
		setContentView(R.layout.tabs);
		fth = (FragmentTabHost)findViewById(android.R.id.tabhost);
		fth.setup(this, getSupportFragmentManager(), R.id.container);

		hideData   = getIntent().getBooleanExtra("nonDetails", false);
		hidePlayer = getIntent().getBooleanExtra("nonPlayers", false);
		hidePlugins = getIntent().getBooleanExtra("nonPlugins", false);

		if (!hidePlayer) {
			playersF = fth.newTabSpec("playersList");
			playersF.setIndicator(getResources().getString(R.string.players));
			fth.addTab(playersF, PlayersFragment.class, null);
		}

		if (!hideData) {
			dataF = fth.newTabSpec("dataList");
			dataF.setIndicator(getResources().getString(R.string.data));
			if(localStat.isPC)
				fth.addTab(dataF, DataFragmentPC.class,null);
			else
				fth.addTab(dataF, DataFragmentPE.class,null);
		}

		if (!(hidePlugins|localStat.isPC)) {
			pluginsF = fth.newTabSpec("pluginsList");
			pluginsF.setIndicator(getResources().getString(R.string.plugins));
			fth.addTab(pluginsF, PluginsFragment.class, null);
		}

		adap = new AppBaseArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
		adap2 = new KVListAdapter<>(this);
		adap3 = new AppBaseArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());

		nonUpd = getIntent().getBooleanExtra("nonUpd", false);

		/*tc=new TipController(this);
		 if(stat.isPC){
		 tc.visible(true).text(R.string.serverInfoPCMessage);
		 }*/

		ip = localStat.ip;
		port = localStat.port;
		
		fth.setCurrentTab(getIntent().getIntExtra("offset",0));

		update(localStat.response);
	}
	public synchronized void update(final ServerPingResult resp) {
		if (resp instanceof FullStat) {
			FullStat fs=(FullStat)resp;
			final ArrayList<String> sort=new ArrayList<>(fs.getPlayerList());
			if(pref.getBoolean("sortPlayerNames",true))
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
			CompatArrayAdapter.addAll(adap, sort);
			adap2.clear();
			CompatArrayAdapter.addAll(adap2, fs.getData().entrySet());
			adap3.clear();
			if (fs.getData().containsKey("plugins")) {
				String[] data=fs.getData().get("plugins").split("\\: ");
				if (data.length >= 2){
					ArrayList<String> plugins=new ArrayList<>(Arrays.<String>asList(data[1].split("\\; ")));
					if(pref.getBoolean("sortPluginNames",false))
						Collections.sort(plugins);
					CompatArrayAdapter.addAll(adap3, plugins);
				}
			}
			setTitle(title);
		} else if (resp instanceof Reply) {
			Reply rep=(Reply)resp;
			if (rep.description == null) {
				setTitle(localStat.ip + ":" + localStat.port);
			} else {
				setTitle(deleteDecorations(rep.description));
			}

			if (rep.players.getSample() != null) {
				final ArrayList<String> sort=new ArrayList<>();
				for (Reply.Player o:rep.players.getSample()) {
					sort.add(o.getName());
				}
				if(pref.getBoolean("sortPlayerNames",true))
					Collections.sort(sort);
				adap.clear();
				CompatArrayAdapter.addAll(adap, sort);
			} else {
				adap.clear();
			}

			serverNameStr = deleteDecorations(rep.description);

			if (rep.favicon != null) {
				byte[] image=Base64.decode(rep.favicon.split("\\,")[1], Base64.NO_WRAP);
				Bitmap bmp=BitmapFactory.decodeByteArray(image, 0, image.length);
				serverIconObj = new BitmapDrawable(bmp);
			} else {
				serverIconObj = new ColorDrawable(Color.TRANSPARENT);
			}

			adap2.clear();
			Map<String,String> data=new HashMap<>();
			data.put(getResources().getString(R.string.pc_maxPlayers), rep.players.getMax() + "");
			data.put(getResources().getString(R.string.pc_nowPlayers), rep.players.getOnline() + "");
			data.put(getResources().getString(R.string.pc_softwareVersion), rep.version.getName());
			data.put(getResources().getString(R.string.pc_protocolVersion), rep.version.getProtocol() + "");
			CompatArrayAdapter.addAll(adap2, data.entrySet());
		} else if (resp instanceof Reply19) {
			Reply19 rep=(Reply19)resp;
			if (rep.description == null) {
				setTitle(localStat.ip + ":" + localStat.port);
			} else {
				setTitle(deleteDecorations(rep.description.text));
			}

			if (rep.players.getSample() != null) {
				final ArrayList<String> sort=new ArrayList<>();
				for (Reply19.Player o:rep.players.getSample()) {
					sort.add(o.getName());
				}
				Collections.sort(sort);
				adap.clear();
				CompatArrayAdapter.addAll(adap, sort);
			} else {
				adap.clear();
			}

			serverNameStr = deleteDecorations(rep.description.text);

			if (rep.favicon != null) {
				byte[] image=Base64.decode(rep.favicon.split("\\,")[1], Base64.NO_WRAP);
				Bitmap bmp=BitmapFactory.decodeByteArray(image, 0, image.length);
				serverIconObj = new BitmapDrawable(bmp);
			} else {
				serverIconObj = new ColorDrawable(Color.TRANSPARENT);
			}

			adap2.clear();
			Map<String,String> data=new HashMap<>();
			data.put(getResources().getString(R.string.pc_maxPlayers), rep.players.getMax() + "");
			data.put(getResources().getString(R.string.pc_nowPlayers), rep.players.getOnline() + "");
			data.put(getResources().getString(R.string.pc_softwareVersion), rep.version.getName());
			data.put(getResources().getString(R.string.pc_protocolVersion), rep.version.getProtocol() + "");
			CompatArrayAdapter.addAll(adap2, data.entrySet());
		} else if (resp instanceof SprPair) {
			SprPair p=(SprPair)resp;
			update(p.getA());
			update(p.getB());
		} else if (resp instanceof UnconnectedPing.UnconnectedPingResult) {
			if (resp == localStat.response) {
				finish();
				Toast.makeText(this, R.string.ucpInfoError, 0).show();
				return;
			} else {
				setTitle(deleteDecorations((((UnconnectedPing.UnconnectedPingResult)resp).getServerName())));
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO: Implement this method
		if (!nonUpd)
			updateBtn=menu.add(Menu.NONE, 0, 0, R.string.update);
		//menu.add(Menu.NONE, 0, 1, "メニュー2");

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO: Implement this method
		switch (item.getItemId()) {
			case 0://Update
				setResultInstead(Constant.ACTIVITY_RESULT_UPDATE,new Intent().putExtra("offset",fth.getCurrentTab()));
				finish();//ServerListActivity updates the stat
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void setResultInstead(int resultCode, Intent data) {
		// TODO: Implement this method
		setResult(resultCode, keeping!=null?data.putExtra("object",keeping):data);
	}

	@Override
	public void finish() {
		// TODO: Implement this method
		super.finish();
	}

	public void setPlayersView(ListView lv) {
		players = lv;
		lv.setAdapter(adap);
	}
	public void setDataView(View lv) {
		data = (ListView)lv.findViewById(R.id.data);
		if (localStat.isPC) {
			serverIcon = (ImageView)lv.findViewById(R.id.serverIcon);
			serverName = (TextView)lv.findViewById(R.id.serverTitle);
			serverIcon.setImageDrawable(serverIconObj);
			serverName.setText(serverNameStr);
		}
		data.setAdapter(adap2);
	}
	public void setPluginsView(ListView lv) {
		plugins = lv;
		lv.setAdapter(adap3);
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}
	public static class PlayersFragment extends BaseFragment<ServerInfoActivity> {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO: Implement this method
			ListView lv=(ListView) inflater.inflate(R.layout.players_tab, null, false);
			getParentActivity().setPlayersView(lv);
			return lv;
		}
	}
	public static class DataFragmentPE extends BaseFragment<ServerInfoActivity> {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO: Implement this method
			View lv= inflater.inflate(R.layout.data_tab, null, false);
			getParentActivity().setDataView(lv);
			return lv;
		}
	}
	public static class DataFragmentPC extends BaseFragment<ServerInfoActivity> {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO: Implement this method
			View lv= inflater.inflate(R.layout.data_tab_pc, null, false);
			getParentActivity().setDataView(lv);
			return lv;
		}
	}
	public static class PluginsFragment extends BaseFragment<ServerInfoActivity> {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO: Implement this method
			ListView lv=(ListView) inflater.inflate(R.layout.players_tab, null, false);
			getParentActivity().setPluginsView(lv);
			return lv;
		}
	}
}
