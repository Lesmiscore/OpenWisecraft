package com.nao20010128nao.Wisecraft.misc;
import android.support.v7.app.AppCompatActivity;
import java.util.Map;
import java.io.File;
import java.util.HashMap;
import android.content.Intent;
import com.ipaulpro.afilechooser.FileOpenChooserActivity;
import java.security.SecureRandom;


//Wrapper for aFileChooser
public class ServerListActivityBase3 extends AppCompatActivity
{
	SecureRandom sr=new SecureRandom();
	Map<Integer,FileChooserResult> results=new HashMap<>();

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO: Implement this method
		super.onActivityResult(requestCode, resultCode, data);
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
		}
	}
	
	public void startChooseFile(File startDir,FileChooserResult result){
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
	
	public static interface FileChooserResult{
		public void onSelected(File f);
		public void onSelectCancelled();
	}
}
