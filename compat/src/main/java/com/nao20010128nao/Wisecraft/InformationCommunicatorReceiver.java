package com.nao20010128nao.Wisecraft;

import android.content.*;
import android.os.*;
import android.preference.*;
import android.util.*;
import com.google.gson.*;

import java.util.*;

public class InformationCommunicatorReceiver extends BroadcastReceiver
{
	public static final String ICR_REQUEST_ACTION="com.nao20010128nao.Wisecraft.DISCLOSURE_REQUEST";
	public static final String ICR_RESULT_ACTION="com.nao20010128nao.Wisecraft.DISCLOSURE_RESULT";
	DisclosureResult res;
	public InformationCommunicatorReceiver(){}
	private InformationCommunicatorReceiver(DisclosureResult dr){
		new Handler().postDelayed(() -> {
            res.disclosureTimeout();
            res=null;
        },1000000);
		res=dr;
	}
	@Override
	public void onReceive(Context context, Intent request) {
		SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(context);
		if(request.getAction().equals(ICR_REQUEST_ACTION)){
			if(!sp.contains("uuid"))return;//nothing to disclosure
			Intent reply=new Intent();
			reply.setAction(ICR_RESULT_ACTION);
			reply.putExtra("disclosure",true);
			reply.putExtra("uuid",sp.getString("uuid",null));
			reply.putExtra("sending",sp.getBoolean("sendInfos_force",false)|sp.getBoolean("sendInfos",false));
			reply.putExtra("package",context.getPackageName());
			context.sendBroadcast(reply);
		}else if(request.getAction().equals(ICR_RESULT_ACTION)){
			if(!sp.getBoolean("disclosure",false))return;//reply is invalid or nothing was disclosured
			Log.d("uuid_disclosured",request.getStringExtra("uuid"));
			Gson gson=new Gson();
			Map<String,String> values=gson.fromJson(sp.getString("related","{}"),UuidMap.class);
			values.put(request.getStringExtra("package"),request.getStringExtra("uuid"));
			sp.edit().putString("related",gson.toJson(values)).commit();
			context.unregisterReceiver(this);
			if(res!=null)res.disclosued();
		}
	}
	public static boolean startDisclosureRequestIfNeeded(Context ctx,DisclosureResult dr){
		SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(ctx);
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
		public void nothingToDisclosure();
	}
	public static class UuidMap extends HashMap<String,String>{
		
	}
}
