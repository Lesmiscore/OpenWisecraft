package com.nao20010128nao.Wisecraft.rcon;
import com.mikepenz.materialdrawer.model.*;
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
}
