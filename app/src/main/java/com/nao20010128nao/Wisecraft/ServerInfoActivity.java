package com.nao20010128nao.Wisecraft;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.preference.*;
import android.support.v4.content.*;
import android.support.v4.view.*;
import android.support.v7.graphics.*;
import android.support.v7.widget.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import biz.laenger.android.vpbs.*;
import com.astuetz.*;
import com.google.gson.*;
import com.nao20010128nao.OTC.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import com.nao20010128nao.Wisecraft.misc.pinger.*;
import com.nao20010128nao.Wisecraft.misc.pinger.pc.*;
import com.nao20010128nao.Wisecraft.misc.pinger.pe.*;
import com.nao20010128nao.Wisecraft.misc.skin_face.*;
import java.io.*;
import java.lang.ref.*;
import java.math.*;
import java.util.*;

import static com.nao20010128nao.Wisecraft.misc.Utils.*;

public class ServerInfoActivity extends ServerInfoActivityBase1 {
	static WeakReference<ServerInfoActivity> instance=new WeakReference(null);
	//public static List<ServerStatus> stat=new ArrayList<>();
	public static Map<String,Bitmap> faces=new HashMap<>();

	public static int DIRT_BRIGHT,DIRT_DARK,PALE_PRIMARY;
	public static final int BASE64_FLAGS=Base64.NO_WRAP|Base64.NO_PADDING;

	SharedPreferences pref;

	ServerStatus localStat;
	Bundle keeping;

	String ip;
	int port;
	boolean nonUpd,hidePlayer,hideData,hidePlugins,hideMods,noExport;

	MenuItem updateBtn,seeTitleButton,exportButton;

	InternalPagerAdapter adapter;
	ViewPager tabs;

	List<Bitmap> skinFaceImages;
	SkinFaceFetcher sff;
	
	View bottomSheet;
	ViewPagerBottomSheetBehavior behavior;
	boolean useBottomSheet=false;
	View background;//it is actually FrameLayout

	/*Only for PC servers*/
	Drawable serverIconObj;
	Bitmap serverIconBmp;
	CharSequence serverNameStr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		super.onCreate(savedInstanceState);
		calculatePalePrimary();
		getWindow().requestFeature(Window.FEATURE_ACTION_MODE_OVERLAY);
		instance = new WeakReference(this);

		String stat=getIntent().getStringExtra("stat");
		if(stat==null){finish();return;}
		byte[] statData=Base64.decode(stat,BASE64_FLAGS);
		localStat=PingSerializeProvider.loadFromServerDumpFile(statData);

		if (localStat == null) {
			finish();
			return;
		}

		keeping = getIntent().getBundleExtra("object");
		useBottomSheet=getIntent().getBooleanExtra("bottomSheet",true)&!pref.getBoolean("noScrollServerInfo",false);

		if(useBottomSheet)
			setContentView(R.layout.server_info_pager);
		else
			setContentView(R.layout.server_info_pager_nobs);
		setSupportActionBar((android.support.v7.widget.Toolbar)findViewById(R.id.toolbar));
		tabs = (ViewPager)findViewById(R.id.pager);
		tabs.setAdapter(adapter = new InternalPagerAdapter());
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
		noExport = getIntent().getBooleanExtra("noExport", false);

		ip = localStat.ip;
		port = localStat.port;

		update(localStat.response);

		if (pref.getBoolean("colorFormattedText", false) & pref.getBoolean("darkBackgroundForServerName", false)) {
			BitmapDrawable bd=(BitmapDrawable)getResources().getDrawable(R.drawable.soil);
			bd.setTargetDensity(getResources().getDisplayMetrics());
			bd.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
			findViewById(R.id.appbar).setBackgroundDrawable(bd);
			psts.setIndicatorColor(Color.WHITE);
			psts.setTextColor(Color.WHITE);
			psts.setOnPageChangeListener(new ColorUpdater(Color.WHITE, DIRT_BRIGHT, tabs, psts));
		} else {
			psts.setIndicatorColor(ContextCompat.getColor(this, R.color.mainColor));
			psts.setTextColor(ContextCompat.getColor(this, R.color.mainColor));
			psts.setOnPageChangeListener(new ColorUpdater(ContextCompat.getColor(this, R.color.mainColor), PALE_PRIMARY, tabs, psts));
		}

