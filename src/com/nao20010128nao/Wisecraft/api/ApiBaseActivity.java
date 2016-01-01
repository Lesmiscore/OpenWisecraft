package com.nao20010128nao.Wisecraft.api;
import android.support.v4.app.*;
import android.os.*;
import android.content.*;
import uk.co.chrisjenx.calligraphy.*;

public abstract class ApiBaseActivity extends FragmentActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
	}
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}
}
