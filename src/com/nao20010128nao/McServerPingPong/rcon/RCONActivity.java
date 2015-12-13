package com.nao20010128nao.McServerPingPong.rcon;
import android.support.v4.app.*;
import android.os.*;
import com.nao20010128nao.McServerPingPong.*;

public class RCONActivity extends FragmentActivity
{
	FragmentTabHost fth;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rconmain);
		fth = (FragmentTabHost)findViewById(android.R.id.tabhost);
		fth.setup(this, getSupportFragmentManager(), R.id.container);
		
	}
}
