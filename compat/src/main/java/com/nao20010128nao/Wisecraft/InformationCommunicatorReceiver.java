package com.nao20010128nao.Wisecraft;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.IntentFilter;

public class InformationCommunicatorReceiver extends BroadcastReceiver
{
	public static final String ICR_REQUEST_ACTION="com.nao20010128nao.Wisecraft.DISCLOSURE_REQUEST";
	public static final String ICR_RESULT_ACTION="com.nao20010128nao.Wisecraft.DISCLOSURE_RESULT";
	DisclosureResult res;
	public InformationCommunicatorReceiver(){}
	private InformationCommunicatorReceiver(DisclosureResult dr){res=dr;}
	@Override
	public void onReceive(Context context, Intent request) {
		// TODO: Implement this method
		SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(context);
		if(request.getAction().equals(ICR_REQUEST_ACTION)){
			if(!sp.contains("uuid"))return;//nothing to disclosure
			Intent reply=new Intent();
			reply.setAction(ICR_RESULT_ACTION);
			reply.putExtra("disclosure",true);
			reply.putExtra("uuid",sp.getString("uuid",null));
			reply.putExtra("sending",sp.getBoolean("sendInfos_force",false)|sp.getBoolean("sendInfos",false));
			context.sendBroadcast(reply);
		}else if(request.getAction().equals(ICR_RESULT_ACTION)){
			if(!sp.getBoolean("disclosure",false))return;//reply is invalid or nothing was disclosured
			sp.edit().putString("uuid",request.getStringExtra("uuid")).putBoolean("sending",request.getBooleanExtra("sending",false)).commit();
			context.unregisterReceiver(this);
		}
	}
	public static boolean startDisclosureRequestIfNeeded(Context ctx,DisclosureResult dr){
		SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(ctx);
		if(sp.contains("uuid"))return false;
		BroadcastReceiver discloreMan=new InformationCommunicatorReceiver(dr){};
		IntentFilter infi=new IntentFilter();
		infi.addAction(ICR_RESULT_ACTION);
		ctx.registerReceiver(discloreMan,infi);
		ctx.sendBroadcast(new Intent().setAction(ICR_REQUEST_ACTION));
		return true;
	}
	public static interface DisclosureResult{
		public void disclosued();
		public void disclosureTimeout();
	}
}
