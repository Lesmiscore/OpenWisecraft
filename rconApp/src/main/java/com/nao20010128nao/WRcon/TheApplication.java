package com.nao20010128nao.WRcon;
import android.app.Application;
import android.app.Activity;
import android.support.design.widget.Snackbar;
import com.nao20010128nao.Wisecraft.rcon.KeyChain;

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

	@Override
	public void showSelfMessage(Activity a, int strRes, int duration) {
		// TODO: Implement this method
		Snackbar.make(a.findViewById(android.R.id.content),strRes,duration==com.nao20010128nao.Wisecraft.rcon.Presenter.MESSAGE_SHOW_LENGTH_SHORT?Snackbar.LENGTH_SHORT:Snackbar.LENGTH_LONG).show();
	}

	@Override
	public void showSelfMessage(Activity a, String str, int duration) {
		// TODO: Implement this method
		Snackbar.make(a.findViewById(android.R.id.content),str,duration==com.nao20010128nao.Wisecraft.rcon.Presenter.MESSAGE_SHOW_LENGTH_SHORT?Snackbar.LENGTH_SHORT:Snackbar.LENGTH_LONG).show();
	}

	@Override
	public KeyChain getKeyChain() {
		// TODO: Implement this method
		return null;
	}
}
