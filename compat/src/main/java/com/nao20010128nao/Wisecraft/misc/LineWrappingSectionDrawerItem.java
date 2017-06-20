package com.nao20010128nao.Wisecraft.misc;

import android.view.*;
import com.mikepenz.materialdrawer.model.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;

public class LineWrappingSectionDrawerItem extends SectionDrawerItem implements IdContainer<LineWrappingSectionDrawerItem> {

    int id = View.NO_ID;

    @Override
    public int getLayoutRes() {
        return R.layout.drawer_item_section_linewrap;
    }

    @Override
    public int getIntId() {
        return id;
    }

    @Override
    public LineWrappingSectionDrawerItem setIntId(int id) {
        this.id = id;
        return this;
    }
}
