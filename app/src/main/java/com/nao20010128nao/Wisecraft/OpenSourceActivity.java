package com.nao20010128nao.Wisecraft;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.preference.*;
import com.nao20010128nao.TESTAPP.*;
import com.nao20010128nao.Wisecraft.misc.*;

public class OpenSourceActivity extends ScrollingActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ThemePatcher.applyThemeForActivity(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.osl_parent);
		getSupportActionBar().setElevation(0f);
		Utils.getActionBarTextView(this).setTextColor(Color.WHITE);
	}
	
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(TheApplication.injectContextSpecial(newBase));
	}

	@Override
	public int getLayoutResId() {
		return R.layout.osl_decor;
	}
}
