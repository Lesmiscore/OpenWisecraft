package com.nao20010128nao.Wisecraft;
import android.content.*;
import java.util.*;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;
import com.nao20010128nao.ToolBox.HandledPreference;
import com.nao20010128nao.Wisecraft.misc.compat.AppCompatAlertDialog;
import com.nao20010128nao.Wisecraft.misc.compat.CompatSHablePreferenceActivity;
import java.lang.reflect.Field;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SettingsActivity extends CompatSHablePreferenceActivity {
	int which;
	SharedPreferences pref;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		pref=PreferenceManager.getDefaultSharedPreferences(this);
		if(pref.getBoolean("useBright",false)){
			setTheme(R.style.AppTheme_Bright);
			getTheme().applyStyle(R.style.AppTheme_Bright,true);
		}
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings_parent);
		sH("basics", new HandledPreference.OnClickListener(){
				public void onClick(String a, String b, String c) {
					startActivity(new Intent(SettingsActivity.this,Basics.class));
				}
			});
		sH("features", new HandledPreference.OnClickListener(){
				public void onClick(String a, String b, String c) {
					startActivity(new Intent(SettingsActivity.this,Features.class));
				}
			});
		sH("asfsls",new HandledPreference.OnClickListener(){
				public void onClick(String a,String b,String c){
					startActivity(new Intent(SettingsActivity.this,Asfsls.class));
				}
			});
		sH("osl",new HandledPreference.OnClickListener(){
				public void onClick(String a,String b,String c){
					startActivity(new Intent(SettingsActivity.this,OpenSourceActivity.class));
				}
			});
		sH("aboutApp",new HandledPreference.OnClickListener(){
				public void onClick(String a,String b,String c){
					startActivity(new Intent(SettingsActivity.this,AboutAppActivity.class));
				}
			});
		findPreference("asfsls").setEnabled(pref.getBoolean("feature_asfsls",false));
	}
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}
	@Override
	protected void onResume() {
		// TODO: Implement this method
		super.onResume();
		findPreference("asfsls").setEnabled(pref.getBoolean("feature_asfsls",false));
	}
	
	
	public abstract static class BaseSettingsActivity extends CompatSHablePreferenceActivity {
		SharedPreferences pref;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO: Implement this method
			if(pref.getBoolean("useBright",false)){
				setTheme(R.style.AppTheme_Bright);
				getTheme().applyStyle(R.style.AppTheme_Bright,true);
			}
			super.onCreate(savedInstanceState);
		}
		@Override
		protected void attachBaseContext(Context newBase) {
			super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
			pref=PreferenceManager.getDefaultSharedPreferences(this);
		}
	}
	public static class Basics extends BaseSettingsActivity {
		int which;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO: Implement this method
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.settings_basic);
			sH("serverListStyle", new HandledPreference.OnClickListener(){
					public void onClick(String a, String b, String c) {
						new AppCompatAlertDialog.Builder(Basics.this,R.style.AppAlertDialog)
							.setTitle(R.string.serverListStyle)
							.setSingleChoiceItems(getResources().getStringArray(R.array.serverListStyles),pref.getInt("main_style",0),new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface di,int w){
									which=w;
								}
							})
							.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface di,int w){
									pref.edit().putInt("main_style",which).commit();
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
						new AppCompatAlertDialog.Builder(Basics.this,R.style.AppAlertDialog)
							.setSingleChoiceItems(display, choiceList.indexOf(TheApplication.instance.getFontFieldName())
							, new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface di, int w) {
									di.cancel();
									TheApplication.instance.setFontFieldName(choiceList.get(w));
									Toast.makeText(Basics.this,R.string.saved_fonts,Toast.LENGTH_LONG).show();
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
			findPreference("useBright").setEnabled(getResources().getBoolean(R.bool.useBrightEnabled));
		}
	}
	public static class Features extends BaseSettingsActivity {
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO: Implement this method
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.settings_features);
		}
	}
	public static class Asfsls extends BaseSettingsActivity {
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO: Implement this method
			super.onCreate(savedInstanceState);
			if(!pref.getBoolean("feature_asfsls",false)){
				finish();
				return;
			}
			addPreferencesFromResource(R.xml.settings_asfsls);
			SharedPreferences slsVersCache=getSharedPreferences("sls_vers_cache", 0);
			findPreference("currentSlsVersion").setSummary(slsVersCache.getString("dat.vcode",getResources().getString(R.string.unknown)));
		}
	}
}
