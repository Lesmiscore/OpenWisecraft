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

public class SettingsActivity extends SHablePreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		sH("trickSheet", new HandledPreference.OnClickListener(){
				public void onClick(String a, String b, String c) {
					startActivity(new Intent(SettingsActivity.this, TrickSheet.class));
				}
			});

	}
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}
}
