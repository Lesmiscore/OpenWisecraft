package com.nao20010128nao.Wisecraft.misc;
import android.view.*;
import com.mikepenz.materialdrawer.model.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;

/*Fix patch for Wisecraft to wrap lines.*/
public class LineWrappingPrimaryDrawerItem extends PrimaryDrawerItem implements IdContainer<LineWrappingPrimaryDrawerItem> {
    
    int id=View.NO_ID;

	@Override
	public int getLayoutRes() {
		return R.layout.drawer_item_primary_linewrap;
	}

    @Override
    public int getIntId() {
        return id;
    }

    @Override
    public LineWrappingPrimaryDrawerItem setIntId(int id) {
        this.id = id;
        return this;
    }
}
