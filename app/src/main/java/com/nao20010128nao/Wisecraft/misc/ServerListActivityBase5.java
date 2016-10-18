package com.nao20010128nao.Wisecraft.misc;

import android.content.*;
import android.net.*;
import android.os.*;
import java.io.*;
import java.util.*;

public class ServerListActivityBase5 extends ServerListActivityBaseFields 
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
	
	public void startExtChooseFile(UriFileChooserResult result){
		int call=Math.abs(sr.nextInt())&0xf;
		while(externalFileSelectResults.containsKey(call)){
			call=Math.abs(sr.nextInt())&0xf;
		}
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		startActivityForResult(intent, call);
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
