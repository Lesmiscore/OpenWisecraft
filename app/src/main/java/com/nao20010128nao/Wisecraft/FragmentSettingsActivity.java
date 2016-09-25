package com.nao20010128nao.Wisecraft;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.content.*;
import android.support.v4.view.*;
import android.support.v7.app.*;
import android.support.v7.preference.*;
import android.support.v7.widget.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.astuetz.*;
import com.azeesoft.lib.colorpicker.*;
import com.nao20010128nao.ToolBox.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import com.nao20010128nao.Wisecraft.misc.contextwrappers.extender.*;
import com.nao20010128nao.Wisecraft.misc.pinger.pe.*;
import com.nao20010128nao.Wisecraft.misc.pref.*;
import java.io.*;
import java.net.*;
import java.util.*;
import uk.co.chrisjenx.calligraphy.*;

import android.support.v7.widget.Toolbar;
import com.nao20010128nao.Wisecraft.R;

public class FragmentSettingsActivity extends AppCompatActivity {
	public static final Map<String,Class<? extends Fragment>> FRAGMENT_CLASSES=new HashMap<String,Class<? extends Fragment>>(){{
			put("root",HubPrefFragment.class);
			put("basics",Basics.class);
			put("features",Features.class);
			put("asfsls",Asfsls.class);
			put("versionInfo",VersionInfoFragmentLocal.class);
			put("serverListStyleEditor",ServerListStyleEditor.class);
	}};
	public static final String DIALOG_FRAGMENT_TAG_PREFIX="settings@com.nao20010128nao.Wisecraft#";
	
	int which;
	SharedPreferences pref;
	boolean requireRestart=true;
	
