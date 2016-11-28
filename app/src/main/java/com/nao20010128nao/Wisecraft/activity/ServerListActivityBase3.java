package com.nao20010128nao.Wisecraft.activity;
import android.content.*;
import com.ipaulpro.afilechooser.*;
import com.nao20010128nao.Wisecraft.misc.Utils;

import java.io.*;

import android.os.*;


//Wrapper for aFileChooser
abstract class ServerListActivityBase3 extends ServerListActivityBase4
{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addActivityResultReceiver(new DispatchActivityResult(){
				@Override
				public boolean dispatchActivityResult(int requestCode, int resultCode, Intent data,boolean consumed) {
					if(localFileSelectResults.containsKey(requestCode)){
						switch(resultCode){
							case RESULT_OK:
								localFileSelectResults.get(requestCode).onSelected(new File(data.getStringExtra("path")));
								break;
							case RESULT_CANCELED:
								localFileSelectResults.get(requestCode).onSelectCancelled();
								break;
						}
						localFileSelectResults.remove(requestCode);
						return true;
					}
					return false;
				}
			});
	}
	
	public void startChooseFileForOpen(File startDir,FileChooserResult result){
		int call=Math.abs(sr.nextInt())&0xf;
		while(localFileSelectResults.containsKey(call)){
			call=Math.abs(sr.nextInt())&0xf;
		}
		Intent intent=new Intent(this,FileOpenChooserActivity.class);
		if(startDir!=null){
			intent.putExtra("path",startDir.toString());
		}
		localFileSelectResults.put(call, Utils.requireNonNull(result));
		startActivityForResult(intent,call);
	}
	
	public void startChooseFileForSelect(File startDir,FileChooserResult result){
		int call=Math.abs(sr.nextInt())&0xf;
		while(localFileSelectResults.containsKey(call)){
			call=Math.abs(sr.nextInt())&0xf;
		}
		Intent intent=new Intent(this,FileChooserActivity.class);
		if(startDir!=null){
			intent.putExtra("path",startDir.toString());
		}
		localFileSelectResults.put(call,Utils.requireNonNull(result));
		startActivityForResult(intent,call);
	}
	
	public void startChooseDirectory(File startDir,FileChooserResult result){
		int call=Math.abs(sr.nextInt())&0xf;
		while(localFileSelectResults.containsKey(call)){
			call=Math.abs(sr.nextInt())&0xf;
		}
		Intent intent=new Intent(this,DirectoryChooserActivity.class);
		if(startDir!=null){
			intent.putExtra("path",startDir.toString());
		}
		localFileSelectResults.put(call,Utils.requireNonNull(result));
		startActivityForResult(intent,call);
	}
}
