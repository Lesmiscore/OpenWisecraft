package com.nao20010128nao.WRcon;
import com.nao20010128nao.Wisecraft.rcon.RCONActivityBase;
import android.os.Bundle;

public class RCONActivity extends RCONActivityBase
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setTitle(getIntent().getStringExtra("ip")+":"+getIntent().getIntExtra("port",0));
	}
}
