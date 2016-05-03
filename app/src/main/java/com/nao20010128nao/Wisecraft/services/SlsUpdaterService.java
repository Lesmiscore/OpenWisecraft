package com.nao20010128nao.Wisecraft.services;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.nao20010128nao.Wisecraft.misc.SlsUpdater;

public class SlsUpdaterService extends Service
{
	public String replyAction;
	@Override
	public IBinder onBind(Intent p1) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO: Implement this method
		replyAction=intent.getStringExtra("action");
		ExecWorker ew=new ExecWorker();
		ew.start();
		return START_NOT_STICKY;
	}

	class ExecWorker extends SlsUpdater{
		public ExecWorker(){
			super(SlsUpdaterService.this);
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