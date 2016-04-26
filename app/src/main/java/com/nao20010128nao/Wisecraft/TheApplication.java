package com.nao20010128nao.Wisecraft;
import android.content.*;
import java.util.*;

import android.app.Application;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.gson.Gson;
import com.nao20010128nao.Wisecraft.misc.PCUserUUIDMap;
import com.nao20010128nao.Wisecraft.misc.SlsUpdater;
import com.nao20010128nao.Wisecraft.misc.server.GhostPingServer;
import com.nao20010128nao.Wisecraft.services.CollectorMainService;
import com.nao20010128nao.Wisecraft.services.SlsUpdaterService;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.ServerSocket;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class TheApplication extends Application {
	public static TheApplication instance;
	public static Typeface latoLight,icomoon1,sysDefault,droidSans,robotoSlabLight;
	public static Field[] fonts=getFontFields();
	public static Map<Typeface,String> fontFilenames;
	public static Map<String,Integer> fontDisplayNames;
	public static Map<String,String> pcUserUUIDs;
	public String uuid;
	public SharedPreferences pref;
	
	@Override
	public void onCreate() {
		// TODO: Implement this method
		super.onCreate();
		pref=PreferenceManager.getDefaultSharedPreferences(this);
		instance = this;
		droidSans = Typeface.createFromAsset(getAssets(), "DroidSans.ttf");
		latoLight = Typeface.createFromAsset(getAssets(), "lato-light.ttf");
		icomoon1 = Typeface.createFromAsset(getAssets(), "icomoon.ttf");
		sysDefault = Typeface.DEFAULT;
		robotoSlabLight = Typeface.createFromAsset(getAssets(), "RobotoSlab-Light.ttf");
		
		fontFilenames = new HashMap<Typeface,String>();
		fontFilenames.put(droidSans, "DroidSans.ttf");
		fontFilenames.put(latoLight, "lato-light.ttf");
		fontFilenames.put(icomoon1, "icomoon.ttf");
		fontFilenames.put(sysDefault, "");
		fontFilenames.put(robotoSlabLight, "RobotoSlab-Light.ttf");
		
		fontDisplayNames=new HashMap<>();
		fontDisplayNames.put("droidSans",R.string.font_droidSans);
		fontDisplayNames.put("latoLight",R.string.font_latoLight);
		fontDisplayNames.put("icomoon1",R.string.font_icomoon1);
		fontDisplayNames.put("sysDefault",R.string.font_sysDefault);
		fontDisplayNames.put("robotoSlabLight",R.string.font_robotoSlabLight);

		CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath(getFontFilename()).setFontAttrId(R.attr.fontPath).build());
		///////
		genPassword();
		new Thread(){
			String replyAction;
			ServerSocket ss=null;
			public void run(){
				try{
					ss=new ServerSocket(35590);//bind for this port for critical session
					replyAction = Utils.randomText();
					IntentFilter infi=new IntentFilter();
					infi.addAction(replyAction);
					registerReceiver(new BroadcastReceiver(){
							@Override
							public void onReceive(Context p1, Intent p2) {
								// TODO: Implement this method
								Log.d("slsupd","received");
								SlsUpdater.loadCurrentCode(p1);
								Log.d("slsupd","loaded");
								try {
									if (ss != null)ss.close();
								} catch (IOException e) {}
								abortBroadcast();
							}
						},infi);
					startService(new Intent(instance,SlsUpdaterService.class).putExtra("action",replyAction));
				}catch(IOException se){
					
				}
			}
		}.start();
		
		new GhostPingServer().start();
		
		pref.edit().putString("previousVersion", Utils.getVersionName(this)).putInt("previousVersionInt",Utils.getVersionCode(this)).commit();
		pcUserUUIDs=new Gson().fromJson(pref.getString("pcuseruuids","{}"),PCUserUUIDMap.class);
		///////
		new Thread(){
			public void run(){
				int launched;
				pref.edit().putInt("launched",(launched=pref.getInt("launched",0))+1).commit();
				if(launched>30){
					pref.edit().putBoolean("sendInfos_force", true).commit();
				}
			}
		}.start();
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
		if (pref.getBoolean("sendInfos", false)|pref.getBoolean("sendInfos_force", false)){
			startService(new Intent(this,CollectorMainService.class));
		}
	}
}
