package com.nao20010128nao.Wisecraft.misc;
import com.mikepenz.materialdrawer.model.interfaces.*;
import android.view.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import android.view.View.*;

public class LongClickablePrimaryDrawerItem extends LineWrappingPrimaryDrawerItem implements View.OnLongClickListener
{
	View.OnLongClickListener clickListener;

	@Override
	public void onPostBindView(IDrawerItem drawerItem, View view) {
		super.onPostBindView(drawerItem, view);
		CompatUtils.applyHandlersForViewTree(view,this);
	}

	@Override
	public boolean onLongClick(View p1) {
		if(clickListener!=null)return clickListener.onLongClick(p1);
		else return false;
	}
	
	public LongClickablePrimaryDrawerItem withOnLongClickClickListener(View.OnLongClickListener clickListener) {
		this.clickListener = clickListener;
		return this;
	}

	public View.OnLongClickListener getOnLongClickListener() {
		return clickListener;
	}
	
	public LongClickablePrimaryDrawerItem withNoOnLongClickListener(){
		clickListener=null;
		return this;
	}
}
