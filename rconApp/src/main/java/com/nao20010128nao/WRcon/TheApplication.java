package com.nao20010128nao.WRcon;
import android.app.*;
import android.content.*;
import android.graphics.drawable.*;
import android.preference.*;
import android.support.design.widget.*;
import android.support.v4.graphics.drawable.*;
import android.view.*;
import com.google.android.gms.tasks.*;
import com.google.firebase.analytics.*;
import com.google.firebase.remoteconfig.*;
import com.google.gson.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.rcon.*;
import com.nao20010128nao.Wisecraft.services.*;
import java.util.*;
import com.google.gson.reflect.*;

public class TheApplication extends Application implements  com.nao20010128nao.Wisecraft.rcon.Presenter,
															com.nao20010128nao.Wisecraft.misc.collector.Presenter,
															InformationCommunicatorReceiver.DisclosureResult
{

	public static TheApplication instance;
	public String uuid;
	public SharedPreferences pref;
	public SharedPreferences stolenInfos;
	public FirebaseAnalytics firebaseAnalytics;
	public FirebaseRemoteConfig firebaseRemoteCfg;
	public Task<Void> fbCfgLoader;
	boolean disclosurePending=true,disclosureEnded=false;
	
	@Override
	public void onCreate() {
		super.onCreate();
		instance=this;
		pref=PreferenceManager.getDefaultSharedPreferences(this);
		InformationCommunicatorReceiver.startDisclosureRequestIfNeeded(this,this);
		genPassword();//collectImpl();
	}

	public void prepareFirebase(){
		firebaseAnalytics=FirebaseAnalytics.getInstance(this);
		firebaseRemoteCfg=FirebaseRemoteConfig.getInstance();
		fbCfgLoader=firebaseRemoteCfg.fetch();
	}
	
	@Override
	public int getDialogStyleId() {
		return R.style.AppAlertDialog;
	}

	@Override
	public void showSelfMessage(Activity a, int strRes, int duration) {
		Snackbar.make(a.findViewById(android.R.id.content),strRes,duration==com.nao20010128nao.Wisecraft.rcon.Presenter.MESSAGE_SHOW_LENGTH_SHORT?Snackbar.LENGTH_SHORT:Snackbar.LENGTH_LONG).show();
	}

	@Override
	public void showSelfMessage(Activity a, String str, int duration) {
		Snackbar.make(a.findViewById(android.R.id.content),str,duration==com.nao20010128nao.Wisecraft.rcon.Presenter.MESSAGE_SHOW_LENGTH_SHORT?Snackbar.LENGTH_SHORT:Snackbar.LENGTH_LONG).show();
	}

	@Override
	public KeyChain getKeyChain() {
		return null;
	}
	
	@Override
	public void disclosued() {
		
	}

	@Override
	public void disclosureTimeout() {
		
	}

	@Override
	public void nothingToDisclosure() {
		disclosurePending=false;
		disclosureEnded=true;
		collectImpl();
	}

	@Override
	public List<Object> getServerList() {
		return Arrays.asList(((List)new Gson().fromJson(pref.getString("servers", "[]"), new TypeToken<List<Server>>(){}.getType())).toArray());
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
		if(uuid==null)uuid=UUID.randomUUID().toString();
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
	public Drawable getTintedDrawable(int res,int color){
		Drawable d=getResources().getDrawable(res);
		d=DrawableCompat.wrap(d);
		DrawableCompat.setTint(d,color);
		return d;
	}
}
