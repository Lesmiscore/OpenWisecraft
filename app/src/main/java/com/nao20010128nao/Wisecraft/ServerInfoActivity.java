package com.nao20010128nao.Wisecraft;

import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.skin_face.*;
import java.util.*;

import android.app.ActivityManager;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import com.google.gson.Gson;
import com.nao20010128nao.OTC.OrderTrustedMap;
import com.nao20010128nao.Wisecraft.misc.compat.AppCompatAlertDialog;
import com.nao20010128nao.Wisecraft.misc.compat.CompatArrayAdapter;
import com.nao20010128nao.Wisecraft.misc.pinger.ServerPingResult;
import com.nao20010128nao.Wisecraft.misc.pinger.pc.Reply;
import com.nao20010128nao.Wisecraft.misc.pinger.pc.Reply19;
import com.nao20010128nao.Wisecraft.misc.pinger.pe.FullStat;
import com.nao20010128nao.Wisecraft.pingEngine.UnconnectedPing;
import java.lang.ref.WeakReference;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.nao20010128nao.Wisecraft.Utils.*;

public class ServerInfoActivity extends ActionBarActivity {
	static WeakReference<ServerInfoActivity> instance=new WeakReference(null);
	public static List<ServerStatus> stat=new ArrayList<>();
	public static Map<String,Bitmap> faces=new HashMap<>();
	SharedPreferences pref;

	ServerStatus localStat;
	Bundle keeping;

	String ip;
	int port;
	boolean nonUpd,hidePlayer,hideData,hidePlugins,hideMods;

	MenuItem updateBtn,seeTitleButton;

	List<Thread> t=new ArrayList<>();
	ListView players,data,plugins,mods;
	TextView modLoader;
	FragmentTabHost fth;
	TabHost.TabSpec playersF,dataF,pluginsF,modsF;

	ArrayAdapter<String> adap,adap3;
	ArrayAdapter<Map.Entry<String,String>> adap2;
	ArrayAdapter<Object> adap4;

	List<Bitmap> skinFaceImages;
	SkinFaceFetcher sff;

