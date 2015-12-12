package com.nao20010128nao.McServerPingPong;
import android.app.*;
import android.graphics.*;

public class TheApplication extends Application
{
	public static TheApplication instance;
	public static Typeface cinzelDecorative,latoLight;
	@Override
	public void onCreate() {
		// TODO: Implement this method
		super.onCreate();
		instance=this;
		cinzelDecorative=Typeface.createFromAsset(getAssets(),"cinzeldecorative.ttf");
		latoLight=Typeface.createFromAsset(getAssets(),"lato-light.ttf");
	}
}
