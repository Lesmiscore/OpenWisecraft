package com.nao20010128nao.Wisecraft;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.preference.*;
import android.support.v4.app.*;
import android.support.v4.view.*;
import android.support.v7.app.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.google.gson.*;
import com.nao20010128nao.OTC.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.Utils;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import com.nao20010128nao.Wisecraft.misc.pinger.*;
import com.nao20010128nao.Wisecraft.misc.pinger.pc.*;
import com.nao20010128nao.Wisecraft.misc.pinger.pe.*;
import com.nao20010128nao.Wisecraft.misc.skin_face.*;
import com.nao20010128nao.Wisecraft.pingEngine.*;
import java.lang.ref.*;
import java.util.*;
import uk.co.chrisjenx.calligraphy.*;

import static com.nao20010128nao.Wisecraft.misc.Utils.*;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.Spannable;
import com.astuetz.PagerSlidingTabStrip;
import android.content.res.ColorStateList;
import java.math.BigDecimal;

public class ServerInfoActivity extends AppCompatActivity {
	static WeakReference<ServerInfoActivity> instance=new WeakReference(null);
	public static List<ServerStatus> stat=new ArrayList<>();
	public static Map<String,Bitmap> faces=new HashMap<>();
	
	public static final int DIRT_BRIGHT,DIRT_DARK,PALE_PRIMARY;
	
	SharedPreferences pref;

	ServerStatus localStat;
	Bundle keeping;

	String ip;
	int port;
	boolean nonUpd,hidePlayer,hideData,hidePlugins,hideMods;

	MenuItem updateBtn,seeTitleButton;

	List<Thread> t=new ArrayList<>();
	InternalPagerAdapter adapter;
	ViewPager tabs;

	List<Bitmap> skinFaceImages;
	SkinFaceFetcher sff;