	FrameLayout misc;
	ViewPager pager;
	ServerListFragment slf;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		pref=PreferenceManager.getDefaultSharedPreferences(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_settings_with_preview);
		if(savedInstanceState==null){
			getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.preference,new HubPrefFragment())
				.addToBackStack("root")
				.commit();
		}
		misc=(FrameLayout)findViewById(R.id.misc);
		pager=(ViewPager)LayoutInflater.from(this).inflate(R.layout.view_pager_only,misc,true).findViewById(R.id.pager);
		
		UsefulPagerAdapter2 upa=new UsefulPagerAdapter2(getSupportFragmentManager());
		pager.setAdapter(upa);
		slf=new ServerListPreviewFragment();
		if(getResources().getBoolean(R.bool.is_port)){
			Log.d("FSA","calculating by the width of the screen");
			slf.setRows(Utils.calculateRows(FragmentSettingsActivity.this));
		}else{
			Log.d("FSA","calculating by the half width of the screen");
			slf.setRows(Utils.calculateRows(FragmentSettingsActivity.this,Utils.getScreenWidth(this)/2));
		}
		upa.addTab(slf,"");
		upa.addTab(new ServerInfoToolbarFragment(),"");
		if(savedInstanceState!=null){
			misc.setVisibility(savedInstanceState.getInt("misc.visibility"));
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO: Implement this method
		super.onSaveInstanceState(outState);
		outState.putInt("misc.visibility",misc.getVisibility());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO: Implement this method
		MenuItem showPreview=menu.add(Menu.NONE,0,0,R.string.preview);
		showPreview.setIcon(misc.getVisibility()==View.VISIBLE?R.drawable.ic_visibility_black_48dp:R.drawable.ic_visibility_off_black_48dp);
		MenuItemCompat.setShowAsAction(showPreview,MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO: Implement this method
		switch(item.getItemId()){
			case 0:
				boolean isShowing=misc.getVisibility()==View.VISIBLE;
				if(isShowing){
					misc.setVisibility(View.GONE);
				}else{
					misc.setVisibility(View.VISIBLE);
				}
				invalidateOptionsMenu();
				break;
		}
		return true;
	}
	
	@Override
	protected void attachBaseContext(Context newBase) {
        TheApplication.instance.initForActivities();
		super.attachBaseContext(TheApplication.injectContextSpecial(newBase));
	}

	@Override
	public void onBackPressed() {
		// TODO: Implement this method
		FragmentManager sfm=getSupportFragmentManager();
		if(sfm.getBackStackEntryCount()<2){
			finish();
			return;
		}
		sfm.popBackStack();
	}

	@Override
	public void finish() {
		// TODO: Implement this method
		if(requireRestart){
			if(ServerListActivityImpl.instance.get()!=null)
				ServerListActivityImpl.instance.get().finish();
			startActivity(new Intent(this,ServerListActivity.class));
		}
		super.finish();
	}
	
	
	public static class HubPrefFragment extends BaseFragment {
		SharedPreferences pref;
		@Override
		public void onCreatePreferences(Bundle p1, String p2) {
			// TODO: Implement this method
			addPreferencesFromResource(R.xml.settings_parent_compat);
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			// TODO: Implement this method
			pref=PreferenceManager.getDefaultSharedPreferences(getContext());
			super.onCreate(savedInstanceState);
			sH("basics", new HandledPreference.OnClickListener(){
					public void onClick(String a, String b, String c) {
						getActivity().getSupportFragmentManager()
							.beginTransaction()
							.replace(R.id.preference,new Basics())
							.addToBackStack("basics")
							.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
							.commit();
					}
				});
			sH("features", new HandledPreference.OnClickListener(){
					public void onClick(String a, String b, String c) {
						getActivity()
							.getSupportFragmentManager()
							.beginTransaction()
							.replace(R.id.preference,new Features())
							.addToBackStack("features")
							.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
							.commit();
					}
				});
			sH("asfsls",new HandledPreference.OnClickListener(){
					public void onClick(String a,String b,String c){
						getActivity()
							.getSupportFragmentManager()
							.beginTransaction()
							.replace(R.id.preference,new Asfsls())
							.addToBackStack("asfsls")
							.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
							.commit();
					}
				});
			sH("changeColor",new HandledPreference.OnClickListener(){
					public void onClick(String a,String b,String c){
						getActivity()
							.getSupportFragmentManager()
							.beginTransaction()
							.replace(R.id.preference,new ColorChanger())
							.addToBackStack("changeColor")
							.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
							.commit();
					}
				});
			sH("osl",new HandledPreference.OnClickListener(){
					public void onClick(String a,String b,String c){
						startActivity(new Intent(getContext(),OpenSourceActivity.class));
					}
				});
			sH("aboutApp",new HandledPreference.OnClickListener(){
					public void onClick(String a,String b,String c){
						startActivity(new Intent(getContext(),AboutAppActivity.class));
					}
				});
			sH("versionInfo",new HandledPreference.OnClickListener(){
					public void onClick(String a,String b,String c){
						getActivity()
							.getSupportFragmentManager()
							.beginTransaction()
							.replace(R.id.preference,new VersionInfoFragmentLocal())
							.addToBackStack("versionInfo")
							.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
							.commit();
					}
				});
			findPreference("asfsls").setEnabled(pref.getBoolean("feature_asfsls",false));
			((SetTextColor)findPreference("settingsAttention")).setTextColor(ContextCompat.getColor(getContext(),R.color.color888));
		}
		@Override
		public void onResume() {
			// TODO: Implement this method
			super.onResume();
			getActivity().setTitle(R.string.settings);
			findPreference("asfsls").setEnabled(pref.getBoolean("feature_asfsls",false));
		}
	}

	public static class Basics extends BaseFragment {
		public static final String PARALLELS_DIALOG_FRAGMENT_TAG=DIALOG_FRAGMENT_TAG_PREFIX+"parallels-dialog";
		int which;
		@Override
		public void onCreatePreferences(Bundle p1, String p2) {
			// TODO: Implement this method
			addPreferencesFromResource(R.xml.settings_basic_compat);
			sH("serverListStyle", new HandledPreference.OnClickListener(){
					public void onClick(String a, String b, String c) {
						new AppCompatAlertDialog.Builder(getContext(),R.style.AppAlertDialog)
							.setTitle(R.string.serverListStyle)
							.setSingleChoiceItems(getResources().getStringArray(R.array.serverListStyles),pref.getInt("serverListStyle2",0),new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface di,int w){
									which=w;
								}
							})
							.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface di,int w){
									pref.edit().putInt("serverListStyle2",which).commit();
								}
							})
							.setNegativeButton(android.R.string.cancel,new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface di,int w){

								}
							})
							.show();
					}
				});
			sH("selectFont",new HandledPreference.OnClickListener(){
					public void onClick(String a,String b,String c){
						String[] choice=getFontChoices();
						String[] display=TheApplication.instance.getDisplayFontNames(choice);
						final List<String> choiceList=Arrays.<String>asList(choice);
						new AppCompatAlertDialog.Builder(getContext(),R.style.AppAlertDialog)
							.setSingleChoiceItems(display, choiceList.indexOf(TheApplication.instance.getFontFieldName())
							, new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface di, int w) {
									di.cancel();
									TheApplication.instance.setFontFieldName(choiceList.get(w));
									//Toast.makeText(getContext(),R.string.saved_fonts,Toast.LENGTH_LONG).show();
								}
							})
							.show();
					}
					String[] getFontChoices() {
						return getResources().getStringArray(R.array.fontNamesOrder);
					}
				});
			sH("changeDpi", new HandledPreference.OnClickListener(){
					public void onClick(String a, String b, String c) {
						PreferenceUtils.showEditTextDialog(getActivity(),findPreference("changeDpi"),"1.0",new Treatment<View>(){
							public void process(View v){
								((EditText)v.findViewById(android.R.id.edit)).setInputType(InputType.TYPE_CLASS_NUMBER|
																							InputType.TYPE_TEXT_VARIATION_NORMAL|
																							InputType.TYPE_NUMBER_FLAG_DECIMAL);
							}
						});
					}
				});
			sH("addLessRows", new HandledPreference.OnClickListener(){
					public void onClick(String a, String b, String c) {
						View v=getLayoutInflater(null).inflate(R.layout.quick_seekbar,null);
						final SeekBar seekBar=(SeekBar)v.findViewById(R.id.seekbar);
						((TextView)v.findViewById(R.id.max)).setText("5");
						((TextView)v.findViewById(R.id.min)).setText("-5");
						final TextView value=(TextView)v.findViewById(R.id.value);
						seekBar.setMax(10);seekBar.setProgress(pref.getInt("addLessRows",0)+5);
						seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
								public void onProgressChanged(SeekBar p1, int p2, boolean p3){
									value.setText((seekBar.getProgress()-5)+"");
								}

								public void onStartTrackingTouch(SeekBar p1){
									
								}

								public void onStopTrackingTouch(SeekBar p1){
									
								}
								
								{
									onProgressChanged(null,0,false);
								}
						});
						new AppCompatAlertDialog.Builder(getContext(),R.style.AppAlertDialog)
							.setTitle(R.string.addLessRows)
							.setView(v)
							.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface di,int w){
									pref.edit().putInt("addLessRows",seekBar.getProgress()-5).commit();
								}
							})
							.setNegativeButton(android.R.string.cancel,new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface di,int w){

								}
							})
							.show();
					}
				});
			sH("serverListLooks", new HandledPreference.OnClickListener(){
					public void onClick(String a, String b, String c) {
						ServerListStyleEditor slse=new ServerListStyleEditor();
						slse.setRetainInstance(true);
						getActivity()
							.getSupportFragmentManager()
							.beginTransaction()
							.replace(R.id.preference,slse)
							.addToBackStack("serverListStyleEditor")
							.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
							.commit();
					}
				});
		}

		@Override
		public void onResume() {
			// TODO: Implement this method
			super.onResume();
			getActivity().setTitle(R.string.basics);
		}
	}


	public static class Features extends BaseFragment {
		int which;
		@Override
		public void onCreatePreferences(Bundle p1, String p2) {
			// TODO: Implement this method
			addPreferencesFromResource(R.xml.settings_features_compat);
		}

		@Override
		public void onResume() {
			// TODO: Implement this method
			super.onResume();
			getActivity().setTitle(R.string.features);
		}
	}


	public static class Asfsls extends BaseFragment {
		int which;
		@Override
		public void onCreatePreferences(Bundle p1, String p2) {
			// TODO: Implement this method
			addPreferencesFromResource(R.xml.settings_asfsls_compat);
			SharedPreferences slsVersCache=getContext().getSharedPreferences("sls_vers_cache", 0);
			findPreference("currentSlsVersion").setSummary(slsVersCache.getString("dat.vcode",getResources().getString(R.string.unknown)));
		}

		@Override
		public void onResume() {
			// TODO: Implement this method
			super.onResume();
			getActivity().setTitle(R.string.addServerFromServerListSite);
		}
	}
	
	public static class ColorChanger extends BaseFragment {

		@Override
		public void onResume() {
			// TODO: Implement this method
			super.onResume();
			getActivity().setTitle(R.string.colorChange);
		}

		@Override
		public void onCreatePreferences(Bundle p1, String p2) {
			// TODO: Implement this method
			addPreferencesFromResource(R.xml.settings_color_changer_compat);
		}
	}
	
	public static class VersionInfoFragmentLocal extends VersionInfoFragment{

		@Override
		public void onResume() {
			// TODO: Implement this method
			super.onResume();
			getActivity().setTitle(R.string.versionInfo);
		}
		
		@Override
		public RecyclerView onCreateRecyclerView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
			// TODO: Implement this method
			return super.onCreateRecyclerView(Utils.fixLayoutInflaterIfNeeded(CalligraphyContextWrapper.wrap(inflater.getContext()),getActivity()),
											  parent, 
											  savedInstanceState);
		}

		@Override
		public void onModifyPreferenceViewHolder(PreferenceViewHolder viewHolder, Preference pref) {
			// TODO: Implement this method
			PreferenceUtils.onBindViewHolder(getActivity(),pref,viewHolder);
		}
	}
	
	public static class ServerListStyleEditor extends com.nao20010128nao.Wisecraft.misc.BaseFragment<FragmentSettingsActivity> {
		ServerListStyleLoader slsl;
		RadioGroup rdGrp;
		ImageView color,image;
		Button selectColor,selectImage,apply;
		FileSelectFragment fsf;
		Bitmap loadedBitmap;
		int selectedColor=Color.BLACK;
		
		
		@Override
		public void onStart() {
			// TODO: Implement this method
			super.onStart();
			slsl=new ServerListStyleLoader(getActivity());
			rdGrp=(RadioGroup)findViewById(R.id.checkGroup);
			color=(ImageView)findViewById(R.id.singleColorIndicate);
			image=(ImageView)findViewById(R.id.imagePreview);
			selectColor=(Button)findViewById(R.id.selectColor);
			selectImage=(Button)findViewById(R.id.selectImage);
			apply=(Button)findViewById(R.id.apply);
			
			fsf=new FileSelectFragment();
			fsf.setRetainInstance(true);
			getChildFragmentManager()
				.beginTransaction()
				.replace(R.id.fileSelectFrg,fsf)
				.commit();
			
			switch(slsl.getBgId()){
				case ServerListStyleLoader.BACKGROUND_WHITE:
					rdGrp.check(R.id.white);
					break;
				case ServerListStyleLoader.BACKGROUND_BLACK:
					rdGrp.check(R.id.black);
					break;
				case ServerListStyleLoader.BACKGROUND_DIRT:
					rdGrp.check(R.id.dirt);
					break;
				case ServerListStyleLoader.BACKGROUND_SINGLE_COLOR:
					rdGrp.check(R.id.singleColor);
					color.setImageDrawable(slsl.load());
					selectedColor=slsl.getBackgroundSimpleColor();
					break;
				case ServerListStyleLoader.BACKGROUND_IMAGE:
					rdGrp.check(R.id.image);
					loadedBitmap=slsl.getImageBgBitmap();
					image.setImageBitmap(loadedBitmap);
					break;
			}
			
			rdGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
					public void onCheckedChanged(RadioGroup p1, int p2){
						switch(rdGrp.getCheckedRadioButtonId()){
							case R.id.white:
							case R.id.black:
							case R.id.dirt:
							case R.id.singleColor:
								apply.setEnabled(true);
								break;
							case R.id.image:
								apply.setEnabled(loadedBitmap!=null);
								break;
						}
					}
				});
			selectColor.setOnClickListener(new View.OnClickListener(){
					public void onClick(View v){
						ColorPickerDialog cpd=ColorPickerDialog.createColorPickerDialog(getActivity(),ColorPickerDialog.LIGHT_THEME);
						cpd.setLastColor(selectedColor);
						cpd.setOnColorPickedListener(new ColorPickerDialog.OnColorPickedListener(){
								public void onColorPicked(int color,String hex){
									selectedColor=color;
								}
							});
						cpd.setOnClosedListener(new ColorPickerDialog.OnClosedListener(){
								public void onClosed(){

								}
							});
						cpd.show();
					}
				});
			selectImage.setOnClickListener(new View.OnClickListener(){
					public void onClick(View v){
						new AsyncTask<Object,Void,Bitmap>(){
							public Bitmap doInBackground(Object... a){
								String path=ServerListStyleEditor.toString(a[0]);
								InputStream is=null;
								try{
									is=tryOpen(path);
									return BitmapFactory.decodeStream(is);
								}catch(Throwable e){
									WisecraftError.report("slse image",e);
									return null;
								}finally{
									try {
										if (is != null)is.close();
									} catch (IOException e) {
										WisecraftError.report("slse image",e);
									}
								}
							}
							public void onPostExcecute(Bitmap bmp){
								loadedBitmap=bmp;
							}
						}.execute(fsf.getLastResult());
					}
				});
			apply.setOnClickListener(new View.OnClickListener(){
					public void onClick(View v){
						switch(rdGrp.getCheckedRadioButtonId()){
							case R.id.white:
								slsl.setWhiteBg();
								break;
							case R.id.black:
								slsl.setBlackBg();
								break;
							case R.id.dirt:
								slsl.setDirtBg();
								break;
							case R.id.singleColor:
								slsl.setSingleColorBg(selectedColor);
								break;
							case R.id.image:
								if(loadedBitmap!=null)
									slsl.setImageBg(loadedBitmap);
								else return;
								break;
						}
						getParentActivity().getSupportFragmentManager().popBackStack();
					}
				});
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO: Implement this method
			return inflater.inflate(R.layout.settings_server_list_style_editor,container,false);
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			// TODO: Implement this method
			super.onActivityResult(requestCode, resultCode, data);
			fsf.onActivityResult(requestCode, resultCode, data);
		}
		
		public InputStream tryOpen(String uri) throws IOException {
			Log.d("dbg", "tryOpen:" + uri);
			if (uri.startsWith("content://")) {
				return getActivity().getContentResolver().openInputStream(Uri.parse(uri));
			} else if (uri.startsWith("/")) {
				return new FileInputStream(uri);
			} else {
				return URI.create(uri).toURL().openConnection().getInputStream();
			}
		}
		
		public OutputStream trySave(String uri) throws IOException {
			Log.d("dbg", "trySave:" + uri);
			if (uri.startsWith("content://")) {
				return getActivity().getContentResolver().openOutputStream(Uri.parse(uri));
			} else if (uri.startsWith("/")) {
				return new FileOutputStream(uri);
			} else {
				return URI.create(uri).toURL().openConnection().getOutputStream();
			}
		}
		
		public static String toUri(Object o) throws IOException,URISyntaxException{
			if(o instanceof File)
				return toUri(((File)o).toURL());
			else if(o instanceof Uri)
				return ((Uri)o).toString();
			else if(o instanceof URL)
				return toUri(((URL)o).toURI());
			else if(o instanceof URI)
				return ((URI)o).toString();
			else
				return null;
		}
		
		public static String toString(Object o) {
			return o==null?"null":o.toString();
		}
	}
	
	
	
	
	

	public abstract static class BaseFragment extends SHablePreferenceFragment {
		protected SharedPreferences pref;
		
		LinearLayout miscContent;
		@Override
		public void onCreate(Bundle savedInstanceState) {
			// TODO: Implement this method
			pref=PreferenceManager.getDefaultSharedPreferences(getContext());
			super.onCreate(savedInstanceState);
		}

		@Override
		public LayoutInflater getLayoutInflater(Bundle savedInstanceState) {
			// TODO: Implement this method
			return getActivity().getLayoutInflater().cloneInContext(super.getLayoutInflater(savedInstanceState).getContext());
		}

		@Override
		public Context getContext() {
			// TODO: Implement this method
			return TheApplication.injectContextSpecial(super.getContext());
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO: Implement this method
			View v=super.onCreateView(getActivity().getLayoutInflater(), container, savedInstanceState);
			miscContent=(LinearLayout)v.findViewById(R.id.misc);
			if(miscContent!=null)onMiscPartAvailable(miscContent);
			return v;
		}

		@Override
		public RecyclerView onCreateRecyclerView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
			// TODO: Implement this method
			return super.onCreateRecyclerView(Utils.fixLayoutInflaterIfNeeded(CalligraphyContextWrapper.wrap(inflater.getContext()),getActivity()),
				parent, 
				savedInstanceState);
		}
		
		
		
		protected void onMiscPartAvailable(LinearLayout misc){}
		
		public LinearLayout getMiscContent(){return miscContent;}
	}
	
	
	
	//preview part
	public static class ServerListPreviewFragment extends ServerListFragment<FragmentSettingsActivity> {

		@Override
		public void onResume() {
			// TODO: Implement this method
			super.onResume();
			new AsyncTask<Void,Void,List<Server>>(){
				public List<Server> doInBackground(Void...a){
					List<Server> list=new ArrayList<>();
					
					ByteArrayOutputStream result=new ByteArrayOutputStream();
					DataOutputStream resW=new DataOutputStream(result);
					try{
						//full stat
						resW.write(0);resW.writeInt(0);
						//73 70 6C 69 74 6E 75 6D 00 80 00
						resW.write((byte)0x73);
						resW.write((byte)0x70);
						resW.write((byte)0x6c);
						resW.write((byte)0x69);
						resW.write((byte)0x74);
						resW.write((byte)0x6e);
						resW.write((byte)0x75);
						resW.write((byte)0x6d);
						resW.write((byte)0x00);
						resW.write((byte)0x80);
						resW.write((byte)0x00);
						//KV
						Map<String,String> kv=new HashMap();
						kv.put("gametype", "SMP");
						kv.put("map", "wisecraft");
						kv.put("server_engine", "");
						kv.put("hostport", "");
						kv.put("whitelist", "on");
						kv.put("plugins", "Wisecraft Ghost Ping");
						kv.put("hostname", "§0W§1i§2s§3e§4c§5r§6a§7f§8t §9P§aE §bS§ce§dr§ev§fe§rr");//Colorful!
						kv.put("numplayers", Integer.MAX_VALUE + "");
						kv.put("version", "v0.15.6 alpha");
						kv.put("game_id", "MINECRAFTPE");
						kv.put("hostip", "127.0.0.1");
						kv.put("maxplayers", Integer.MAX_VALUE + "");
						for (Map.Entry<String,String> ent:kv.entrySet()) {
							resW.write(ent.getKey().getBytes(CompatCharsets.UTF_8));
							resW.write(0);
							resW.write(ent.getValue().getBytes(CompatCharsets.UTF_8));
							resW.write(0);
						}
						resW.write(0);
						//01 70 6C 61 79 65 72 5F 00 00
						resW.write((byte)0x01);
						resW.write((byte)0x70);
						resW.write((byte)0x6c);
						resW.write((byte)0x61);
						resW.write((byte)0x79);
						resW.write((byte)0x65);
						resW.write((byte)0x72);
						resW.write((byte)0x5f);
						resW.write((byte)0x00);
						resW.write((byte)0x00);
						//players
						resW.write(0);
						resW.write(0);
						
						resW.flush();
					}catch(Throwable e){}
					
					ServerStatus success=new ServerStatus();//success server(PE)
					success.ip="localhost";
					success.port=19132;
					success.ping=123;
					success.response=new FullStat(result.toByteArray());
					list.add(success);
					
					Server error=success.cloneAsServer();//error server(PE)
					error.port++;
					list.add(error);
					
					Server pending=error.cloneAsServer();//pending server(PE)
					pending.port++;
					list.add(pending);
					
					return list;
				}
				
				public void onPostExecute(List<Server> lst){
					getAdapter().clear();
					getAdapter().getPingingMap().put(lst.get(2),true);
					addServers(lst);
				}
			}.execute();
			setRows(onCalculateRows());
		}

		@Override
		protected int onCalculateRows() {
			// TODO: Implement this method
			int rows;
			if(getResources().getBoolean(R.bool.is_port)){
				Log.d("FSA","calculating by the width of the screen");
				rows=Utils.calculateRows(getActivity());
			}else{
				Log.d("FSA","calculating by the width of the content");
				rows=Utils.calculateRows(getActivity(),Utils.getScreenWidth(getActivity())/2);
			}
			Log.d("FSA","calculated rows: "+rows);
			return rows;
		}
	}
	
	public static class ServerInfoToolbarFragment extends com.nao20010128nao.Wisecraft.misc.BaseFragment<FragmentSettingsActivity> {
		UsefulPagerAdapter adapter;
		ViewPager tabs;
		ServerListStyleLoader slsl;
		
		@Override
		public void onResume() {
			// TODO: Implement this method
			super.onResume();
			Toolbar tb=(Toolbar)findViewById(R.id.toolbar);
			slsl=(ServerListStyleLoader)getActivity().getSystemService(ContextWrappingExtender.SERVER_LIST_STYLE_LOADER);
			
			tabs = (ViewPager)findViewById(R.id.pager);
			tabs.setAdapter(adapter = new UsefulPagerAdapter(getChildFragmentManager()));
			PagerSlidingTabStrip psts=(PagerSlidingTabStrip)findViewById(R.id.tabs);
			psts.setViewPager(tabs);
			
			adapter.addTab(BlankFragment.class,"A");
			adapter.addTab(BlankFragment.class,"B");
			adapter.addTab(BlankFragment.class,"C");
			
			psts.setIndicatorColor(slsl.getTextColor());
			psts.setTextColor(slsl.getTextColor());
			psts.setOnPageChangeListener(new ColorUpdater(slsl.getTextColor(), ServerInfoActivity.translucent(slsl.getTextColor()), tabs, psts));

			findViewById(R.id.appbar).setBackgroundDrawable(slsl.load());
			
			{
				String title="§0W§1i§2s§3e§4c§5r§6a§7f§8t §9P§aE §bS§ce§dr§ev§fe§rr";
				if (pref.getBoolean("serverListColorFormattedText", false)) {
					tb.setTitle(Utils.parseMinecraftFormattingCode(title.toString(),slsl.getTextColor()));
				} else {
					tb.setTitle(Utils.deleteDecorations(title.toString()));
				}
			}
			
			{
				Menu menu=tb.getMenu();
				MenuItem updateBtn,seeTitleButton;
				
				int color= ContextCompat.getColor(getContext(), R.color.mainColor);
				seeTitleButton = menu.add(Menu.NONE, 0, 0, R.string.seeTitle);
				seeTitleButton.setIcon(TheApplication.instance.getTintedDrawable(com.nao20010128nao.MaterialIcons.R.drawable.ic_open_in_new_black_48dp, slsl.getTextColor(): color));
				MenuItemCompat.setShowAsAction(seeTitleButton, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

				updateBtn = menu.add(Menu.NONE, 1, 1, R.string.update);
				updateBtn.setIcon(TheApplication.instance.getTintedDrawable(com.nao20010128nao.MaterialIcons.R.drawable.ic_refresh_black_48dp, slsl.getTextColor(): color));
				MenuItemCompat.setShowAsAction(updateBtn, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
			}
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO: Implement this method
			return LayoutInflater.from(getActivity()).inflate(R.layout.server_info_pager_nobs,container,false);
		}
		
		public static class BlankFragment extends com.nao20010128nao.Wisecraft.misc.BaseFragment<FragmentSettingsActivity> {

			@Override
			public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
				// TODO: Implement this method
				return inflater.inflate(R.layout.none,container,false);
			}
		}
	}
}
