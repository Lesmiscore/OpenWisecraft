package com.nao20010128nao.Wisecraft;
import com.nao20010128nao.TESTAPP.ScrollingActivity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;

public class AboutAppActivity extends ScrollingActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("useBright",false)){
			setTheme(R.style.AppTheme_Bright_OpenSource);
			getTheme().applyStyle(R.style.AppTheme_Bright_OpenSource,true);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_app);
	}

	@Override
	protected int getLayoutResId() {
		// TODO: Implement this method
		return R.layout.about_app_decor;
	}
}
