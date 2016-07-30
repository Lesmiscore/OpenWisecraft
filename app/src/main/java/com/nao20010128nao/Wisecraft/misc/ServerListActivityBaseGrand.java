package com.nao20010128nao.Wisecraft.misc;
import android.content.*;
import android.os.*;
import android.preference.*;
import android.support.v7.app.*;

//Only most common things
public class ServerListActivityBaseGrand extends AppCompatActivity
{
	protected SharedPreferences pref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		super.onCreate(savedInstanceState);
	}
}
