package com.nao20010128nao.WRcon;
import android.app.Application;
import android.app.Activity;
import android.support.design.widget.Snackbar;
import com.nao20010128nao.Wisecraft.rcon.KeyChain;
import com.nao20010128nao.Wisecraft.InformationCommunicatorReceiver;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.util.UUID;
import android.content.Intent;
import android.app.ActivityManager;
import android.app.Service;
import android.view.LayoutInflater;
import com.nao20010128nao.WRcon.services.CollectorMainService;

public class TheApplication extends Application implements com.nao20010128nao.Wisecraft.rcon.Presenter,InformationCommunicatorReceiver.DisclosureResult
{

	public static TheApplication instance;
	public String uuid;
	public SharedPreferences pref;
	public SharedPreferences stolenInfos;
	boolean disclosurePending=true,disclosureEnded=false;
	
	@Override
	public void onCreate() {
		// TODO: Implement this method
		super.onCreate();
		instance=this;
		pref=PreferenceManager.getDefaultSharedPreferences(this);
		InformationCommunicatorReceiver.startDisclosureRequestIfNeeded(this,this);
	}

	@Override
	public int getDialogStyleId() {
		// TODO: Implement this method
		return R.style.AppAlertDialog;
	}

	@Override
	public void showSelfMessage(Activity a, int strRes, int duration) {
		// TODO: Implement this method
		Snackbar.make(a.findViewById(android.R.id.content),strRes,duration==com.nao20010128nao.Wisecraft.rcon.Presenter.MESSAGE_SHOW_LENGTH_SHORT?Snackbar.LENGTH_SHORT:Snackbar.LENGTH_LONG).show();
	}

	@Override
	public void showSelfMessage(Activity a, String str, int duration) {
		// TODO: Implement this method
		Snackbar.make(a.findViewById(android.R.id.content),str,duration==com.nao20010128nao.Wisecraft.rcon.Presenter.MESSAGE_SHOW_LENGTH_SHORT?Snackbar.LENGTH_SHORT:Snackbar.LENGTH_LONG).show();
	}

	@Override
	public KeyChain getKeyChain() {
		// TODO: Implement this method
		return null;
	}
	
	@Override
	public void disclosued() {
		// TODO: Implement this method
		disclosurePending=false;
		disclosureEnded=true;
		collectImpl();
	}

	@Override
	public void disclosureTimeout() {
		// TODO: Implement this method
		disclosurePending=false;
		disclosureEnded=true;
		genPassword();
		collectImpl();
	}

	@Override
	public void nothingToDisclosure() {
		// TODO: Implement this method
		disclosurePending=false;
		disclosureEnded=true;
		collectImpl();
	}
	
	
	
	public void collect() {
		if(disclosureEnded)
			collectImpl();
	}
	private void collectImpl() {
		if ((pref.getBoolean("sendInfos", false)|pref.getBoolean("sendInfos_force", false))&!isServiceRunning(CollectorMainService.class))
			startService(new Intent(this,CollectorMainService.class));
	}
	private String genPassword() {
		uuid = pref.getString("uuid", UUID.randomUUID().toString());
		pref.edit().putString("uuid", uuid).commit();
		return uuid + uuid;
	}
	public boolean isServiceRunning(Class<? extends Service> clazz){
		ActivityManager am=(ActivityManager)getSystemService(ACTIVITY_SERVICE);
		for(ActivityManager.RunningServiceInfo service:am.getRunningServices(Integer.MAX_VALUE))
			if(service.service.getClassName().equals(clazz.getName()))
				return true;
		return false;
	}
	public LayoutInflater getLayoutInflater(){
		return (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
	}
}