	/*Only for PC servers*/
	Drawable serverIconObj;
	Bitmap serverIconBmp;
	CharSequence serverNameStr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		if(pref.getBoolean("useBright",false)&!(pref.getBoolean("colorFormattedText", false) & pref.getBoolean("darkBackgroundForServerName", false))){
			setTheme(R.style.AppTheme_Bright_NoActionBar);
			getTheme().applyStyle(R.style.AppTheme_Bright_NoActionBar,true);
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

		setContentView(R.layout.server_info_pager);
		setSupportActionBar((android.support.v7.widget.Toolbar)findViewById(R.id.toolbar));
		tabs = (ViewPager)findViewById(R.id.pager);
		tabs.setAdapter(adapter=new InternalPagerAdapter());
		PagerSlidingTabStrip psts=(PagerSlidingTabStrip)findViewById(R.id.tabs);
		psts.setViewPager(tabs);

		hideData   = getIntent().getBooleanExtra("nonDetails", false);
		hidePlayer = getIntent().getBooleanExtra("nonPlayers", false);
		hidePlugins = getIntent().getBooleanExtra("nonPlugins", false);
		hideMods = getIntent().getBooleanExtra("nonMods", false);

		if (!hidePlayer) {
			if (localStat.response instanceof UnconnectedPing.UnconnectedPingResult) {
				adapter.addTab(UcpInfoFragment.class, getResources().getString(R.string.players));
			} else {
				adapter.addTab(PlayersFragment.class, getResources().getString(R.string.players));
			}
		}

		if (!hideData) {
			if (localStat.response instanceof UnconnectedPing.UnconnectedPingResult) {
				adapter.addTab(UcpDetailsFragment.class, getResources().getString(R.string.data));
			} else {
				switch (localStat.mode) {
					case 0:adapter.addTab(DataFragmentPE.class, getResources().getString(R.string.data));break;
					case 1:adapter.addTab(DataFragmentPC.class, getResources().getString(R.string.data));break;
				}
			}
		}

		if (!(hidePlugins | localStat.mode == 1)) {
			if (localStat.response instanceof UnconnectedPing.UnconnectedPingResult) {
				adapter.addTab(UcpInfoFragment.class, getResources().getString(R.string.plugins));
			} else {
				adapter.addTab(PluginsFragment.class, getResources().getString(R.string.plugins));
			}
		}

		nonUpd = getIntent().getBooleanExtra("nonUpd", false);

		ip = localStat.ip;
		port = localStat.port;
		
		update(localStat.response);
		
		tabs.setCurrentItem(getIntent().getIntExtra("offset", 0));
		
		if (pref.getBoolean("colorFormattedText", false) & pref.getBoolean("darkBackgroundForServerName", false)) {
			BitmapDrawable bd=(BitmapDrawable)getResources().getDrawable(R.drawable.soil);
			bd.setTargetDensity(getResources().getDisplayMetrics());
			bd.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
			findViewById(R.id.appbar).setBackgroundDrawable(bd);
			if (Build.VERSION.SDK_INT >= 21) {
				getWindow().setStatusBarColor(DIRT_DARK);
			}
			ColorStateList csl=new ColorStateList(new int[][]{
													  new int[]{ },
													  new int[]{~android.R.attr.state_selected},
													  new int[]{ android.R.attr.state_selected}
												  },
												  new int[]{
													  DIRT_BRIGHT,
													  DIRT_BRIGHT,
													  Color.WHITE
												  });
			psts.setIndicatorColor(Color.WHITE);
			psts.setTextColor(csl);
		}else{
			ColorStateList csl=new ColorStateList(new int[][]{
													  new int[]{ },
													  new int[]{~android.R.attr.state_selected},
													  new int[]{ android.R.attr.state_selected}
												  },
												  new int[]{
													  PALE_PRIMARY,
													  PALE_PRIMARY,
													  getResources().getColor(R.color.upd_2)
												  });
			psts.setIndicatorColor(getResources().getColor(R.color.upd_2));
			psts.setTextColor(csl);
		}
	}
	public synchronized void update(final ServerPingResult resp) {
		if (resp instanceof FullStat) {
			FullStat fs=(FullStat)resp;
			final String title;
			Map<String,String> m=fs.getData();
			if (m.containsKey("hostname")) {
				title = m.get("hostname");
			} else if (m.containsKey("motd")) {
				title = m.get("motd");
			} else {
				title = ip + ":" + port;
			}
			setTitle(title);
		} else if (resp instanceof Reply) {
			Reply rep=(Reply)resp;
			if (rep.description == null) {
				setTitle(localStat.toString());
			} else {
				setTitle(rep.description);
			}
		} else if (resp instanceof Reply19) {
			Reply19 rep=(Reply19)resp;
			if (rep.description == null) {
				setTitle(localStat.toString());
			} else {
				setTitle(rep.description.text);
			}
		} else if (resp instanceof SprPair) {
			SprPair p=(SprPair)resp;
			update(p.getA());
			update(p.getB());
		} else if (resp instanceof UnconnectedPing.UnconnectedPingResult & resp!=localStat.response) {
			setTitle((((UnconnectedPing.UnconnectedPingResult)resp).getServerName()));
			addUcpDetailsTab();
		} else if (resp instanceof UnconnectedPing.UnconnectedPingResult & resp==localStat.response) {
			if (pref.getBoolean("showDetailsIfNoDetails", false)) {
				setTitle((((UnconnectedPing.UnconnectedPingResult)resp).getServerName()));
			} else {
				finish();
				Toast.makeText(this, R.string.ucpInfoError, Toast.LENGTH_SHORT).show();
				return;
			}
		}
		
		
		updateTaskDesc(resp);
	}
	public void updateTaskDesc(ServerPingResult resp){
		if (Build.VERSION.SDK_INT >= 21) {
			if (resp instanceof Reply) {
				Reply rep=(Reply)resp;
				if (rep.favicon != null) {
					byte[] image=Base64.decode(rep.favicon.split("\\,")[1], Base64.NO_WRAP);
					serverIconBmp=BitmapFactory.decodeByteArray(image, 0, image.length);
					serverIconObj = new BitmapDrawable(serverIconBmp);
				} else {
					serverIconObj = new ColorDrawable(Color.TRANSPARENT);
				}
			} else if (resp instanceof Reply19) {
				Reply19 rep=(Reply19)resp;
				if (rep.favicon != null) {
					byte[] image=Base64.decode(rep.favicon.split("\\,")[1], Base64.NO_WRAP);
					serverIconBmp=BitmapFactory.decodeByteArray(image, 0, image.length);
					serverIconObj = new BitmapDrawable(serverIconBmp);
				} else {
					serverIconObj = new ColorDrawable(Color.TRANSPARENT);
				}
			}
			ActivityManager.TaskDescription td;
			switch (localStat.mode) {
				case 1:
					if (serverIconBmp!=null) {
						td = new ActivityManager.TaskDescription(getTitle().toString(), serverIconBmp, getResources().getColor(R.color.upd_2));
					} else {
						td = new ActivityManager.TaskDescription(getTitle().toString(), ((BitmapDrawable)getResources().getDrawable(R.drawable.ic_launcher)).getBitmap(), getResources().getColor(R.color.upd_2));
					}
					break;
				default:
					td = new ActivityManager.TaskDescription(getTitle().toString(), ((BitmapDrawable)getResources().getDrawable(R.drawable.ic_launcher)).getBitmap(), getResources().getColor(R.color.upd_2));
					break;
			}
			setTaskDescription(td);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean isDark;
		if (pref.getBoolean("colorFormattedText", false)) {
			if (pref.getBoolean("darkBackgroundForServerName", false)) {
				isDark=true;
			} else {
				isDark=false;
			}
		} else {
			isDark=false;
		}
		seeTitleButton = menu.add(Menu.NONE, 0, 0, R.string.seeTitle);
		seeTitleButton.setIcon(TheApplication.instance.getTintedDrawable(com.nao20010128nao.MaterialIcons.R.drawable.ic_open_in_new_black_48dp,isDark?Color.WHITE:0xff_666666));
		MenuItemCompat.setShowAsAction(seeTitleButton, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		if (!nonUpd) {
			updateBtn = menu.add(Menu.NONE, 1, 1, R.string.update);
			updateBtn.setIcon(isDark?com.nao20010128nao.HoloIcons.R.drawable.ic_action_refresh_dark:com.nao20010128nao.HoloIcons.R.drawable.ic_action_refresh_light);
			MenuItemCompat.setShowAsAction(updateBtn, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO: Implement this method
		switch (item.getItemId()) {
			case 1://Update
				setResultInstead(Constant.ACTIVITY_RESULT_UPDATE, new Intent().putExtra("offset", tabs.getCurrentItem()));
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
					ll.setBackgroundDrawable(bd);
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
		if(title==null){
			if (pref.getBoolean("colorFormattedText", false)) {
				SpannableStringBuilder ssb=new SpannableStringBuilder();
				ssb.append(localStat.toString());
				if (pref.getBoolean("darkBackgroundForServerName", false)) {
					ssb.setSpan(new ForegroundColorSpan(0xff_ffffff),0,ssb.length()-1,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				} else {
					ssb.setSpan(new ForegroundColorSpan(0xff_000000),0,ssb.length()-1,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				super.setTitle(ssb);
			} else {
				super.setTitle(localStat.toString());
			}
		}else{
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
		if(serverIconBmp!=null)serverIconBmp.recycle();
	}

	@Override
	protected void onResume() {
		// TODO: Implement this method
		super.onResume();
	}
	
	public void addModsTab() {
		if ((!hideMods) | localStat.mode == 1) {
			adapter.addTab(ModsFragment.class, getResources().getString(R.string.mods));
		}
	}

	public void addUcpDetailsTab() {
		if ((!hideMods) | localStat.mode == 1) {
			adapter.addTab(UcpDetailsFragment.class, getResources().getString(R.string.data_ucp));
		}
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(TheApplication.injectContextSpecial(newBase));
	}

	class PCUserFaceAdapter extends PlayerNamesListAdapter {
		List<View> cached=new ArrayList<>(Constant.ONE_HUNDRED_LENGTH_NULL_LIST);
		public PCUserFaceAdapter() {
			super(ServerInfoActivity.this, R.layout.simple_list_item_with_image, new ArrayList<String>());
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO: Implement this method
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.simple_list_item_with_image, parent, false);
			}
			while (cached.size() < position)cached.addAll(Constant.ONE_HUNDRED_LENGTH_NULL_LIST);
			String playerName=getItem(position);
			((TextView)convertView.findViewById(android.R.id.text1)).setText(playerName);
			ImageView iv=(ImageView)convertView.findViewById(R.id.image);
			if (faces.containsKey(playerName)) {
				iv.setVisibility(View.VISIBLE);
				iv.setImageBitmap(faces.get(playerName));
				iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
			} else {
				iv.setVisibility(View.GONE);
				sff.requestLoadSkin(playerName, new Handler());
				iv.setImageBitmap(null);
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
	class PlayerNamesListAdapter extends AppBaseArrayAdapter<String>{
		public PlayerNamesListAdapter() {
			super(ServerInfoActivity.this, android.R.layout.simple_list_item_1, new ArrayList<String>());
		}
		public PlayerNamesListAdapter(Context context, int resource, List<String> objects) {
			super(context, resource, objects);
		}
		
		@Override
		public String getItem(int position) {
			// TODO: Implement this method
			String s=super.getItem(position);
			if(pref.getBoolean("deleteDecoPlayerName",false))
				s=deleteDecorations(s);
			return s;
		}
	}

	
	class InternalPagerAdapter extends FragmentPagerAdapter{
		List<Map.Entry<Class,String>> pages=new ArrayList<>();
		
		public InternalPagerAdapter(){
			super(getSupportFragmentManager());
		}

		public void addTab(Class<? extends BaseFragment<ServerInfoActivity>> clat,String title){
			pages.add(new KVP<Class,String>(clat,title));
			notifyDataSetChanged();
		}
		
		@Override
		public int getCount() {
			// TODO: Implement this method
			return pages.size();
		}

		@Override
		public android.support.v4.app.Fragment getItem(int p1) {
			// TODO: Implement this method
			try {
				return (android.support.v4.app.Fragment)pages.get(p1).getKey().newInstance();
			} catch (Throwable e) {
				return null;
			}
		}

		@Override
		public CharSequence getPageTitle(int position) {
			// TODO: Implement this method
			return pages.get(position).getValue();
		}
	}
	
	public static class PlayersFragment extends BaseFragment<ServerInfoActivity> {
		ListView lv;
		ArrayAdapter<String> player;
		@Override
		public void onResume() {
			// TODO: Implement this method
			super.onResume();
			lv=(ListView)getView();
			
			
			ServerStatus localStat=getParentActivity().localStat;
			ServerPingResult resp=localStat.response;
			String ip=localStat.ip;
			int port=localStat.port;
			
			if (pref.getBoolean("showPcUserFace", false) & localStat.mode == 1) {
				getParentActivity().skinFaceImages = new ArrayList<>();
				getParentActivity().sff = new SkinFaceFetcher();
				player = getParentActivity().new PCUserFaceAdapter();
				Log.d("ServerInfoActivity", "face on");
			} else {
				player = getParentActivity().new PlayerNamesListAdapter();
				Log.d("ServerInfoActivity", "face off");
			}
			
			lv.setAdapter(player);
			
			if (resp instanceof FullStat|resp instanceof SprPair) {
				FullStat fs=null;
				if(resp instanceof FullStat)
					fs=(FullStat)resp;
				else if(resp instanceof SprPair)
					fs=(FullStat)((SprPair)resp).getA();
				final ArrayList<String> sort=new ArrayList<>(fs.getPlayerList());
				if (pref.getBoolean("sortPlayerNames", true))
					Collections.sort(sort);
				player.clear();
				CompatArrayAdapter.addAll(player, sort);
			} else if (resp instanceof Reply) {
				Reply rep=(Reply)resp;
				if (rep.players.sample != null) {
					final ArrayList<String> sort=new ArrayList<>();
					for (Reply.Player o:rep.players.sample) {
						sort.add(o.name);
						TheApplication.instance.pcUserUUIDs.put(o.name, o.id);
					}
					if (pref.getBoolean("sortPlayerNames", true))
						Collections.sort(sort);
					player.clear();
					CompatArrayAdapter.addAll(player, sort);
				} else {
					player.clear();
				}
			} else if (resp instanceof Reply19) {
				Reply19 rep=(Reply19)resp;
				if (rep.players.sample != null) {
					final ArrayList<String> sort=new ArrayList<>();
					for (Reply19.Player o:rep.players.sample) {
						sort.add(o.name);
						TheApplication.instance.pcUserUUIDs.put(o.name, o.id);
					}
					if (pref.getBoolean("sortPlayerNames", true))
						Collections.sort(sort);
					player.clear();
					CompatArrayAdapter.addAll(player, sort);
				} else {
					player.clear();
				}
			}
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO: Implement this method
			return inflater.inflate(R.layout.players_tab, container, false);
		}
	}
	public static class DataFragmentPE extends BaseFragment<ServerInfoActivity> {
		View lv;
		ListView data;
		KVListAdapter<String,String> infos;
		@Override
		public void onResume() {
			// TODO: Implement this method
			super.onResume();
			data=(ListView)getView().findViewById(R.id.data);
			
			infos = new KVListAdapter<>(getParentActivity());
			data.setAdapter(infos);
			ServerStatus localStat=getParentActivity().localStat;
			ServerPingResult resp=localStat.response;
			String ip=localStat.ip;
			int port=localStat.port;
			
			if (resp instanceof FullStat|resp instanceof SprPair) {
				FullStat fs=null;
				if(resp instanceof FullStat)
					fs=(FullStat)resp;
				else if(resp instanceof SprPair)
					fs=(FullStat)((SprPair)resp).getA();
				infos.clear();
				CompatArrayAdapter.addAll(infos, fs.getData().entrySet());
			} 
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO: Implement this method
			return this.lv=inflater.inflate(R.layout.data_tab, container, false);
		}
	}
	public static class DataFragmentPC extends BaseFragment<ServerInfoActivity> {
		ImageView serverIcon;
		TextView serverName;
		Drawable serverIconObj;
		Bitmap serverIconBmp;
		ListView data;
		CharSequence serverNameStr;
		KVListAdapter<String,String> infos;
		@Override
		public void onResume() {
			// TODO: Implement this method
			super.onResume();
			serverIcon = (ImageView)getView().findViewById(R.id.serverIcon);
			serverName = (TextView)getView().findViewById(R.id.serverTitle);
			data=(ListView)getView().findViewById(R.id.data);
			
			
			infos = new KVListAdapter<>(getParentActivity());
			data.setAdapter(infos);
			ServerStatus localStat=getParentActivity().localStat;
			ServerPingResult resp=localStat.response;
			String ip=localStat.ip;
			int port=localStat.port;
			if (resp instanceof Reply) {
				Reply rep=(Reply)resp;
				if (pref.getBoolean("colorFormattedText", false)) {
					if (pref.getBoolean("darkBackgroundForServerName", false)) {
						serverNameStr = Utils.parseMinecraftFormattingCodeForDark(rep.description);
					} else {
						serverNameStr = Utils.parseMinecraftFormattingCode(rep.description);
					}
				} else {
					serverNameStr = Utils.deleteDecorations(rep.description);
				}
				
				infos.clear();
				Map<String,String> data=new OrderTrustedMap<>();
				data.put(getResources().getString(R.string.pc_maxPlayers), rep.players.max + "");
				data.put(getResources().getString(R.string.pc_nowPlayers), rep.players.online + "");
				data.put(getResources().getString(R.string.pc_softwareVersion), rep.version.name);
				data.put(getResources().getString(R.string.pc_protocolVersion), rep.version.protocol + "");
				CompatArrayAdapter.addAll(infos, data.entrySet());
				
				if (rep.favicon != null) {
					byte[] image=Base64.decode(rep.favicon.split("\\,")[1], Base64.NO_WRAP);
					serverIconBmp=BitmapFactory.decodeByteArray(image, 0, image.length);
					serverIconObj = new BitmapDrawable(serverIconBmp);
				} else {
					serverIconObj = new ColorDrawable(Color.TRANSPARENT);
				}
			} else if (resp instanceof Reply19) {
				Reply19 rep=(Reply19)resp;
				if (pref.getBoolean("colorFormattedText", false)) {
					if (pref.getBoolean("darkBackgroundForServerName", false)) {
						serverNameStr = Utils.parseMinecraftFormattingCodeForDark(rep.description.text);
					} else {
						serverNameStr = Utils.parseMinecraftFormattingCode(rep.description.text);
					}
				} else {
					serverNameStr = Utils.deleteDecorations(rep.description.text);
				}
				
				infos.clear();
				Map<String,String> data=new OrderTrustedMap<>();
				data.put(getResources().getString(R.string.pc_maxPlayers), rep.players.max + "");
				data.put(getResources().getString(R.string.pc_nowPlayers), rep.players.online + "");
				data.put(getResources().getString(R.string.pc_softwareVersion), rep.version.name);
				data.put(getResources().getString(R.string.pc_protocolVersion), rep.version.protocol + "");
				CompatArrayAdapter.addAll(infos, data.entrySet());
				
				if (rep.favicon != null) {
					byte[] image=Base64.decode(rep.favicon.split("\\,")[1], Base64.NO_WRAP);
					serverIconBmp=BitmapFactory.decodeByteArray(image, 0, image.length);
					serverIconObj = new BitmapDrawable(serverIconBmp);
				} else {
					serverIconObj = new ColorDrawable(Color.TRANSPARENT);
				}
			}
			serverName.setText(serverNameStr);
			serverIcon.setImageDrawable(serverIconObj);
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO: Implement this method
			View lv= inflater.inflate(R.layout.data_tab_pc, container, false);
			if (pref.getBoolean("colorFormattedText", false) & pref.getBoolean("darkBackgroundForServerName", false)) {
				BitmapDrawable bd=(BitmapDrawable)getResources().getDrawable(R.drawable.soil);
				bd.setTargetDensity(getResources().getDisplayMetrics());
				bd.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
				lv.findViewById(R.id.serverImageAndName).setBackgroundDrawable(bd);
			}
			return lv;
		}
	}
	public static class PluginsFragment extends BaseFragment<ServerInfoActivity> {
		ArrayAdapter<String> pluginNames;
		ListView lv;
		@Override
		public void onResume() {
			// TODO: Implement this method
			super.onResume();
			lv=(ListView)getView();
			
			
			pluginNames = new AppBaseArrayAdapter<String>(getParentActivity(), android.R.layout.simple_list_item_1, new ArrayList<String>());
			lv.setAdapter(pluginNames);
			ServerStatus localStat=getParentActivity().localStat;
			ServerPingResult resp=localStat.response;
			String ip=localStat.ip;
			int port=localStat.port;
			if (resp instanceof FullStat|resp instanceof SprPair) {
				FullStat fs=null;
				if(resp instanceof FullStat)
					fs=(FullStat)resp;
				else if(resp instanceof SprPair)
					fs=(FullStat)((SprPair)resp).getA();
				pluginNames.clear();
				if (fs.getData().containsKey("plugins")) {
					String[] data=fs.getData().get("plugins").split("\\: ");
					if (data.length >= 2) {
						ArrayList<String> plugins=new ArrayList<>(Arrays.<String>asList(data[1].split("\\; ")));
						if (pref.getBoolean("sortPluginNames", false))
							Collections.sort(plugins);
						CompatArrayAdapter.addAll(pluginNames, plugins);
					}
				}
			}
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO: Implement this method
			return lv=(ListView) inflater.inflate(R.layout.players_tab, container, false);
		}
	}
	public static class ModsFragment extends BaseFragment<ServerInfoActivity> {
		String modLoaderTypeName;
		TextView modLoader;
		ArrayAdapter<Object> modInfos;
		ListView mods;
		@Override
		public void onResume() {
			// TODO: Implement this method
			super.onResume();
			mods=(ListView)getView().findViewById(R.id.players);
			modLoader=(TextView)getView().findViewById(R.id.modLoaderType);
			
			
			modInfos = getParentActivity().new ModInfoListAdapter();
			mods.setAdapter(modInfos);
			ServerStatus localStat=getParentActivity().localStat;
			ServerPingResult resp=localStat.response;
			String ip=localStat.ip;
			int port=localStat.port;
			if (resp instanceof Reply) {
				Reply rep=(Reply)resp;
				if (rep.modinfo != null) {
					CompatArrayAdapter.addAll(modInfos, rep.modinfo.modList);
					modLoaderTypeName = rep.modinfo.type;
				}
			} else if (resp instanceof Reply19) {
				Reply19 rep=(Reply19)resp;
				if (rep.modinfo != null) {
					CompatArrayAdapter.addAll(modInfos, rep.modinfo.modList);
					modLoaderTypeName = rep.modinfo.type;
				}
			}
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO: Implement this method
			return inflater.inflate(R.layout.mods_tab, container, false);
		}
	}
	public static class UcpInfoFragment extends BaseFragment<ServerInfoActivity> {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO: Implement this method
			return inflater.inflate(R.layout.server_info_no_details_fragment, container, false);
		}
	}
	public static class UcpDetailsFragment extends BaseFragment<ServerInfoActivity> {

		@Override
		public void onResume() {
			// TODO: Implement this method
			super.onResume();
			UnconnectedPing.UnconnectedPingResult result;
			if(getParentActivity().localStat.response instanceof UnconnectedPing.UnconnectedPingResult){
				result=(UnconnectedPing.UnconnectedPingResult)getParentActivity().localStat.response;
			}else{
				result=(UnconnectedPing.UnconnectedPingResult)((SprPair)getParentActivity().localStat.response).getB();
			}
			ListView lv=(ListView)getView().findViewById(R.id.data);
			KVListAdapter<String,String> adap=new KVListAdapter<String,String>(getActivity());
			lv.setAdapter(adap);
			OrderTrustedMap<String,String> otm=new OrderTrustedMap<String,String>();
			String[] values=result.getRaw().split("\\;");
			otm.put(getString(R.string.ucp_serverName),      values[1]);
			otm.put(getString(R.string.ucp_protocolVersion), values[2]);
			otm.put(getString(R.string.ucp_mcpeVersion),     values[3]);
			otm.put(getString(R.string.ucp_nowPlayers),      values[4]);
			otm.put(getString(R.string.ucp_maxPlayers),      values[5]);
			CompatArrayAdapter.addAll(adap,otm.entrySet());
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO: Implement this method
			return inflater.inflate(R.layout.server_info_ucp_details,container,false);
		}
	}
	
	
	static{
		int base=0xff3a2a1d;
		float[] hsv=new float[3];
		Color.RGBToHSV(Color.red(base),Color.green(base),Color.blue(base),hsv);
		float v=hsv[2];
		hsv[2]=v+0.25f;//V+20
		DIRT_BRIGHT=Color.HSVToColor(hsv);
		hsv[2]=v-0.05f;//V-10
		DIRT_DARK=Color.HSVToColor(hsv);
		
		int palePrimary=TheApplication.instance.getResources().getColor(R.color.upd_2);
		int r=Color.red(palePrimary);
		int g=Color.green(palePrimary);
		int b=Color.blue(palePrimary);
		int a=new BigDecimal(0xff).multiply(new BigDecimal("0.3")).intValue();
		
		PALE_PRIMARY=Color.argb(a,r,g,b);
	}
}
