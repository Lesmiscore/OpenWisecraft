package com.nao20010128nao.Wisecraft;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.content.Context;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import com.nao20010128nao.MCPE.SC.misc.SHablePreferenceActivity;
import com.nao20010128nao.ToolBox.HandledPreference;
import java.io.IOException;
import android.content.Intent;
import com.nao20010128nao.Wisecraft.extender.ContextWrappingExtender;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.DialogInterface;

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
	}
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}
}
