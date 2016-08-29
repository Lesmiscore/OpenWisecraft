package com.nao20010128nao.Wisecraft.misc;
import android.content.*;
import com.ipaulpro.afilechooser.*;
import java.io.*;
import java.security.*;
import java.util.*;


//Wrapper for aFileChooser
public abstract class ServerListActivityBase3 extends ServerListActivityBase4
{
	Map<Integer,FileChooserResult> results=new HashMap<>();

	@Override
	public boolean dispatchActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO: Implement this method
		if(results.containsKey(requestCode)){
			switch(resultCode){
				case RESULT_OK:
					results.get(requestCode).onSelected(new File(data.getStringExtra("path")));
					break;
				case RESULT_CANCELED:
					results.get(requestCode).onSelectCancelled();
					break;
			}
			results.remove(requestCode);
			return true;
		}
		return false;
	}
	
	public void startChooseFileForOpen(File startDir,FileChooserResult result){
		int call=Math.abs(sr.nextInt())&0xf;
		while(results.containsKey(call)){
			call=Math.abs(sr.nextInt())&0xf;
		}
		Intent intent=new Intent(this,FileOpenChooserActivity.class);
		if(startDir!=null){
			intent.putExtra("path",startDir.toString());
		}
		results.put(call,Utils.requireNonNull(result));
		startActivityForResult(intent,call);
	}
	
	public void startChooseFileForSelect(File startDir,FileChooserResult result){
		int call=Math.abs(sr.nextInt())&0xf;
		while(results.containsKey(call)){
			call=Math.abs(sr.nextInt())&0xf;
		}
		Intent intent=new Intent(this,FileChooserActivity.class);
		if(startDir!=null){
			intent.putExtra("path",startDir.toString());
		}
		results.put(call,Utils.requireNonNull(result));
		startActivityForResult(intent,call);
	}
	
	public void startChooseDirectory(File startDir,FileChooserResult result){
		int call=Math.abs(sr.nextInt())&0xf;
		while(results.containsKey(call)){
			call=Math.abs(sr.nextInt())&0xf;
		}
		Intent intent=new Intent(this,DirectoryChooserActivity.class);
		if(startDir!=null){
			intent.putExtra("path",startDir.toString());
		}
		results.put(call,Utils.requireNonNull(result));
		startActivityForResult(intent,call);
	}
	
	
	public static interface FileChooserResult{
		public void onSelected(File f);
		public void onSelectCancelled();
	}
}
