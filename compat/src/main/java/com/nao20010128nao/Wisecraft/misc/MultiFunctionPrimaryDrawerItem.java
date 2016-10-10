package com.nao20010128nao.Wisecraft.misc;
import android.content.*;
import android.view.*;
import android.widget.*;
import com.mikepenz.materialdrawer.*;
import com.mikepenz.materialdrawer.holder.*;
import com.mikepenz.materialdrawer.model.interfaces.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import java.util.*;

import com.nao20010128nao.Wisecraft.misc.compat.R;

public class MultiFunctionPrimaryDrawerItem extends LineWrappingPrimaryDrawerItem implements View.OnClickListener
{
	Drawer.OnDrawerItemClickListener clickListener;
	
    @Override
    public void bindView(ViewHolder viewHolder, List payloads) {
        Context ctx = viewHolder.itemView.getContext();
		View badgeContainer;
		TextView badge;
		badgeContainer = viewHolder.itemView.findViewById(R.id.material_drawer_badge_container);
		badge = (TextView) viewHolder.itemView.findViewById(R.id.material_drawer_badge);
        bindViewHelper(new ViewHolder(viewHolder.itemView.findViewById(R.id.main)));
        boolean badgeVisible = StringHolder.applyToOrHide(mBadge, badge);
        if (badgeVisible) {
            mBadgeStyle.style(badge, getTextColorStateList(getColor(ctx), getSelectedTextColor(ctx)));
            badgeContainer.setVisibility(View.VISIBLE);
        } else {
            badgeContainer.setVisibility(View.GONE);
        }
        if (getTypeface() != null) {
            badge.setTypeface(getTypeface());
        }

    	onPostBindView(this, viewHolder.itemView);
    }
	
	@Override
	public void onPostBindView(IDrawerItem drawerItem, View view) {
		super.onPostBindView(drawerItem, view);
		if(clickListener!=null){
			LinearLayout ll=(LinearLayout)view.findViewById(R.id.imageFrame);
			ll.setVisibility(View.VISIBLE);
			CompatUtils.applyHandlersForViewTree(view.findViewById(R.id.imageFrame),this);
		}else{
			view.findViewById(R.id.imageFrame).setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View p1) {
		View decor=(View)((View)(p1.getId()==R.id.image?p1.getParent():p1)).getParent();
		if(clickListener!=null)clickListener.onItemClick(decor,-1,this);
	}
	
	@Override
	public int getLayoutRes() {
		return R.layout.drawer_item_primary_twofunc;
	}
	
	public MultiFunctionPrimaryDrawerItem withAnotherClickListener(Drawer.OnDrawerItemClickListener clickListener) {
		this.clickListener = clickListener;
		return this;
	}

	public Drawer.OnDrawerItemClickListener getAnotherClickListener() {
		return clickListener;
	}
	
	public MultiFunctionPrimaryDrawerItem withNoAnotherClickListener(){
		clickListener=null;
		return this;
	}
}
