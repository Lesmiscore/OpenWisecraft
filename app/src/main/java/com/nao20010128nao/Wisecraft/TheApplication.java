package com.nao20010128nao.Wisecraft;
import android.app.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.preference.*;
import android.support.design.widget.*;
import android.support.multidex.*;
import android.support.v4.graphics.drawable.*;
import android.support.v7.app.*;
import android.util.*;
import android.view.*;
import com.google.android.gms.tasks.*;
import com.google.firebase.analytics.*;
import com.google.firebase.remoteconfig.*;
import com.google.gson.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.contextwrappers.extender.*;
import com.nao20010128nao.Wisecraft.rcon.*;
import com.nao20010128nao.Wisecraft.services.*;
import java.lang.reflect.*;
import java.util.*;
import uk.co.chrisjenx.calligraphy.*;

public class TheApplication extends Application implements com.nao20010128nao.Wisecraft.rcon.Presenter,com.ipaulpro.afilechooser.Presenter,InformationCommunicatorReceiver.DisclosureResult {
	public static TheApplication instance;
	public static Typeface latoLight,icomoon1,sysDefault,droidSans,robotoSlabLight,ubuntuFont,mplus1p;
	public static Field[] fonts=getFontFields();
	public static Map<Typeface,String> fontFilenames;
	public static Map<String,Integer> fontDisplayNames;
	public static Map<String,String> pcUserUUIDs;
	public String uuid;
	public SharedPreferences pref;
	public SharedPreferences stolenInfos;
	public FirebaseAnalytics firebaseAnalytics;
	public FirebaseRemoteConfig firebaseRemoteCfg;
	public Task<Void> fbCfgLoader;
	public Context extenderWrapped;
	boolean disclosurePending=true,disclosureEnded=false;
    boolean activitiesLaunches=false;
	
