package com.nao20010128nao.Wisecraft.misc;
import com.nao20010128nao.Wisecraft.misc.compat.AppCompatListActivity;
import java.util.HashMap;
import java.security.SecureRandom;
import android.support.v4.app.ActivityCompat;
import java.util.ArrayList;
import android.content.pm.PackageManager;
import com.nao20010128nao.Wisecraft.Factories;
import android.content.Intent;
import java.util.Arrays;

public class ServerListActivityBase extends AppCompatListActivity
{
	SecureRandom sr=new SecureRandom();
	HashMap<Integer,Metadata> permRequire=new HashMap<>();
	HashMap<Integer,Boolean> permReqResults=new HashMap<Integer,Boolean>(){
		@Override
		public Boolean get(Object key) {
			// TODO: Implement this method
			Boolean b = super.get(key);
			if (b == null) {
				return false;
			}
			return b;
		}
	};
	
	
	public void doAfterRequirePerm(RequirePermissionResult r,String[] perms){
		int call=Math.abs(sr.nextInt())&0xfffffff0;
		while(permRequire.containsKey(call)){
			call=Math.abs(sr.nextInt())&0xfffffff0;
		}
		ArrayList<String> notAllowed=new ArrayList<>();
		ArrayList<String> unconfirmable=new ArrayList<>();
		for(String perm:perms)
			if(ActivityCompat.checkSelfPermission(this,perm)==PackageManager.PERMISSION_DENIED)
				if(ActivityCompat.shouldShowRequestPermissionRationale(this,perm))
					notAllowed.add(perm);
				else
					unconfirmable.add(perm);
		if(perms.length==unconfirmable.size()){
			r.onFailed(perms,Factories.strArray(unconfirmable));
			return;
		}
		if(notAllowed.isEmpty()&unconfirmable.isEmpty()){
			r.onSuccess();
			return;
		}
		Metadata md=new Metadata();
		md.rpr=r;
		md.currentlyDenied=Factories.strArray(unconfirmable);
		permRequire.put(call,md);
		ActivityCompat.requestPermissions(this,Factories.strArray(notAllowed),call);
	}
	
	protected boolean dispatchActivityResult(int request,int result,Intent data){
		super.onActivityResult(request,result,data);
		return permReqResults.get(request);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO: Implement this method
		dispatchActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		// TODO: Implement this method
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if(!permRequire.containsKey(requestCode)){
			return;
		}
		Metadata md=permRequire.get(requestCode);
		
		ArrayList lst=new ArrayList();
		for (int i=0;i < grantResults.length;i++)
			if (grantResults[i]==PackageManager.PERMISSION_DENIED)
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
	
	public static interface RequirePermissionResult{
		public void onSuccess();
		public void onFailed(String[] corruptPerms,String[] unconfirmable);
	}
	
	class Metadata{
		RequirePermissionResult rpr;
		String[] currentlyDenied;
	}
}
