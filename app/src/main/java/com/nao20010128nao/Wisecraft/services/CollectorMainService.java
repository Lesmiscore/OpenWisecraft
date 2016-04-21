package com.nao20010128nao.Wisecraft.services;
import android.app.Service;
import android.os.IBinder;
import android.content.Intent;
import com.nao20010128nao.Wisecraft.collector.CollectorMain;

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
		ExecWorker ew=new ExecWorker();
		new Thread(ew).start();
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
			}finally{
				stopSelf();
			}
		}
	}
}
