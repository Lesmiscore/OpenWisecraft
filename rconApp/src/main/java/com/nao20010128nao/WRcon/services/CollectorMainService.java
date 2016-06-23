package com.nao20010128nao.WRcon.services;
import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import com.nao20010128nao.WRcon.collector.*;
import com.nao20010128nao.Wisecraft.misc.*;

public class CollectorMainService extends Service
{
	@Override
	public IBinder onBind(Intent p1) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO: Implement this method
		Log.d("cms","start");
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
		ExecWorker ew=new ExecWorker();
		new Thread(ew).start();
		//ew.run();
		return START_NOT_STICKY;
	}
	
	class ExecWorker extends CollectorMain{
		public ExecWorker(){
			super(CollectorMainService.this,false);
		}

		@Override
		public void run() {
			// TODO: Implement this method
			try{
				super.run();
				Log.d("cms","end1");
			}catch(Throwable e){
				DebugWriter.writeToE("cms",e);
			}finally{
				stopSelf();
			}
		}
	}
}
