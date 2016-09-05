package com.nao20010128nao.Wisecraft.misc;
import android.view.*;
import com.mikepenz.materialdrawer.model.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;

/*Fix patch for Wisecraft to wrap lines.*/
public class LineWrappingPrimaryDrawerItem extends PrimaryDrawerItem implements IdContainer<LineWrappingPrimaryDrawerItem> {
    
    int id=View.NO_ID;

	@Override
	public int getLayoutRes() {
		// TODO: Implement this method
		return R.layout.drawer_item_primary_linewrap;
	}

    @Override
    public int getIntId() {
        // TODO: Implement this method
        return id;
    }

    @Override
    public LineWrappingPrimaryDrawerItem setIntId(int id) {
        // TODO: Implement this method
        this.id = id;
        return this;
    }
}
