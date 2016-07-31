package com.nao20010128nao.Wisecraft.misc;
import com.mikepenz.materialdrawer.model.*;
import com.nao20010128nao.Wisecraft.*;

/*Fix patch for Wisecraft to wrap lines.*/
public class LineWrappingPrimaryDrawerItem extends PrimaryDrawerItem
{
	@Override
	public int getLayoutRes() {
		// TODO: Implement this method
		return R.layout.drawer_item_primary_linewrap;
	}
}
