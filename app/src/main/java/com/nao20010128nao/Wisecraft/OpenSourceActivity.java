package com.nao20010128nao.Wisecraft;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.nao20010128nao.TESTAPP.ScrollingActivity;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OpenSourceActivity extends ScrollingActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("useBright",false)){
			setTheme(R.style.AppTheme_Bright);
			getTheme().applyStyle(R.style.AppTheme_Bright_OpenSource,true);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.osl_parent);
	}
	
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}
}
