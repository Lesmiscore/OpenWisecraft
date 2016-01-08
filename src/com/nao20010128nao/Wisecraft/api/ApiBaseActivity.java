package com.nao20010128nao.Wisecraft.api;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
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
