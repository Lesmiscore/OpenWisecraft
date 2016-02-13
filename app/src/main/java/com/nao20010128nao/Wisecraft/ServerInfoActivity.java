package com.nao20010128nao.Wisecraft;

import android.view.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.misc.*;
import java.util.*;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Base64;
import com.nao20010128nao.MCPing.ServerPingResult;
import com.nao20010128nao.MCPing.pc.Reply;
import com.nao20010128nao.MCPing.pe.FullStat;
import com.nao20010128nao.Wisecraft.pingEngine.UnconnectedPing;
import java.lang.ref.WeakReference;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.nao20010128nao.Wisecraft.Utils.*;

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
	String serverNameStr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		instance = new WeakReference(this);

		if (stat == null) {
			finish();
			return;
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
			fth.addTab(dataF, DataFragment.class, null);
		}

		if (!hidePlugins) {
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

		ip = stat.ip;
		port = stat.port;

		update(stat.response);
	}
	public synchronized void update(final ServerPingResult resp) {
		if (resp instanceof FullStat) {
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
			if (fs.getData().containsKey("plugins")) {
				String[] data=fs.getData().get("plugins").split("\\: ");
				if (data.length >= 2)
					adap3.addAll(data[1].split("\\; "));
			}
			setTitle(title);
		} else if (resp instanceof Reply) {
			Reply rep=(Reply)resp;
			if (rep.description == null) {
				setTitle(stat.ip + ":" + stat.port);
			} else {
				setTitle(deleteDecorations(rep.description));
			}

			if (rep.players.getSample() != null) {
				final ArrayList<String> sort=new ArrayList<>();
				for (Reply.Player o:rep.players.getSample()) {
					sort.add(o.getName());
				}
				Collections.sort(sort);
				adap.clear();
				adap.addAll(sort);
			} else {
				adap.clear();
			}

			serverNameStr = deleteDecorations(rep.description);

			byte[] image=Base64.decode(rep.favicon.split("\\,")[1], Base64.NO_WRAP);
			Bitmap bmp=BitmapFactory.decodeByteArray(image, 0, image.length);
			serverIconObj = new BitmapDrawable(bmp);

			adap2.clear();
			Map<String,String> data=new HashMap<>();
			data.put(getResources().getString(R.string.pc_maxPlayers), rep.players.getMax() + "");
			data.put(getResources().getString(R.string.pc_nowPlayers), rep.players.getOnline() + "");
			data.put(getResources().getString(R.string.pc_softwareVersion), rep.version.getName());
			data.put(getResources().getString(R.string.pc_protocolVersion), rep.version.getProtocol() + "");
			adap2.addAll(data.entrySet());
		} else if (resp instanceof SprPair) {
			SprPair p=(SprPair)resp;
			update(p.getA());
			update(p.getB());
		} else if (resp instanceof UnconnectedPing.UnconnectedPingResult) {
			if (resp == stat.response) {
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
			menu.add(Menu.NONE, 0, 0, R.string.update);
		//menu.add(Menu.NONE, 0, 1, "メニュー2");

		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO: Implement this method
		switch (featureId) {
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
		if (stat.isPC) {
			serverIcon = (ImageView)lv.findViewById(R.id.serverIcon);
			serverName = (TextView)lv.findViewById(R.id.serverTitle);
			serverIcon.setImageDrawable(serverIconObj);
			serverName.setText(serverNameStr);
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
			View lv= inflater.inflate(stat.isPC ?R.layout.data_tab_pc: R.layout.data_tab, null, false);
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
