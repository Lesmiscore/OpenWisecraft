package com.nao20010128nao.Wisecraft.settings.compat;

import android.content.*;
import android.os.*;
import android.preference.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import com.nao20010128nao.Wisecraft.settings.*;
import uk.co.chrisjenx.calligraphy.*;

import com.nao20010128nao.Wisecraft.R;

public class SettingsActivity extends CompatSHablePreferenceActivity {
	SettingsDelegate delegate;
	int which;
	SharedPreferences pref;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		pref=PreferenceManager.getDefaultSharedPreferences(this);
		super.onCreate(savedInstanceState);
		delegate.onCreate();
	}
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(TheApplication.injectContextSpecial(newBase));
		delegate=new SettingsDelegate(this);
	}
	@Override
	protected void onResume() {
		// TODO: Implement this method
		super.onResume();
		delegate.onResume();
	}


	public abstract static class BaseSettingsActivity extends CompatSHablePreferenceActivity {
		SharedPreferences pref;
		SettingsDelegate delegate;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO: Implement this method
			super.onCreate(savedInstanceState);
			delegate.onCreate();
		}
		@Override
		protected void attachBaseContext(Context newBase) {
			super.attachBaseContext(TheApplication.injectContextSpecial(newBase));
			pref=PreferenceManager.getDefaultSharedPreferences(this);
			delegate=new SettingsDelegate(this);
		}
	}
	public static class Basics extends BaseSettingsActivity {
		
	}
	public static class Features extends BaseSettingsActivity {
		
	}
	public static class Asfsls extends BaseSettingsActivity {
		
	}
}
