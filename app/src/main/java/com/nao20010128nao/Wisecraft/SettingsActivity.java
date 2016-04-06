package com.nao20010128nao.Wisecraft;
import android.content.*;
import java.util.*;

import android.app.AlertDialog;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;
import com.nao20010128nao.MCPE.SC.misc.SHablePreferenceActivity;
import com.nao20010128nao.ToolBox.HandledPreference;
import java.lang.reflect.Field;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SettingsActivity extends SHablePreferenceActivity {
	int which;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		final SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this);
		addPreferencesFromResource(R.xml.settings);
		sH("trickSheet", new HandledPreference.OnClickListener(){
				public void onClick(String a, String b, String c) {
					startActivity(new Intent(SettingsActivity.this, TrickSheet.class));
				}
			});
		sH("serverListStyle", new HandledPreference.OnClickListener(){
				public void onClick(String a, String b, String c) {
					new AlertDialog.Builder(SettingsActivity.this)
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
					new AlertDialog.Builder(SettingsActivity.this)
						.setSingleChoiceItems(display, choiceList.indexOf(TheApplication.instance.getFontFieldName())
						, new DialogInterface.OnClickListener(){
							public void onClick(DialogInterface di, int w) {
								di.cancel();
								TheApplication.instance.setFontFieldName(choiceList.get(w));
								Toast.makeText(SettingsActivity.this,R.string.saved_fonts,Toast.LENGTH_LONG).show();
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
		SharedPreferences slsVersCache=getSharedPreferences("sls_vers_cache", 0);
		findPreference("currentSlsVersion").setSummary(slsVersCache.getString("dat.vcode",getResources().getString(R.string.unknown)));
	}
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}
}
