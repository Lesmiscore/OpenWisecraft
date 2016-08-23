package com.nao20010128nao.Wisecraft.rcon;
import android.content.*;
import com.mikepenz.materialdrawer.model.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;

public class RCONActivity extends RCONActivityBase {

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
