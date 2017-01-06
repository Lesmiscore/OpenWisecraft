package com.nao20010128nao.Wisecraft.activity;

import android.content.*;
import android.net.*;
import android.os.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import java.io.*;
import java.util.*;
import permissions.dispatcher.*;

import com.nao20010128nao.Wisecraft.R;

@RuntimePermissions
abstract class ServerListActivityBase5 extends ServerListActivityBaseFields 
{
	protected Map<Integer,UriFileChooserResult> externalFileSelectResults=new HashMap<>();
	protected Map<Integer,FileChooserResult> localFileSelectResults=new HashMap<>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addActivityResultReceiver(new DispatchActivityResult(){
				@Override
				public boolean dispatchActivityResult(int requestCode, int resultCode, Intent data,boolean consumed) {
					if(externalFileSelectResults.containsKey(requestCode)){
						switch(resultCode){
							case RESULT_OK:
								externalFileSelectResults.get(requestCode).onSelected(data.getData());
								break;
							case RESULT_CANCELED:
								externalFileSelectResults.get(requestCode).onSelectCancelled();
								break;
						}
						externalFileSelectResults.remove(requestCode);
						return true;
					}
					return false;
				}
			});
	}
	
	@NeedsPermission({"android.permission.WRITE_EXTERNAL_STORAGE"})
	public void startExtChooseFile(UriFileChooserResult result){
		int call=Math.abs(sr.nextInt())&0xf;
		while(externalFileSelectResults.containsKey(call)){
			call=Math.abs(sr.nextInt())&0xf;
		}
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		startActivityForResult(intent, call);
	}
	
	@OnShowRationale({"android.permission.WRITE_EXTERNAL_STORAGE"})
	@Deprecated
	public void _startExtChooseFileRationale(PermissionRequest req){
		Utils.describeForPermissionRequired(this,new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"},req,R.string.permissionsRequiredReasonSelectFile);
	}
	
	@OnPermissionDenied({"android.permission.WRITE_EXTERNAL_STORAGE"})
	@Deprecated
	public void _startExtChooseFileError(){
		Utils.showPermissionError(this,new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"},R.string.permissionsRequiredReasonSelectFile);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		ServerListActivityBase5PermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
	}
	
	public static interface FileChooserResult extends ChooserResult<File>{
		
	}
	public static interface UriFileChooserResult extends ChooserResult<Uri>{
		
	}
	public static interface ChooserResult<R>{
		public void onSelected(R f);
		public void onSelectCancelled();
	}
}
