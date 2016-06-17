package com.nao20010128nao.WRcon;
import android.app.Application;

public class TheApplication extends Application implements com.nao20010128nao.Wisecraft.rcon.Presenter
{

	public static TheApplication instance;
	@Override
	public void onCreate() {
		// TODO: Implement this method
		super.onCreate();
		instance=this;
	}

	@Override
	public int getDialogStyleId() {
		// TODO: Implement this method
		return R.style.AppAlertDialog;
	}

}
