package com.nao20010128nao.Wisecraft.rcon;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.nao20010128nao.Wisecraft.R;

public class RCONActivity extends RCONActivityBase
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("useBright",false)){
			setTheme(R.style.AppTheme_Bright);
			getTheme().applyStyle(R.style.AppTheme_Bright,true);
		}
		super.onCreate(savedInstanceState);
	}
}
