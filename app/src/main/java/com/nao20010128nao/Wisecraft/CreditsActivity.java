package com.nao20010128nao.Wisecraft;

import android.content.*;
import android.os.*;
import com.nao20010128nao.TESTAPP.*;

public class CreditsActivity extends ScrollingActivity 
{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
	}
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(TheApplication.injectContextSpecial(newBase));
	}
	
}
