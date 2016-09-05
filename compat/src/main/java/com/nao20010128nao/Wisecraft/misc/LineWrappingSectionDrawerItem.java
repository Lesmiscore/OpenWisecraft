package com.nao20010128nao.Wisecraft.misc;

import android.view.*;
import com.mikepenz.materialdrawer.model.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;

public class LineWrappingSectionDrawerItem extends SectionDrawerItem implements IdContainer<LineWrappingSectionDrawerItem>{
    
    int id=View.NO_ID;
    
	@Override
	public int getLayoutRes() {
		// TODO: Implement this method
		return R.layout.drawer_item_section_linewrap;
	}

    @Override
    public int getIntId() {
        // TODO: Implement this method
        return id;
    }

    @Override
    public LineWrappingSectionDrawerItem setIntId(int id) {
        // TODO: Implement this method
        this.id=id;
        return this;
    }
}
