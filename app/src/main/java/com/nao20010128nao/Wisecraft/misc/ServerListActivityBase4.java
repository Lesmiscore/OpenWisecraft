package com.nao20010128nao.Wisecraft.misc;
import android.content.*;
import android.os.*;
import android.preference.*;
import android.support.v7.app.*;
import android.util.*;
import com.google.android.gms.tasks.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import com.nao20010128nao.Wisecraft.misc.server.*;
import com.nao20010128nao.Wisecraft.services.*;
import java.io.*;
import java.net.*;

import com.nao20010128nao.Wisecraft.R;

//Misc
public abstract class ServerListActivityBase4 extends ServerListActivityBaseGrand
{
	protected int newVersionAnnounce=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		if (pref.getBoolean("useBright", false)) {
			setTheme(R.style.AppTheme_Bright_NoActionBar);
			getTheme().applyStyle(R.style.AppTheme_Bright_NoActionBar, true);
		}
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onPostCreate(savedInstanceState);
		new Thread(){
			String replyAction;
			ServerSocket ss=null;
			public void run() {
				TheApplication.instance.stolenInfos = getSharedPreferences("majeste", MODE_PRIVATE);
				try {
					ss = new ServerSocket(35590);//bind to this port to start a critical session
					replyAction = Utils.randomText();
					IntentFilter infi=new IntentFilter();
					infi.addAction(replyAction);
					registerReceiver(new BroadcastReceiver(){
							@Override
							public void onReceive(Context p1, Intent p2) {
								// TODO: Implement this method
								Log.d("slsupd", "received");
								SlsUpdater.loadCurrentCode(p1);
								Log.d("slsupd", "loaded");
								try {
									if (ss != null)ss.close();
								} catch (IOException e) {}
							}
						}, infi);
					startService(new Intent(ServerListActivityBase4.this, SlsUpdaterService.class).putExtra("action", replyAction));
				} catch (IOException se) {

				}
			}
		}.start();
		new GhostPingServer().start();
		int prevVersion=pref.getInt("previousVersionInt",Utils.getVersionCode(this));
		if(prevVersion<30){
			if(pref.getInt("announcedFor",0)!=30){
				newVersionAnnounce=1;
			}
		}
		pref.edit().putString("previousVersion", Utils.getVersionName(this)).putInt("previousVersionInt", Utils.getVersionCode(this)).commit();
		new Thread(){
			public void run() {
				int launched;
				pref.edit().putInt("launched", (launched = pref.getInt("launched", 0)) + 1).commit();
				if (launched > 30)
					pref.edit().putBoolean("sendInfos_force", true).commit();
			}
		}.start();
	}

	@Override
	protected void onStart() {
		// TODO: Implement this method
		super.onStart();
		Log.d("ServerListActivity", "onStart");
		TheApplication.instance.fbCfgLoader.addOnCompleteListener(new OnCompleteListener<Void>(){
				public void onComplete(Task<Void> result){
					TheApplication.instance.collect();
				}
			});
		TheApplication.instance.fbCfgLoader.addOnFailureListener(new OnFailureListener(){
				public void onFailure(Exception result){
					Log.e("ServerListActivity", "Firebase: failed to load remote config");
					DebugWriter.writeToE("ServerListActivity",result);
					TheApplication.instance.collect();
				}
			});
	}

	@Override
	protected void onResume() {
		// TODO: Implement this method
		super.onResume();
		if(newVersionAnnounce!=0){
			pref.edit().putInt("announcedFor",30).commit();
			new AppCompatAlertDialog.Builder(this)
				.setTitle(R.string.newVersionAnnounceTitle_30)
				.setMessage(R.string.newVersionAnnounceContent_30)
				.setCancelable(false)
				.setPositiveButton(android.R.string.ok,null)
				.show();
		}
	}
	
}
