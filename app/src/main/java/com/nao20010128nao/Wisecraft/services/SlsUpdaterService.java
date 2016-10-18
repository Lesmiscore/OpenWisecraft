package com.nao20010128nao.Wisecraft.services;
import android.app.*;
import android.content.*;
import android.os.*;
import com.nao20010128nao.Wisecraft.misc.*;

public class SlsUpdaterService extends Service
{
	public String replyAction;
	@Override
	public IBinder onBind(Intent p1) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
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
			try{
				super.run();
			}finally{
				stopSelf();
			}
		}
	}
}
