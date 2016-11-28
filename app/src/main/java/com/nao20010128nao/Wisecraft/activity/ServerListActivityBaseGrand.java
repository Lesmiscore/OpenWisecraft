package com.nao20010128nao.Wisecraft.activity;
import android.content.*;
import android.preference.*;
import android.support.v7.app.*;
import java.util.*;

//Only most common things
public abstract class ServerListActivityBaseGrand extends AppCompatActivity
{
	protected SharedPreferences pref;
	protected ActivityResultDispatcher resultDerived=new ActivityResultDispatcher();

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(newBase);
		pref = PreferenceManager.getDefaultSharedPreferences(this);
	}

	@Override
	public final void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		resultDerived.call(requestCode, resultCode, data);
	}
	
	public void addActivityResultReceiver(DispatchActivityResult dar){
		resultDerived.add(dar);
	}
	
	public interface DispatchActivityResult{
		public boolean dispatchActivityResult(int requestCode, int resultCode, Intent data,boolean consumed);
	}
	
	private class ActivityResultDispatcher extends ArrayList<DispatchActivityResult>{
		public boolean call(int requestCode, int resultCode, Intent data){
			boolean consumed=false;
			for(DispatchActivityResult d:this)consumed|=d.dispatchActivityResult(requestCode,resultCode,data,consumed);
			return consumed;
		}
	}
}
