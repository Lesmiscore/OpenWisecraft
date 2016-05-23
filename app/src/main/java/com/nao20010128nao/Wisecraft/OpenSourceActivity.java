package com.nao20010128nao.Wisecraft;
import android.content.*;
import android.os.*;
import android.preference.*;
import com.nao20010128nao.TESTAPP.*;
import uk.co.chrisjenx.calligraphy.*;

public class OpenSourceActivity extends ScrollingActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("useBright",false)){
			setTheme(R.style.AppTheme_Bright_OpenSource);
			getTheme().applyStyle(R.style.AppTheme_Bright_OpenSource,true);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.osl_parent);
		getSupportActionBar().setElevation(0f);
	}
	
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}

	@Override
	protected int getLayoutResId() {
		// TODO: Implement this method
		return R.layout.osl_decor;
	}
}
