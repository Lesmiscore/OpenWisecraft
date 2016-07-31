package com.nao20010128nao.Wisecraft.misc;
import android.content.*;
import android.os.*;
import android.preference.*;
import android.support.v7.app.*;

//Only most common things
public abstract class ServerListActivityBaseGrand extends AppCompatActivity
{
	protected SharedPreferences pref;

	@Override
	protected void attachBaseContext(Context newBase) {
		// TODO: Implement this method
		super.attachBaseContext(newBase);
		pref = PreferenceManager.getDefaultSharedPreferences(this);
	}

	@Override
	protected final void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO: Implement this method
		super.onActivityResult(requestCode, resultCode, data);
		dispatchActivityResult(requestCode, resultCode, data);
	}
	
	protected final void callSuperOnActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public abstract boolean dispatchActivityResult(int requestCode, int resultCode, Intent data);
}