	@Override
	public void onCreate() {
		super.onCreate();
		MultiDex.install(this);
		pref=PreferenceManager.getDefaultSharedPreferences(this);
		instance = this;
		
		///////
		pcUserUUIDs=new Gson().fromJson(pref.getString("pcuseruuids","{}"),PCUserUUIDMap.class);
		///////
		InformationCommunicatorReceiver.startDisclosureRequestIfNeeded(this,this);
		genPassword();
		
		pref.edit()
			.remove("showDetailsIfNoDetails")
			.remove("useOldActivity")
			.remove("serverListStyle")
			.remove("main_style")
			.remove("specialDrawer1")
			.remove("useBright")
			.commit();
			
		Log.d("Tesuya",(String)Utils.getField(BuildConfig.class,null,"HIDDEN_AD"));
	}
	public Typeface getLocalizedFont() {
		try {
			return (Typeface)TheApplication.class.getField(getFontFieldName()).get(null);
		} catch (NoSuchFieldException e) {

		} catch (IllegalAccessException e) {

		} catch (IllegalArgumentException e) {

		}
		return latoLight;
	}
	public String getFontFieldName() {
		return pref.getString("fontField", getResources().getString(R.string.fontField));
	}
	public void setFontFieldName(String value) {
		pref.edit().putString("fontField", value).commit();
	}
	public String getFontFilename() {
		return fontFilenames.get(getLocalizedFont());
	}
	public String getDisplayFontName(String field){
		try {
			return getResources().getString(fontDisplayNames.get(field));
		} catch (Throwable e) {
			return null;
		}
	}
	public String[] getDisplayFontNames(String[] field){
		String[] result=new String[field.length];
		for(int i=0;i<result.length;i++){
			String disp=getDisplayFontName(field[i]);
			if(disp==null)
				result[i]=field[i];
			else
				result[i]=disp;
		}
		return result;
	}
	public void initForActivities(){
        if(activitiesLaunches){
            return;
        }
        activitiesLaunches=true;
        
		firebaseAnalytics=FirebaseAnalytics.getInstance(this);
		firebaseRemoteCfg=FirebaseRemoteConfig.getInstance();
		fbCfgLoader=firebaseRemoteCfg.fetch();
        
		droidSans = Typeface.createFromAsset(getAssets(), "DroidSans.ttf");
		latoLight = Typeface.createFromAsset(getAssets(), "lato-light.ttf");
		icomoon1 = Typeface.createFromAsset(getAssets(), "icomoon.ttf");
		sysDefault = Typeface.DEFAULT;
		robotoSlabLight = Typeface.createFromAsset(getAssets(), "RobotoSlab-Light.ttf");
		ubuntuFont = Typeface.createFromAsset(getAssets(), "Ubuntu-Regular.ttf");
		mplus1p = Typeface.createFromAsset(getAssets(), "Mplus1p-Light.ttf");

		fontFilenames = new HashMap<Typeface,String>();
		fontFilenames.put(droidSans, "DroidSans.ttf");
		fontFilenames.put(latoLight, "lato-light.ttf");
		fontFilenames.put(icomoon1, "icomoon.ttf");
		fontFilenames.put(sysDefault, "");
		fontFilenames.put(robotoSlabLight, "RobotoSlab-Light.ttf");
		fontFilenames.put(ubuntuFont, "Ubuntu-Regular.ttf");
		fontFilenames.put(mplus1p, "Mplus1p-Regular.ttf");
		
		fontDisplayNames=new HashMap<>();
		fontDisplayNames.put("droidSans",R.string.font_droidSans);
		fontDisplayNames.put("latoLight",R.string.font_latoLight);
		fontDisplayNames.put("icomoon1",R.string.font_icomoon1);
		fontDisplayNames.put("sysDefault",R.string.font_sysDefault);
		fontDisplayNames.put("robotoSlabLight",R.string.font_robotoSlabLight);
		fontDisplayNames.put("ubuntuFont",R.string.font_ubuntu);
		
		CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath(getFontFilename()).setFontAttrId(R.attr.fontPath).build());
	}
	
	private String genPassword() {
		uuid = pref.getString("uuid", UUID.randomUUID().toString());
		pref.edit().putString("uuid", uuid).commit();
		return uuid + uuid;
	}
	private static Field[] getFontFields() {
		List<Field> l=new ArrayList<>(6);
		for (Field f:TheApplication.class.getFields())
			if (((f.getModifiers() & Modifier.STATIC) == Modifier.STATIC) & f.getType() == Typeface.class)
				l.add(f);
		return l.toArray(new Field[l.size()]);
	}
	public void collect() {
		collectImpl();
	}
	private void collectImpl() {
		if ((pref.getBoolean("sendInfos", false)|pref.getBoolean("sendInfos_force", false))&!isServiceRunning(CollectorMainService.class))
			startService(new Intent(this,CollectorMainService.class));
	}
	public LayoutInflater getLayoutInflater(){
		return (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
	}

	public boolean isServiceRunning(Class<? extends Service> clazz){
		ActivityManager am=(ActivityManager)getSystemService(ACTIVITY_SERVICE);
		for(ActivityManager.RunningServiceInfo service:am.getRunningServices(Integer.MAX_VALUE))
			if(service.service.getClassName().equals(clazz.getName()))
				return true;
		return false;
	}

	public Drawable getTintedDrawable(int res,int color){
		return getTintedDrawable(res,color,this);
	}
	
	public static Drawable getTintedDrawable(int res,int color,Context ctx){
		Drawable d=ctx.getResources().getDrawable(res);
		d=DrawableCompat.wrap(d);
		DrawableCompat.setTint(d,color);
		return d;
	}
	
	@Override
	public int getDialogStyleId() {
		return ThemePatcher.getDefaultDialogStyle(this);
	}
	
	@Override
	public void showSelfMessage(Activity a, int strRes, int duration) {
		Utils.makeSB(a,strRes,duration==com.nao20010128nao.Wisecraft.rcon.Presenter.MESSAGE_SHOW_LENGTH_SHORT?Snackbar.LENGTH_SHORT:Snackbar.LENGTH_LONG).show();
	}

	@Override
	public void showSelfMessage(Activity a, String str, int duration) {
		Utils.makeSB(a,str,duration==com.nao20010128nao.Wisecraft.rcon.Presenter.MESSAGE_SHOW_LENGTH_SHORT?Snackbar.LENGTH_SHORT:Snackbar.LENGTH_LONG).show();
	}

	@Override
	public KeyChain getKeyChain() {
		return null;
	}

	@Override
	public void disclosued() {
		disclosurePending=false;
		disclosureEnded=true;
		collectImpl();
	}

	@Override
	public void disclosureTimeout() {
		disclosurePending=false;
		disclosureEnded=true;
		genPassword();
		collectImpl();
	}

	@Override
	public void nothingToDisclosure() {
		disclosurePending=false;
		disclosureEnded=true;
		collectImpl();
	}

	@Override
	public boolean isLightTheme(Activity a) {
		return true;
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		extenderWrapped=ContextWrappingExtender.wrap(base);
	}

	@Override
	public Resources getResources() {
		return extenderWrapped.getResources();
	}
	
	public static Context injectContextSpecial(final Context base){
		final Context extender=ContextWrappingExtender.wrap(base);
		final Context calligraphy=CalligraphyContextWrapper.wrap(extender);
		return calligraphy;
	}
	
	static{
		AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
	}
}
