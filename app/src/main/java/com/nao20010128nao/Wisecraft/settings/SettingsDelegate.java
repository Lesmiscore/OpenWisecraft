package com.nao20010128nao.Wisecraft.settings;
import android.app.*;
import android.content.*;
import android.preference.*;
import android.widget.*;
import com.nao20010128nao.ToolBox.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import java.lang.reflect.*;
import java.util.*;

import com.nao20010128nao.Wisecraft.R;

public class SettingsDelegate extends ContextWrapper
{
	Class basics,features,asfsls;
	SharedPreferences pref;
	PreferenceActivity act;
	boolean parent;
	int which;
	public SettingsDelegate(com.nao20010128nao.Wisecraft.settings.system.SettingsActivity a){
		super(a);
		act=a;
		parent=true;
		setupForNative();
	}
	public SettingsDelegate(com.nao20010128nao.Wisecraft.settings.compat.SettingsActivity a){
		super(a);
		act=a;
		parent=true;
		setupForCompat();
	}
	public SettingsDelegate(com.nao20010128nao.Wisecraft.settings.system.SettingsActivity.BaseSettingsActivity a){
		super(a);
		act=a;
		parent=false;
		setupForNative();
	}
	public SettingsDelegate(com.nao20010128nao.Wisecraft.settings.compat.SettingsActivity.BaseSettingsActivity a){
		super(a);
		act=a;
		parent=false;
		setupForCompat();
	}
	
	private void setupForNative(){
		basics=com.nao20010128nao.Wisecraft.settings.system.SettingsActivity.Basics.class;
		features=com.nao20010128nao.Wisecraft.settings.system.SettingsActivity.Features.class;
		asfsls=com.nao20010128nao.Wisecraft.settings.system.SettingsActivity.Asfsls.class;
	}
	private void setupForCompat(){
		basics=com.nao20010128nao.Wisecraft.settings.compat.SettingsActivity.Basics.class;
		features=com.nao20010128nao.Wisecraft.settings.compat.SettingsActivity.Features.class;
		asfsls=com.nao20010128nao.Wisecraft.settings.compat.SettingsActivity.Asfsls.class;
	}
	
	public void onCreate(){
		pref=PreferenceManager.getDefaultSharedPreferences(this);
		if(parent){
			act.addPreferencesFromResource(R.xml.settings_parent);
			sH("basics", new HandledPreference.OnClickListener(){
					public void onClick(String a, String b, String c) {
						startActivity(new Intent(SettingsDelegate.this,basics));
					}
				});
			sH("features", new HandledPreference.OnClickListener(){
					public void onClick(String a, String b, String c) {
						startActivity(new Intent(SettingsDelegate.this,features));
					}
				});
			sH("asfsls",new HandledPreference.OnClickListener(){
					public void onClick(String a,String b,String c){
						startActivity(new Intent(SettingsDelegate.this,asfsls));
					}
				});
			sH("osl",new HandledPreference.OnClickListener(){
					public void onClick(String a,String b,String c){
						startActivity(new Intent(SettingsDelegate.this,OpenSourceActivity.class));
					}
				});
			sH("aboutApp",new HandledPreference.OnClickListener(){
					public void onClick(String a,String b,String c){
						startActivity(new Intent(SettingsDelegate.this,AboutAppActivity.class));
					}
				});
			act.findPreference("asfsls").setEnabled(pref.getBoolean("feature_asfsls",false));
		}else{
			Class actClass=act.getClass();
			if(basics==actClass){
				act.addPreferencesFromResource(R.xml.settings_basic);
				sH("serverListStyle", new HandledPreference.OnClickListener(){
						public void onClick(String a, String b, String c) {
							new AppCompatAlertDialog.Builder(act,R.style.AppAlertDialog)
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
							new AppCompatAlertDialog.Builder(act,R.style.AppAlertDialog)
								.setSingleChoiceItems(display, choiceList.indexOf(TheApplication.instance.getFontFieldName())
								, new DialogInterface.OnClickListener(){
									public void onClick(DialogInterface di, int w) {
										di.cancel();
										TheApplication.instance.setFontFieldName(choiceList.get(w));
										Toast.makeText(act,R.string.saved_fonts,Toast.LENGTH_LONG).show();
									}
								})
								.show();
						}
						String[] getFontChoices() {
							List<String> l=new ArrayList();
							for (Field f:TheApplication.fonts) {
								l.add(f.getName());
							}
							l.remove("icomoon1");
							return Factories.strArray(l);
						}
					});
				act.findPreference("useBright").setEnabled(getResources().getBoolean(R.bool.useBrightEnabled));
			}else if(features==actClass){
				act.addPreferencesFromResource(R.xml.settings_features);
			}else if(asfsls==actClass){
				act.addPreferencesFromResource(R.xml.settings_asfsls);
				SharedPreferences slsVersCache=getSharedPreferences("sls_vers_cache", 0);
				act.findPreference("currentSlsVersion").setSummary(slsVersCache.getString("dat.vcode",getResources().getString(R.string.unknown)));
			}
		}
	}
	public void onResume() {
		if(parent){
			act.findPreference("asfsls").setEnabled(pref.getBoolean("feature_asfsls",false));
		}
	}
	private void sH(String s,HandledPreference.OnClickListener l){
		try {
			act.getClass().getMethod("sH", String.class, HandledPreference.OnClickListener.class).invoke(act, s, l);
		} catch (Throwable e) {
			DebugWriter.writeToE("SettingsActivity",e);
		}
	}
	
	
	public static void openAppSettings(Activity a){
		if(a.getResources().getBoolean(R.bool.useNativePreferenceActivity))
			a.startActivity(new Intent(a,com.nao20010128nao.Wisecraft.settings.system.SettingsActivity.class));
		else
			a.startActivity(new Intent(a,com.nao20010128nao.Wisecraft.settings.compat.SettingsActivity.class));
	}
}
