package com.nao20010128nao.Wisecraft;
import android.content.*;
import com.mikepenz.materialdrawer.model.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.rcon.*;
import android.os.*;

public class RCONActivity extends RCONActivityBase {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ThemePatcher.applyThemeForActivity(this);
		super.onCreate(savedInstanceState);
	}

	@Override
	public PrimaryDrawerItem onCreatePrimaryDrawerItem() {
		return new LineWrappingPrimaryDrawerItem();
	}

	@Override
	public SectionDrawerItem onCreateSectionDrawerItem() {
		return new LineWrappingSectionDrawerItem();
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(TheApplication.injectContextSpecial(newBase));
	}
}
