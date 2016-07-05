package com.ipaulpro.afilechooser;
import android.app.Activity;

public interface Presenter
{
	public boolean isLightTheme(Activity a);
	public static final int MESSAGE_SHOW_LENGTH_SHORT=0;
	public static final int MESSAGE_SHOW_LENGTH_LONG=1;
	public int getDialogStyleId();
	public void showSelfMessage(Activity a,int strRes,int duration);
	public void showSelfMessage(Activity a,String str,int duration);
}
