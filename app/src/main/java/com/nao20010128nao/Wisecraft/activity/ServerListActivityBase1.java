package com.nao20010128nao.Wisecraft.activity;

import android.content.pm.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.content.*;
import android.util.*;
import com.nao20010128nao.Wisecraft.misc.*;

import java.util.*;

//Permission Request Part
abstract class ServerListActivityBase1 extends ServerListActivityBase2
{
	HashMap<Integer,Metadata> permRequire=new HashMap<>();
	HashMap<Integer,Boolean> permReqResults=new HashMap<Integer,Boolean>(){
		@Override
		public Boolean get(Object key) {
			Boolean b = super.get(key);
			if (b == null) {
				return false;
			}
			return b;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addActivityResultReceiver((request, result, data, consumed) -> {
            if(consumed)return true;
            return permReqResults.get(request);
        });
	}
	
	public void doAfterRequirePerm(RequirePermissionResult r,String[] perms){
		int call=Math.abs(sr.nextInt())&0xf;
		while(permRequire.containsKey(call)){
			call=Math.abs(sr.nextInt())&0xf;
		}
		ArrayList<String> notAllowed=new ArrayList<>();
		ArrayList<String> unconfirmable=new ArrayList<>();
		for(String perm:perms)
			if(PermissionChecker.checkSelfPermission(this,perm)!=PermissionChecker.PERMISSION_GRANTED)
				notAllowed.add(perm);
		for(String s:notAllowed)Log.d("ServerListActivity","notAllowed:"+s);
		for(String s:unconfirmable)Log.d("ServerListActivity","unconfirmable:"+s);
		if(perms.length==unconfirmable.size()){
			Log.d("ServerListActivity","denied");
			r.onFailed(perms, Factories.strArray(unconfirmable));
			return;
		}
		if(notAllowed.isEmpty()&unconfirmable.isEmpty()){
			Log.d("ServerListActivity","nothing to ask");
			r.onSuccess();
			return;
		}
		Metadata md=new Metadata();
		md.rpr=r;
		md.currentlyDenied=Factories.strArray(unconfirmable);
		permRequire.put(call,md);
		ActivityCompat.requestPermissions(this,Factories.strArray(notAllowed),call);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if(!permRequire.containsKey(requestCode)){
			return;
		}
		Metadata md=permRequire.get(requestCode);
		
		ArrayList lst=new ArrayList();
		for (int i=0;i < grantResults.length;i++)
			if (grantResults[i]!=PackageManager.PERMISSION_GRANTED)
				lst.add(permissions[i]);
		lst.addAll(Arrays.asList(md.currentlyDenied));
		
		if(lst.isEmpty()){
			md.rpr.onSuccess();
			permReqResults.put(requestCode,true);
		}else{
			md.rpr.onFailed(Factories.strArray(lst),md.currentlyDenied);
			permReqResults.put(requestCode,false);
		}
		permRequire.remove(requestCode);
	}
	
	public interface RequirePermissionResult{
		void onSuccess();
		void onFailed(String[] corruptPerms, String[] unconfirmable);
	}
	
	class Metadata{
		RequirePermissionResult rpr;
		String[] currentlyDenied;
	}
}
