package com.nao20010128nao.Wisecraft;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.content.Context;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import com.nao20010128nao.MCPE.SC.misc.SHablePreferenceActivity;
import com.nao20010128nao.ToolBox.HandledPreference;
import java.io.IOException;

public class SettingsActivity extends SHablePreferenceActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		sH("trickSheet",new HandledPreference.OnClickListener(){
			public void onClick(String a,String b,String c){
				try {
					new ProcessBuilder("am start -a com.nao20010128nao.TRICK_SHEET -c android.intent.category.DEFAULT --user 0 com.nao20010128nao.Wisecraft/.TrickSheet".split("\\ ")).start();
				} catch (IOException e) {}
			}
		});
	}
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}
}
