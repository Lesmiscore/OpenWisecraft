package com.nao20010128nao.Wisecraft;
import android.content.*;
import com.mikepenz.materialdrawer.model.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.rcon.*;
import android.os.*;

public class RCONActivity extends RCONActivityBase {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		ThemePatcher.applyThemeForActivity(this);
		super.onCreate(savedInstanceState);
	}

	@Override
	public PrimaryDrawerItem onCreatePrimaryDrawerItem() {
		// TODO: Implement this method
		return new LineWrappingPrimaryDrawerItem();
	}

	@Override
	public SectionDrawerItem onCreateSectionDrawerItem() {
		// TODO: Implement this method
		return new LineWrappingSectionDrawerItem();
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		// TODO: Implement this method
		super.attachBaseContext(TheApplication.injectContextSpecial(newBase));
	}
}
