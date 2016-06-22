package com.nao20010128nao.WRcon;
import com.nao20010128nao.Wisecraft.rcon.RCONActivityBase;
import android.os.Bundle;
import com.nao20010128nao.Wisecraft.misc.compat.AppCompatAlertDialog;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.view.MenuItemCompat;

public class RCONActivity extends RCONActivityBase
{
	boolean didSuccess=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setTitle(getIntent().getStringExtra("ip")+":"+getIntent().getIntExtra("port",0));
	}

	@Override
	public void exitActivity() {
		// TODO: Implement this method
		new AppCompatAlertDialog.Builder(this,R.style.AppAlertDialog)
			.setMessage(R.string.auSure_exit)
			.setNegativeButton(android.R.string.ok,new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface di,int w){
					RCONActivity.super.exitActivity();
				}
			})
			.setPositiveButton(android.R.string.cancel,new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface di,int w){
					RCONActivity.super.cancelExitActivity();
				}
			})
			.setCancelable(false)
			.show();
	}

	@Override
	protected void onConnectionSuccess() {
		// TODO: Implement this method
		didSuccess=true;
		invalidateOptionsMenu();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO: Implement this method
		if(!didSuccess)return super.onCreateOptionsMenu(menu);
		MenuItem reconnect=menu.add(Menu.NONE,0,0,R.string.reconnect).setIcon(R.drawable.ic_action_replay_dark);
		MenuItemCompat.setShowAsAction(reconnect,MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}
}
