package com.nao20010128nao.WRcon;
import com.nao20010128nao.Wisecraft.rcon.RCONActivityBase;
import android.os.Bundle;
import com.nao20010128nao.Wisecraft.misc.compat.AppCompatAlertDialog;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.view.MenuItemCompat;
import android.content.Intent;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.graphics.Color;
import android.text.Spannable;

public class RCONActivity extends RCONActivityBase
{
	boolean didSuccess=false;
	String password;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		SpannableStringBuilder ssb=new SpannableStringBuilder();
		ssb.append(getIntent().getStringExtra("ip")+":"+getIntent().getIntExtra("port",0));
		ssb.setSpan(new ForegroundColorSpan(Color.WHITE),0,ssb.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		setTitle(ssb);
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
	protected void onConnectionSuccess(String s) {
		// TODO: Implement this method
		didSuccess=true;
		password=s;
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO: Implement this method
		switch(item.getItemId()){
			case 0:
				Intent restart=(Intent)getIntent().clone();
				restart.putExtra("password",password);
				super.exitActivity();
				startActivity(restart);
				break;
		}
		return true;
	}
}
