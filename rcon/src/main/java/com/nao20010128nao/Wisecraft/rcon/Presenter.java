package com.nao20010128nao.Wisecraft.rcon;
import android.app.Activity;

public interface Presenter
{
	int MESSAGE_SHOW_LENGTH_SHORT=0;
	int MESSAGE_SHOW_LENGTH_LONG=1;
	int getDialogStyleId();
	void showSelfMessage(Activity a, int strRes, int duration);
	void showSelfMessage(Activity a, String str, int duration);
	KeyChain getKeyChain();
}