	/*Only for PC servers*/
	ImageView serverIcon;
	TextView serverName;
	Drawable serverIconObj;
	CharSequence serverNameStr;
	String modLoaderTypeName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		if(pref.getBoolean("useBright",false)){
			setTheme(R.style.AppTheme_Bright);
			getTheme().applyStyle(R.style.AppTheme_Bright,true);
		}
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_MODE_OVERLAY);
		instance = new WeakReference(this);
		
		int statOfs=getIntent().getIntExtra("statListOffset", -1);

		if (stat.size() > statOfs & statOfs != -1)localStat = stat.get(statOfs);

		if (localStat == null) {
			finish();
			return;
		}

		keeping = getIntent().getBundleExtra("object");

		setContentView(R.layout.tabs);
		fth = (FragmentTabHost)findViewById(android.R.id.tabhost);
		fth.setup(this, getSupportFragmentManager(), R.id.container);

		hideData   = getIntent().getBooleanExtra("nonDetails", false);
		hidePlayer = getIntent().getBooleanExtra("nonPlayers", false);
		hidePlugins = getIntent().getBooleanExtra("nonPlugins", false);
		hideMods = getIntent().getBooleanExtra("nonMods", false);

		if (!hidePlayer) {
			playersF = fth.newTabSpec("playersList");
			playersF.setIndicator(getResources().getString(R.string.players));
			if (localStat.response instanceof UnconnectedPing.UnconnectedPingResult) {
				fth.addTab(playersF, UcpInfoFragment.class, null);
			} else {
				fth.addTab(playersF, PlayersFragment.class, null);
			}
		}

		if (!hideData) {
			dataF = fth.newTabSpec("dataList");
			dataF.setIndicator(getResources().getString(R.string.data));
			if (localStat.response instanceof UnconnectedPing.UnconnectedPingResult) {
				fth.addTab(dataF, UcpInfoFragment.class, null);
			} else {
				switch (localStat.mode) {
					case 0:fth.addTab(dataF, DataFragmentPE.class, null);break;
					case 1:fth.addTab(dataF, DataFragmentPC.class, null);break;
				}
			}
		}

		if (!(hidePlugins | localStat.mode == 1)) {
			pluginsF = fth.newTabSpec("pluginsList");
			pluginsF.setIndicator(getResources().getString(R.string.plugins));
			if (localStat.response instanceof UnconnectedPing.UnconnectedPingResult) {
				fth.addTab(pluginsF, UcpInfoFragment.class, null);
			} else {
				fth.addTab(pluginsF, PluginsFragment.class, null);
			}
		}

		if (pref.getBoolean("showPcUserFace", false) & localStat.mode == 1) {
			skinFaceImages = new ArrayList<>();
			sff = new SkinFaceFetcher();
			adap = new PCUserFaceAdapter();
			Log.d("ServerInfoActivity", "face on");
		} else {
			adap = new AppBaseArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
			Log.d("ServerInfoActivity", "face off");
		}
		adap2 = new KVListAdapter<>(this);
		adap3 = new AppBaseArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
		adap4 = new ModInfoListAdapter();

		nonUpd = getIntent().getBooleanExtra("nonUpd", false);

		ip = localStat.ip;
		port = localStat.port;

		fth.setCurrentTab(getIntent().getIntExtra("offset", 0));

		update(localStat.response);

		if (pref.getBoolean("colorFormattedText", false) & pref.getBoolean("darkBackgroundForServerName", false)) {
			BitmapDrawable bd=(BitmapDrawable)getResources().getDrawable(R.drawable.soil);
			bd.setTargetDensity(getResources().getDisplayMetrics());
			bd.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
			getSupportActionBar().setBackgroundDrawable(bd);
			
			if (Build.VERSION.SDK_INT >= 21) {
				getWindow().setStatusBarColor(0xff3a2a1d);
			}
			if(pref.getBoolean("useBright",false)){//Revert the Theme because status bar looks ugly
				setTheme(R.style.AppTheme);
				getTheme().applyStyle(R.style.AppTheme,true);
			}
		}
		if (Build.VERSION.SDK_INT >= 21) {
			ActivityManager.TaskDescription td;
			switch (localStat.mode) {
				case 1:
					if (serverIconObj instanceof BitmapDrawable) {
						td = new ActivityManager.TaskDescription(getTitle().toString(), ((BitmapDrawable)serverIconObj).getBitmap(), getResources().getColor(R.color.primary));
					} else {
						td = new ActivityManager.TaskDescription(getTitle().toString(), ((BitmapDrawable)getResources().getDrawable(R.drawable.ic_launcher)).getBitmap(), getResources().getColor(R.color.primary));
					}
					break;
				default:
					td = new ActivityManager.TaskDescription(getTitle().toString(), ((BitmapDrawable)getResources().getDrawable(R.drawable.ic_launcher)).getBitmap(), getResources().getColor(R.color.primary));
					break;
			}
			setTaskDescription(td);
		}
	}
	public synchronized void update(final ServerPingResult resp) {
		if (resp instanceof FullStat) {
			FullStat fs=(FullStat)resp;
			final ArrayList<String> sort=new ArrayList<>(fs.getPlayerList());
			if (pref.getBoolean("sortPlayerNames", true))
				Collections.sort(sort);
			final String title;
			Map<String,String> m=fs.getData();
			if (m.containsKey("hostname")) {
				title = m.get("hostname");
			} else if (m.containsKey("motd")) {
				title = m.get("motd");
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
				if (data.length >= 2) {
					ArrayList<String> plugins=new ArrayList<>(Arrays.<String>asList(data[1].split("\\; ")));
					if (pref.getBoolean("sortPluginNames", false))
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
				setTitle(rep.description);
			}

			if (rep.players.sample != null) {
				final ArrayList<String> sort=new ArrayList<>();
				for (Reply.Player o:rep.players.sample) {
					sort.add(o.name);
					TheApplication.instance.pcUserUUIDs.put(o.name, o.id);
				}
				if (pref.getBoolean("sortPlayerNames", true))
					Collections.sort(sort);
				adap.clear();
				CompatArrayAdapter.addAll(adap, sort);
			} else {
				adap.clear();
			}

			if (pref.getBoolean("colorFormattedText", false)) {
				if (pref.getBoolean("darkBackgroundForServerName", false)) {
					serverNameStr = Utils.parseMinecraftFormattingCodeForDark(rep.description);
				} else {
					serverNameStr = Utils.parseMinecraftFormattingCode(rep.description);
				}
			} else {
				serverNameStr = Utils.deleteDecorations(rep.description);
			}

			if (rep.favicon != null) {
				byte[] image=Base64.decode(rep.favicon.split("\\,")[1], Base64.NO_WRAP);
				Bitmap bmp=BitmapFactory.decodeByteArray(image, 0, image.length);
				serverIconObj = new BitmapDrawable(bmp);
			} else {
				serverIconObj = new ColorDrawable(Color.TRANSPARENT);
			}

			adap2.clear();
			Map<String,String> data=new OrderTrustedMap<>();
			data.put(getResources().getString(R.string.pc_maxPlayers), rep.players.max + "");
			data.put(getResources().getString(R.string.pc_nowPlayers), rep.players.online + "");
			data.put(getResources().getString(R.string.pc_softwareVersion), rep.version.name);
			data.put(getResources().getString(R.string.pc_protocolVersion), rep.version.protocol + "");
			CompatArrayAdapter.addAll(adap2, data.entrySet());

			if (rep.modinfo != null) {
				addModsTab();
				CompatArrayAdapter.addAll(adap4, rep.modinfo.modList);
				modLoaderTypeName = rep.modinfo.type;
			}
		} else if (resp instanceof Reply19) {
			Reply19 rep=(Reply19)resp;
			if (rep.description == null) {
				setTitle(localStat.ip + ":" + localStat.port);
			} else {
				setTitle(rep.description.text);
			}

			if (rep.players.sample != null) {
				final ArrayList<String> sort=new ArrayList<>();
				for (Reply19.Player o:rep.players.sample) {
					sort.add(o.name);
					TheApplication.instance.pcUserUUIDs.put(o.name, o.id);
				}
				Collections.sort(sort);
				adap.clear();
				CompatArrayAdapter.addAll(adap, sort);
			} else {
				adap.clear();
			}

			if (pref.getBoolean("colorFormattedText", false)) {
				if (pref.getBoolean("darkBackgroundForServerName", false)) {
					serverNameStr = Utils.parseMinecraftFormattingCodeForDark(rep.description.text);
				} else {
					serverNameStr = Utils.parseMinecraftFormattingCode(rep.description.text);
				}
			} else {
				serverNameStr = Utils.deleteDecorations(rep.description.text);
			}

			if (rep.favicon != null) {
				byte[] image=Base64.decode(rep.favicon.split("\\,")[1], Base64.NO_WRAP);
				Bitmap bmp=BitmapFactory.decodeByteArray(image, 0, image.length);
				serverIconObj = new BitmapDrawable(bmp);
			} else {
				serverIconObj = new ColorDrawable(Color.TRANSPARENT);
			}

			adap2.clear();
			Map<String,String> data=new OrderTrustedMap<>();
			data.put(getResources().getString(R.string.pc_maxPlayers), rep.players.max + "");
			data.put(getResources().getString(R.string.pc_nowPlayers), rep.players.online + "");
			data.put(getResources().getString(R.string.pc_softwareVersion), rep.version.name);
			data.put(getResources().getString(R.string.pc_protocolVersion), rep.version.protocol + "");
			CompatArrayAdapter.addAll(adap2, data.entrySet());

			if (rep.modinfo != null) {
				addModsTab();
				CompatArrayAdapter.addAll(adap4, rep.modinfo.modList);
				modLoaderTypeName = rep.modinfo.type;
			}
		} else if (resp instanceof SprPair) {
			SprPair p=(SprPair)resp;
			update(p.getA());
			update(p.getB());
		} else if (resp instanceof UnconnectedPing.UnconnectedPingResult) {
			if (pref.getBoolean("showDetailsIfNoDetails", false)) {
				setTitle((((UnconnectedPing.UnconnectedPingResult)resp).getServerName()));
			} else {
				finish();
				Toast.makeText(this, R.string.ucpInfoError, Toast.LENGTH_SHORT).show();
				return;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO: Implement this method
		seeTitleButton = menu.add(Menu.NONE, 0, 0, R.string.seeTitle);
		seeTitleButton.setIcon(R.drawable.ic_menu_view);
		MenuItemCompat.setShowAsAction(seeTitleButton, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		if (!nonUpd) {
			updateBtn = menu.add(Menu.NONE, 1, 1, R.string.update);
			updateBtn.setIcon(R.drawable.ic_menu_refresh);
			MenuItemCompat.setShowAsAction(updateBtn, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO: Implement this method
		switch (item.getItemId()) {
			case 1://Update
				setResultInstead(Constant.ACTIVITY_RESULT_UPDATE, new Intent().putExtra("offset", fth.getCurrentTab()));
				finish();//ServerListActivity updates the stat
				return true;
			case 0://See the title for all
				AppCompatAlertDialog.Builder ab=new AppCompatAlertDialog.Builder(this, R.style.AppAlertDialog);
				LinearLayout ll;
				boolean dark;
				dark = pref.getBoolean("colorFormattedText", false) ?pref.getBoolean("darkBackgroundForServerName", false): false;
				{
					if (dark) {
						ll = (LinearLayout)TheApplication.instance.getLayoutInflater().inflate(R.layout.server_info_show_title_dark, null);
					} else {
						ll = (LinearLayout)TheApplication.instance.getLayoutInflater().inflate(R.layout.server_info_show_title, null);
					}
					BitmapDrawable bd=(BitmapDrawable)getResources().getDrawable(R.drawable.soil);
					bd.setTargetDensity(getResources().getDisplayMetrics());
					bd.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
					if (!dark)bd.setAlpha(0);
					ll.setBackground(bd);
				}
				TextView serverNameView=(TextView)ll.findViewById(R.id.serverName);
				serverNameView.setText(getTitle());
				ab.setView(ll).show();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void setResultInstead(int resultCode, Intent data) {
		// TODO: Implement this method
		setResult(resultCode, data.putExtra("object", keeping));
	}

	@Override
	public void setTitle(CharSequence title) {
		// TODO: Implement this method
		if (pref.getBoolean("colorFormattedText", false)) {
			if (pref.getBoolean("darkBackgroundForServerName", false)) {
				super.setTitle(Utils.parseMinecraftFormattingCodeForDark(title.toString()));
			} else {
				super.setTitle(Utils.parseMinecraftFormattingCode(title.toString()));
			}
		} else {
			super.setTitle(Utils.deleteDecorations(title.toString()));
		}
	}

	@Override
	protected void onDestroy() {
		// TODO: Implement this method
		super.onDestroy();
		new Thread(){
			public void run() {
				pref.edit().putString("pcuseruuids", new Gson().toJson(TheApplication.instance.pcUserUUIDs)).commit();
			}
		}.start();
	}

	public void addModsTab() {
		if ((!hideMods) | localStat.mode == 1) {
			modsF = fth.newTabSpec("modsList");
			modsF.setIndicator(getResources().getString(R.string.mods));
			fth.addTab(modsF, ModsFragment.class, null);
		}
	}

	public void setPlayersView(ListView lv) {
		players = lv;
		lv.setAdapter(adap);
	}
	public void setDataView(View lv) {
		data = (ListView)lv.findViewById(R.id.data);
		if (localStat.mode == 1) {
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
	public void setModsListView(ListView lv) {
		mods = lv;
		lv.setAdapter(adap4);
	}
	public void setModLoaderNameView(TextView lv) {
		modLoader = lv;
		modLoader.setText(modLoaderTypeName);
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}

	class PCUserFaceAdapter extends AppBaseArrayAdapter<String> {
		List<View> cached=new ArrayList<>(Constant.ONE_HUNDRED_LENGTH_NULL_LIST);
		public PCUserFaceAdapter() {
			super(ServerInfoActivity.this, R.layout.simple_list_item_with_image, new ArrayList<String>());
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO: Implement this method
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.simple_list_item_with_image, null);
			}
			while (cached.size() < position)cached.addAll(Constant.ONE_HUNDRED_LENGTH_NULL_LIST);
			String playerName=getItem(position);
			((TextView)convertView.findViewById(android.R.id.text1)).setText(playerName);
			if (faces.containsKey(playerName)) {
				ImageView iv=(ImageView)convertView.findViewById(R.id.image);
				iv.setVisibility(View.VISIBLE);
				iv.setImageBitmap(faces.get(playerName));
				iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
			} else {
				sff.requestLoadSkin(playerName, new Handler());
			}
			cached.set(position, convertView);
			return convertView;
		}

		class Handler implements SkinFetcher.SkinFetchListener {
			@Override
			public void onError(String player) {
				// TODO: Implement this method
				Log.d("face", "err:" + player);
			}

			@Override
			public void onSuccess(final Bitmap bmp, final String player) {
				// TODO: Implement this method
				skinFaceImages.add(bmp);
				new AsyncTask<Bitmap,Void,Bitmap>(){
					public Bitmap doInBackground(Bitmap... datas) {
						Bitmap toProc=datas[0];
						int clSiz=getResources().getDimensionPixelSize(R.dimen.list_height) / 8 + 1;
						return ImageResizer.resizeBitmapPixel(toProc, clSiz, Bitmap.Config.ARGB_4444);
					}
					public void onPostExecute(final Bitmap bmp) {
						skinFaceImages.add(bmp);
						faces.put(player, bmp);
						runOnUiThread(new Runnable(){
								public void run() {
									notifyDataSetChanged();
									Log.d("face", "ok:" + player);
								}
							});
					}
				}.execute(bmp);
			}
		}
	}
	class ModInfoListAdapter extends AppBaseArrayAdapter<Object> {
		List<View> cached=new ArrayList<>(Constant.ONE_HUNDRED_LENGTH_NULL_LIST);
		public ModInfoListAdapter() {
			super(ServerInfoActivity.this, R.layout.simple_list_item_with_image, new ArrayList<Object>());
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO: Implement this method
			if (position == 0) {
				return getLayoutInflater().inflate(R.layout.void_view, null);
			} else {
				position--;
			}
			View v=getLayoutInflater().inflate(R.layout.mod_info_content, null);
			Object o=getItem(position);
			if (o instanceof Reply.ModListContent) {
				Reply.ModListContent mlc=(Reply.ModListContent)o;
				((TextView)v.findViewById(R.id.modName)).setText(mlc.modid);
				((TextView)v.findViewById(R.id.modVersion)).setText(mlc.version);
			} else if (o instanceof Reply19.ModListContent) {
				Reply19.ModListContent mlc=(Reply19.ModListContent)o;
				((TextView)v.findViewById(R.id.modName)).setText(mlc.modid);
				((TextView)v.findViewById(R.id.modVersion)).setText(mlc.version);
			}
			return v;
		}

		@Override
		public int getCount() {
			// TODO: Implement this method
			return super.getCount() + 1;
		}
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
			SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(getActivity());
			View lv= inflater.inflate(R.layout.data_tab_pc, null, false);
			getParentActivity().setDataView(lv);
			if (pref.getBoolean("colorFormattedText", false) & pref.getBoolean("darkBackgroundForServerName", false)) {
				BitmapDrawable bd=(BitmapDrawable)getResources().getDrawable(R.drawable.soil);
				bd.setTargetDensity(getResources().getDisplayMetrics());
				bd.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
				lv.findViewById(R.id.serverImageAndName).setBackground(bd);
			}
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
	public static class ModsFragment extends BaseFragment<ServerInfoActivity> {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO: Implement this method
			View lv=inflater.inflate(R.layout.mods_tab, null, false);
			getParentActivity().setModsListView((ListView)lv.findViewById(R.id.players));
			getParentActivity().setModLoaderNameView((TextView)lv.findViewById(R.id.modLoaderType));
			return lv;
		}
	}
	public static class UcpInfoFragment extends BaseFragment<ServerInfoActivity> {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO: Implement this method
			return inflater.inflate(R.layout.server_info_no_details_fragment, null, false);
		}
	}
}
