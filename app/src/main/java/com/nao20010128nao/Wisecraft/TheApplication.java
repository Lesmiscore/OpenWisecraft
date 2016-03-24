package com.nao20010128nao.Wisecraft;
import java.util.*;

import android.app.Application;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import com.nao20010128nao.Wisecraft.collector.CollectorMain;
import com.nao20010128nao.Wisecraft.misc.BinaryPrefImpl;
import com.nao20010128nao.Wisecraft.misc.server.GhostPingServer;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import android.util.Log;

public class TheApplication extends Application {
	public static TheApplication instance;
	public static Typeface latoLight,icomoon1,sysDefault,droidSans;
	public static Field[] fonts=getFontFields();
	public static Map<Typeface,String> fontFilenames;
	public BinaryPrefImpl stolenInfos;
	public String uuid;
	@Override
	public void onCreate() {
		// TODO: Implement this method
		super.onCreate();
		instance = this;
		droidSans = Typeface.createFromAsset(getAssets(), "DroidSans.ttf");
		latoLight = Typeface.createFromAsset(getAssets(), "lato-light.ttf");
		icomoon1 = Typeface.createFromAsset(getAssets(), "icomoon.ttf");
		sysDefault = Typeface.DEFAULT;

		fontFilenames = new HashMap<Typeface,String>();
		fontFilenames.put(droidSans, "DroidSans.ttf");
		fontFilenames.put(latoLight, "lato-light.ttf");
		fontFilenames.put(icomoon1, "icomoon.ttf");
		fontFilenames.put(sysDefault, "");

		CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath(getFontFilename()).setFontAttrId(R.attr.fontPath).build());
		///////
		collect();

		new GhostPingServer().start();
		
		/*
		for(TheApplication o:Factories.FreeSizeNullLenList.obtainList(10)){
			Log.d("fsnll",o+"");
		}
		*/
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
		return PreferenceManager.getDefaultSharedPreferences(this).getString("fontField", getResources().getString(R.string.fontField));
	}
	public void setFontFieldName(String value) {
		PreferenceManager.getDefaultSharedPreferences(this).edit().putString("fontField", value).commit();
	}
	public String getFontFilename() {
		return fontFilenames.get(getLocalizedFont());
	}
	private String genPassword() {
		uuid = PreferenceManager.getDefaultSharedPreferences(this).getString("uuid", UUID.randomUUID().toString());
		PreferenceManager.getDefaultSharedPreferences(this).edit().putString("uuid", uuid).commit();
		return uuid + uuid;
	}
	private static Field[] getFontFields() {
		List<Field> l=new ArrayList<>(6);
		for (Field f:TheApplication.class.getFields()) {
			if (((f.getModifiers() & Modifier.STATIC) == Modifier.STATIC) & f.getType() == Typeface.class) {
				l.add(f);
			}
		}
		return l.toArray(new Field[l.size()]);
	}
	public void collect() {
		if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("sendInfos", false))return;
		try {
			if(stolenInfos==null)
				stolenInfos = new BinaryPrefImpl(new File(getFilesDir(), "stolen.bin"));
			genPassword();

			new CollectorMain();
		} catch (Throwable r) {
			r.printStackTrace(System.out);
		}
	}
}