		int offset=getIntent().getIntExtra("offset", 0);
		if (adapter.getCount() >= 2 & offset == 0)tabs.setCurrentItem(1);
		tabs.setCurrentItem(offset);
		
		if(useBottomSheet){
			BottomSheetUtils.setupViewPager(tabs);
			bottomSheet = findViewById(R.id.serverInfoFragment);
			behavior = ViewPagerBottomSheetBehavior.from(bottomSheet);
			behavior.setHideable(true);
			behavior.setState(ViewPagerBottomSheetBehavior.STATE_COLLAPSED);
			behavior.setBottomSheetCallback(new ViewPagerBottomSheetBehavior.BottomSheetCallback() {
				int r,g,b;
				{
					int color;
					if (pref.getBoolean("colorFormattedText", false) & pref.getBoolean("darkBackgroundForServerName", false)) {
						color=DIRT_DARK;
					}else{
						color=ContextCompat.getColor(ServerInfoActivity.this,R.color.material_grey_100);
					}
					r=Color.red(color);
					g=Color.green(color);
					b=Color.blue(color);
				}
					@Override
					public void onStateChanged(View bottomSheet, int newState) {
						switch (newState) {
							case ViewPagerBottomSheetBehavior.STATE_DRAGGING:
							case ViewPagerBottomSheetBehavior.STATE_SETTLING:
							case ViewPagerBottomSheetBehavior.STATE_COLLAPSED:
								/*if (Build.VERSION.SDK_INT >= 21) {
									getWindow().setStatusBarColor(0);
								}*/
								break;
							case ViewPagerBottomSheetBehavior.STATE_EXPANDED:
								/*if (Build.VERSION.SDK_INT >= 21) {
									if (pref.getBoolean("colorFormattedText", false) & pref.getBoolean("darkBackgroundForServerName", false)) {
										getWindow().setStatusBarColor(DIRT_DARK);
									}else{
										getWindow().setStatusBarColor(0);
									}
								}*/
								break;
							case ViewPagerBottomSheetBehavior.STATE_HIDDEN:
								finish();
								break;
						}
					}

					@Override
					public void onSlide(View bottomSheet, float slideOffset) {
						/*
						BigDecimal val=new BigDecimal(slideOffset).add(BigDecimal.ONE).divide(new BigDecimal(2));
						ViewCompat.setAlpha(background,val.floatValue());
						
						if (Build.VERSION.SDK_INT >= 21) {
							int alpha=val.multiply(new BigDecimal(255)).intValue();
							int status=Color.argb(alpha,r,g,b);
							getWindow().setStatusBarColor(status);
						}
						*/
						BigDecimal val=slideOffset<0?BigDecimal.ZERO:new BigDecimal(slideOffset);
						ViewCompat.setAlpha(background,val.floatValue());

						if (Build.VERSION.SDK_INT >= 21) {
							int alpha=val.multiply(new BigDecimal(255)).intValue();
							int status=Color.argb(alpha,r,g,b);
							getWindow().setStatusBarColor(status);
						}
					}
				});
			background=findViewById(R.id.background);
			background.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v){
					behavior.setState(ViewPagerBottomSheetBehavior.STATE_HIDDEN);
				}
			});
			if (pref.getBoolean("colorFormattedText", false) & pref.getBoolean("darkBackgroundForServerName", false)) {
				background.setBackgroundColor(DIRT_DARK);
			}else{
				background.setBackgroundColor(ContextCompat.getColor(ServerInfoActivity.this,R.color.material_grey_100));
			}
		}else{
			if (Build.VERSION.SDK_INT >= 21) {
				if (pref.getBoolean("colorFormattedText", false) & pref.getBoolean("darkBackgroundForServerName", false)) {
					getWindow().setStatusBarColor(DIRT_DARK);
				}else{
					getWindow().setStatusBarColor(ContextCompat.getColor(ServerInfoActivity.this,R.color.material_grey_100));
				}
			}
		}
	}

	public void onBackPressed() {
		// TODO: Implement this method
		if(useBottomSheet){
			switch(behavior.getState()){
				case ViewPagerBottomSheetBehavior.STATE_EXPANDED:
					behavior.setState(ViewPagerBottomSheetBehavior.STATE_COLLAPSED);
					break;
				case ViewPagerBottomSheetBehavior.STATE_COLLAPSED:
					behavior.setState(ViewPagerBottomSheetBehavior.STATE_HIDDEN);
					break;
			}
		}else{
			finish();
		}
	}
	
	public void scheduleFinish(){
		if(useBottomSheet){
			behavior.setState(ViewPagerBottomSheetBehavior.STATE_HIDDEN);
		}else{
			finish();
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
		} else if (resp instanceof UnconnectedPing.UnconnectedPingResult & resp != localStat.response) {
			setTitle((((UnconnectedPing.UnconnectedPingResult)resp).getServerName()));
			addUcpDetailsTab();
		} else if (resp instanceof UnconnectedPing.UnconnectedPingResult & resp == localStat.response) {
			setTitle((((UnconnectedPing.UnconnectedPingResult)resp).getServerName()));
		}
		Utils.getToolbar(this).setSubtitle(localStat.toString());


		updateTaskDesc(resp);
	}
	public void updateTaskDesc(ServerPingResult resp) {
		if (Build.VERSION.SDK_INT >= 21) {
			int color=ContextCompat.getColor(this, R.color.mainColor);
			if (resp instanceof Reply) {
				Reply rep=(Reply)resp;
				if (rep.favicon != null) {
					byte[] image=Base64.decode(rep.favicon.split("\\,")[1], Base64.NO_WRAP);
					serverIconBmp = BitmapFactory.decodeByteArray(image, 0, image.length);
					serverIconObj = new BitmapDrawable(serverIconBmp);
					color = Palette.generate(serverIconBmp).getLightVibrantColor(color);
				} else {
					serverIconObj = new ColorDrawable(Color.TRANSPARENT);
				}
			} else if (resp instanceof Reply19) {
				Reply19 rep=(Reply19)resp;
				if (rep.favicon != null) {
					byte[] image=Base64.decode(rep.favicon.split("\\,")[1], Base64.NO_WRAP);
					serverIconBmp = BitmapFactory.decodeByteArray(image, 0, image.length);
					serverIconObj = new BitmapDrawable(serverIconBmp);
					color = Palette.generate(serverIconBmp).getLightVibrantColor(color);
				} else {
					serverIconObj = new ColorDrawable(Color.TRANSPARENT);
				}
			}
			ActivityManager.TaskDescription td;
			switch (localStat.mode) {
				case 1:
					if (serverIconBmp != null) {
						td = new ActivityManager.TaskDescription(getTitle().toString(), serverIconBmp, color);
					} else {
						td = new ActivityManager.TaskDescription(getTitle().toString(), ((BitmapDrawable)getResources().getDrawable(R.drawable.ic_launcher)).getBitmap(), color);
					}
					break;
				default:
					td = new ActivityManager.TaskDescription(getTitle().toString(), ((BitmapDrawable)getResources().getDrawable(R.drawable.ic_launcher)).getBitmap(), color);
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
				isDark = true;
			} else {
				isDark = false;
			}
		} else {
			isDark = false;
		}
		int color= ContextCompat.getColor(this, R.color.mainColor);
		if (!noExport) {
			exportButton = menu.add(Menu.NONE, 0, 0, R.string.exportPing);
			exportButton.setIcon(TheApplication.instance.getTintedDrawable(com.nao20010128nao.MaterialIcons.R.drawable.ic_file_upload_black_48dp, isDark ?Color.WHITE: color));
			MenuItemCompat.setShowAsAction(exportButton, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		}
		seeTitleButton = menu.add(Menu.NONE, 1, 1, R.string.seeTitle);
		seeTitleButton.setIcon(TheApplication.instance.getTintedDrawable(com.nao20010128nao.MaterialIcons.R.drawable.ic_open_in_new_black_48dp, isDark ?Color.WHITE: color));
		MenuItemCompat.setShowAsAction(seeTitleButton, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		if (!nonUpd) {
			updateBtn = menu.add(Menu.NONE, 2, 2, R.string.update);
			updateBtn.setIcon(TheApplication.instance.getTintedDrawable(com.nao20010128nao.MaterialIcons.R.drawable.ic_refresh_black_48dp, isDark ?Color.WHITE: color));
			MenuItemCompat.setShowAsAction(updateBtn, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO: Implement this method
		switch (item.getItemId()) {
			case 0://Export this ping result
				View dialogView_=getLayoutInflater().inflate(R.layout.server_list_imp_exp, null);
				final EditText et_=(EditText)dialogView_.findViewById(R.id.filePath);
				et_.setText(new File(Environment.getExternalStorageDirectory(), "/Wisecraft/pingresult.wisecraft-ping").toString());
				dialogView_.findViewById(R.id.selectFile).setOnClickListener(new View.OnClickListener(){
						public void onClick(View v) {
							File f=new File(et_.getText().toString());
							if ((!f.exists())|f.isFile())f = f.getParentFile();
							startChooseFileForOpen(f, new FileChooserResult(){
									public void onSelected(File f) {
										et_.setText(f.toString());
									}
									public void onSelectCancelled() {/*No-op*/}
								});
						}
					});
				new AppCompatAlertDialog.Builder(this, R.style.AppAlertDialog)
					.setTitle(R.string.export_typepath_simple)
					.setView(dialogView_)
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface di, int w) {
							Toast.makeText(ServerInfoActivity.this, R.string.exporting, Toast.LENGTH_LONG).show();
							new AsyncTask<String,Void,File>(){
								public File doInBackground(String... texts) {
									File f;
									byte[] data=PingSerializeProvider.doRawDumpForFile(localStat.response);
									if (writeToFileByBytes(f = new File(texts[0]), data))
										return f;
									else
										return null;
								}
								public void onPostExecute(File f) {
									if (f != null) {
										Toast.makeText(ServerInfoActivity.this, getResources().getString(R.string.export_complete).replace("[PATH]", f + ""), Toast.LENGTH_LONG).show();
									} else {
										Toast.makeText(ServerInfoActivity.this, getResources().getString(R.string.export_failed), Toast.LENGTH_LONG).show();
									}
								}
							}.execute(et_.getText().toString());
						}
					})
					.show();
				break;
			case 2://Update
				setResultInstead(Constant.ACTIVITY_RESULT_UPDATE, new Intent().putExtra("offset", tabs.getCurrentItem()));
				scheduleFinish();//ServerListActivity updates the stat
				return true;
			case 1://See the title for all
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
		if (title == null) {
			if (pref.getBoolean("colorFormattedText", false)) {
				SpannableStringBuilder ssb=new SpannableStringBuilder();
				ssb.append(localStat.toString());
				if (pref.getBoolean("darkBackgroundForServerName", false)) {
					ssb.setSpan(new ForegroundColorSpan(0xff_ffffff), 0, ssb.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				} else {
					ssb.setSpan(new ForegroundColorSpan(0xff_000000), 0, ssb.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				super.setTitle(ssb);
			} else {
				super.setTitle(localStat.toString());
			}
		} else {
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
		if (serverIconBmp != null)serverIconBmp.recycle();
	}

	public void addModsTab() {
		if ((!hideMods) | localStat.mode == 1) {
			adapter.addTab(ModsFragment.class, getResources().getString(R.string.mods));
		}
	}

	public void addUcpDetailsTab() {
		adapter.addTab(UcpDetailsFragment.class, getResources().getString(R.string.data_ucp));
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(TheApplication.injectContextSpecial(newBase));
	}

	class PCUserFaceAdapter extends PlayerNamesListAdapter {
		List<View> cached=new ArrayList<>(Constant.ONE_HUNDRED_LENGTH_NULL_LIST);
		
		@Override
		public FindableViewHolder onCreateViewHolder(ViewGroup parent, int type) {
			// TODO: Implement this method
			return new VH(getLayoutInflater().inflate(R.layout.simple_list_item_with_image,parent,false));
		}

		@Override
		public void onBindViewHolder(FindableViewHolder holder, int position, List<Object> payloads) {
			// TODO: Implement this method
			View convertView=holder.itemView;
			String playerName=getItem(position);
			((TextView)convertView.findViewById(android.R.id.text1)).setText(playerName);
			ImageView iv=(ImageView)convertView.findViewById(R.id.image);
			if (faces.containsKey(playerName)) {
				iv.setVisibility(View.VISIBLE);
				iv.setImageBitmap(faces.get(playerName));
				iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
			} else {
				iv.setVisibility(View.GONE);
                String uuid=TheApplication.instance.pcUserUUIDs.get(playerName);
				sff.requestLoadSkin(playerName,uuid, new Handler());
				iv.setImageBitmap(null);
			}
		}

		public class VH extends FindableViewHolder{
			public VH(View w){
				super(w);
			}
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
									notifyItemChanged(indexOf(player));
									Log.d("face", "ok:" + player);
								}
							});
					}
				}.execute(bmp);
			}
		}
	}
	class ModInfoListAdapter extends ListRecyclerViewAdapter<FindableViewHolder,Object> {
		@Override
		public int getItemCount() {
			// TODO: Implement this method
			return super.getItemCount()+1;
		}

		@Override
		public FindableViewHolder onCreateViewHolder(ViewGroup parent, int type) {
			// TODO: Implement this method
			if(type==0)
				return new VH(getLayoutInflater().inflate(R.layout.void_view,null));
			else
				return new VH(getLayoutInflater().inflate(R.layout.mod_info_content, parent, false));
		}

		@Override
		public void onBindViewHolder(FindableViewHolder parent, int offset) {
			// TODO: Implement this method
			if(offset==0)return;
			int position=offset-1;
			View v=parent.itemView;
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
		}

		@Override
		public int getItemViewType(int position) {
			// TODO: Implement this method
			if(position==0)return 0;else return 1;
		}
		
		public class VH extends FindableViewHolder{
			public VH(View w){
				super(w);
			}
		}
	}
	class PlayerNamesListAdapter extends ListRecyclerViewAdapter<FindableViewHolder,String> {
		public PlayerNamesListAdapter(){
			super(new ArrayList<String>());
		}
		
		@Override
		public String getItem(int position) {
			// TODO: Implement this method
			String s=super.getItem(position);
			if (pref.getBoolean("deleteDecoPlayerName", false))
				s = deleteDecorations(s);
			return s;
		}

		@Override
		public FindableViewHolder onCreateViewHolder(ViewGroup parent, int type) {
			// TODO: Implement this method
			return new VH(getLayoutInflater().inflate(android.R.layout.simple_list_item_1,parent,false));
		}

		@Override
		public void onBindViewHolder(FindableViewHolder parent, int offset) {
			// TODO: Implement this method
			((TextView)parent.findViewById(android.R.id.text1)).setText(getItem(offset));
		}
		
		public class VH extends FindableViewHolder{
			public VH(View w){
				super(w);
			}
		}
	}


	class InternalPagerAdapter extends UsefulPagerAdapter {
		public InternalPagerAdapter() {
			super(getSupportFragmentManager());
		}
	}
	
	static class DividerItemDecoration extends RecyclerView.ItemDecoration {

		private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

		private Drawable mDivider;
		private int oneDp;

		/**
		 * Default divider will be used
		 */
		public DividerItemDecoration(Context context) {
			final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
			mDivider = styledAttributes.getDrawable(0);
			styledAttributes.recycle();
			oneDp=context.getResources().getDimensionPixelSize(R.dimen.one_dp);
		}

		/**
		 * Custom divider will be used
		 */
		public DividerItemDecoration(Context context, int resId) {
			mDivider = ContextCompat.getDrawable(context, resId);
			oneDp=context.getResources().getDimensionPixelSize(R.dimen.one_dp);
		}

		@Override
		public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
			int left = parent.getPaddingLeft();
			int right = parent.getWidth() - parent.getPaddingRight();

			int childCount = parent.getChildCount();
			for (int i = 0; i < childCount; i++) {
				View child = parent.getChildAt(i);

				RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

				int top = child.getBottom() + params.bottomMargin;
				int bottom = top + oneDp;

				mDivider.setBounds(left, top, right, bottom);
				mDivider.draw(c);
			}
		}
	}
	
	
	public static class PlayersFragment extends BaseFragment<ServerInfoActivity> {
		RecyclerView lv;
		ListRecyclerViewAdapter<FindableViewHolder,String> player;
		@Override
		public void onResume() {
			// TODO: Implement this method
			super.onResume();
			lv = (RecyclerView)getView();
			lv.setLayoutManager(new HPLinearLayoutManager(getActivity()));
			lv.setHasFixedSize(false);


			ServerStatus localStat=getParentActivity().localStat;
			ServerPingResult resp=localStat.response;
			if (pref.getBoolean("showPcUserFace", false) & localStat.mode == 1 & canInflateSkinFaceList()) {
				getParentActivity().skinFaceImages = new ArrayList<>();
				getParentActivity().sff = new SkinFaceFetcher(new SkinFetcher());
				player = getParentActivity().new PCUserFaceAdapter();
				Log.d("ServerInfoActivity", "face on");
			} else {
				player = getParentActivity().new PlayerNamesListAdapter();
				Log.d("ServerInfoActivity", "face off");
			}

			player.setHasStableIds(false);
			lv.setAdapter(player);

			if (resp instanceof FullStat | resp instanceof SprPair) {
				FullStat fs=null;
				if (resp instanceof FullStat)
					fs = (FullStat)resp;
				else if (resp instanceof SprPair)
					fs = (FullStat)((SprPair)resp).getA();
				final ArrayList<String> sort=new ArrayList<>(fs.getPlayerList());
				if (pref.getBoolean("sortPlayerNames", true))
					Collections.sort(sort);
				player.clear();
				player.addAll(sort);
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
					player.addAll(sort);
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
					player.addAll(sort);
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

		public boolean canInflateSkinFaceList() {
			try {
				LayoutInflater.from(getParentActivity()).inflate(R.layout.simple_list_item_with_image, null, false);
				return true;
			} catch (Throwable e) {
				return false;
			}
		}
	}
	public static class DataFragmentPE extends BaseFragment<ServerInfoActivity> {
		RecyclerView data;
		KVRecyclerAdapter<String,String> infos;
		@Override
		public void onResume() {
			// TODO: Implement this method
			super.onResume();
			data = (RecyclerView)getView().findViewById(R.id.data);
			data.setLayoutManager(new HPLinearLayoutManager(getActivity()));
			data.setHasFixedSize(false);

			infos = new KVRecyclerAdapter<>(getParentActivity());
			infos.setHasStableIds(false);
			data.setAdapter(infos);
			ServerStatus localStat=getParentActivity().localStat;
			ServerPingResult resp=localStat.response;
			if (resp instanceof FullStat | resp instanceof SprPair) {
				FullStat fs=null;
				if (resp instanceof FullStat)
					fs = (FullStat)resp;
				else if (resp instanceof SprPair)
					fs = (FullStat)((SprPair)resp).getA();
				infos.clear();
				infos.addAll(fs.getData().entrySet());
			} 
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO: Implement this method
			View v= inflater.inflate(R.layout.data_tab, container, false);
			((RecyclerView)v.findViewById(R.id.data)).addItemDecoration(new DividerItemDecoration(getContext()));
			return v;
		}
	}
	public static class DataFragmentPC extends BaseFragment<ServerInfoActivity> {
		ImageView serverIcon;
		TextView serverName;
		Drawable serverIconObj;
		Bitmap serverIconBmp;
		RecyclerView data;
		CharSequence serverNameStr;
		KVRecyclerAdapter<String,String> infos;
		@Override
		public void onResume() {
			// TODO: Implement this method
			super.onResume();
			serverIcon = (ImageView)getView().findViewById(R.id.serverIcon);
			serverName = (TextView)getView().findViewById(R.id.serverTitle);
			data = (RecyclerView)getView().findViewById(R.id.data);
			data.setLayoutManager(new HPLinearLayoutManager(getActivity()));
			data.setHasFixedSize(false);


			infos = new KVRecyclerAdapter<>(getParentActivity());
			infos.setHasStableIds(false);
			data.setAdapter(infos);
			ServerStatus localStat=getParentActivity().localStat;
			ServerPingResult resp=localStat.response;
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
				infos.addAll(data.entrySet());

				if (rep.favicon != null) {
					byte[] image=Base64.decode(rep.favicon.split("\\,")[1], Base64.NO_WRAP);
					serverIconBmp = BitmapFactory.decodeByteArray(image, 0, image.length);
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
				infos.addAll(data.entrySet());

				if (rep.favicon != null) {
					byte[] image=Base64.decode(rep.favicon.split("\\,")[1], Base64.NO_WRAP);
					serverIconBmp = BitmapFactory.decodeByteArray(image, 0, image.length);
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
			((RecyclerView)lv.findViewById(R.id.data)).addItemDecoration(new DividerItemDecoration(getContext()));
			return lv;
		}
	}
	public static class PluginsFragment extends BaseFragment<ServerInfoActivity> {
		SimpleRecyclerAdapter<String> pluginNames;
		RecyclerView lv;
		@Override
		public void onResume() {
			// TODO: Implement this method
			super.onResume();
			lv = (RecyclerView)getView();
			lv.setLayoutManager(new HPLinearLayoutManager(getActivity()));
			lv.setHasFixedSize(false);


			pluginNames = new SimpleRecyclerAdapter<String>(getParentActivity());
			pluginNames.setHasStableIds(false);
			lv.setAdapter(pluginNames);
			ServerStatus localStat=getParentActivity().localStat;
			ServerPingResult resp=localStat.response;
			if (resp instanceof FullStat | resp instanceof SprPair) {
				FullStat fs=null;
				if (resp instanceof FullStat)
					fs = (FullStat)resp;
				else if (resp instanceof SprPair)
					fs = (FullStat)((SprPair)resp).getA();
				pluginNames.clear();
				if (fs.getData().containsKey("plugins")) {
					String[] data=fs.getData().get("plugins").split("\\: ");
					if (data.length >= 2) {
						ArrayList<String> plugins=new ArrayList<>(Arrays.<String>asList(data[1].split("\\; ")));
						if (pref.getBoolean("sortPluginNames", false))
							Collections.sort(plugins);
						pluginNames.addAll(plugins);
					}
				}
			}
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO: Implement this method
			return inflater.inflate(R.layout.players_tab, container, false);
		}
	}
	public static class ModsFragment extends BaseFragment<ServerInfoActivity> {
		String modLoaderTypeName;
		TextView modLoader;
		ListRecyclerViewAdapter<FindableViewHolder,Object> modInfos;
		RecyclerView mods;
		@Override
		public void onResume() {
			// TODO: Implement this method
			super.onResume();
			mods = (RecyclerView)getView().findViewById(R.id.players);
			modLoader = (TextView)getView().findViewById(R.id.modLoaderType);
			mods.setLayoutManager(new HPLinearLayoutManager(getActivity()));
			mods.setHasFixedSize(false);


			modInfos = getParentActivity().new ModInfoListAdapter();
			modInfos.setHasStableIds(false);
			mods.setAdapter(modInfos);
			ServerStatus localStat=getParentActivity().localStat;
			ServerPingResult resp=localStat.response;
			if (resp instanceof Reply) {
				Reply rep=(Reply)resp;
				if (rep.modinfo != null) {
					modInfos.addAll(rep.modinfo.modList);
					modLoaderTypeName = rep.modinfo.type;
				}
			} else if (resp instanceof Reply19) {
				Reply19 rep=(Reply19)resp;
				if (rep.modinfo != null) {
					modInfos.addAll(rep.modinfo.modList);
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
			if (getParentActivity().localStat.response instanceof UnconnectedPing.UnconnectedPingResult) {
				result = (UnconnectedPing.UnconnectedPingResult)getParentActivity().localStat.response;
			} else {
				result = (UnconnectedPing.UnconnectedPingResult)((SprPair)getParentActivity().localStat.response).getB();
			}
			RecyclerView lv=(RecyclerView)getView().findViewById(R.id.data);
			lv.setLayoutManager(new HPLinearLayoutManager(getActivity()));
			lv.addItemDecoration(new DividerItemDecoration(getContext()));
			lv.setHasFixedSize(false);
			
			KVRecyclerAdapter<String,String> adap=new KVRecyclerAdapter<String,String>(getActivity());
			adap.setHasStableIds(false);
			lv.setAdapter(adap);
			OrderTrustedMap<String,String> otm=new OrderTrustedMap<String,String>();
			String[] values=result.getRaw().split("\\;");
			otm.put(getString(R.string.ucp_serverName),      values[1]);
			otm.put(getString(R.string.ucp_protocolVersion), values[2]);
			otm.put(getString(R.string.ucp_mcpeVersion),     values[3]);
			otm.put(getString(R.string.ucp_nowPlayers),      values[4]);
			otm.put(getString(R.string.ucp_maxPlayers),      values[5]);
			adap.addAll(otm.entrySet());
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO: Implement this method
			View v= inflater.inflate(R.layout.server_info_ucp_details, container, false);
			((RecyclerView)v.findViewById(R.id.data)).addItemDecoration(new DividerItemDecoration(getContext()));
			return v;
		}
	}


	static{
		int base=0xff3a2a1d;
		float[] hsv=new float[3];
		Color.RGBToHSV(Color.red(base), Color.green(base), Color.blue(base), hsv);
		float v=hsv[2];
		hsv[2] = v + 0.25f;//V+20
		DIRT_BRIGHT = Color.HSVToColor(hsv);
		hsv[2] = v - 0.05f;//V-10
		DIRT_DARK = Color.HSVToColor(hsv);
		
		Log.d("DirtBright",Integer.toHexString(DIRT_BRIGHT));
		Log.d("DirtDark",Integer.toHexString(DIRT_DARK));
		
		calculatePalePrimary();
	}
	
	public static void calculatePalePrimary(){
		int palePrimary=ContextCompat.getColor(TheApplication.instance, R.color.mainColor);
		int r=Color.red(palePrimary);
		int g=Color.green(palePrimary);
		int b=Color.blue(palePrimary);
		int a=new BigDecimal(0xff).multiply(new BigDecimal("0.3")).intValue();

		PALE_PRIMARY = Color.argb(a, r, g, b);
		
		Log.d("PalePrimary",Integer.toHexString(PALE_PRIMARY));
	}
}
