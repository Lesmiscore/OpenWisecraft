package com.nao20010128nao.Wisecraft.misc;
import android.view.*;

import android.content.Context;
import android.database.DataSetObserver;
import android.widget.ListAdapter;
import com.nao20010128nao.Wisecraft.R;
import com.nao20010128nao.Wisecraft.Utils;
import java.security.SecureRandom;

public final class TopLineListAdapter implements ListAdapter
{
	final long FIRST_ITEM_ID=new SecureRandom().nextLong();
	ListAdapter child;
	LayoutInflater li;
	public TopLineListAdapter(ListAdapter la,LayoutInflater li){
		child=Utils.requireNonNull(la);
		this.li=Utils.requireNonNull(li);
	}
	public TopLineListAdapter(ListAdapter la,Context ctx){
		this(la,(LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
	}
	
	@Override
	public int getViewTypeCount() {
		// TODO: Implement this method
		return child.getViewTypeCount();
	}

	@Override
	public boolean areAllItemsEnabled() {
		// TODO: Implement this method
		return child.areAllItemsEnabled();
	}

	@Override
	public boolean isEmpty() {
		// TODO: Implement this method
		return false;//TopLineListAdapter has one item
	}

	@Override
	public long getItemId(int p1) {
		// TODO: Implement this method
		if(p1==0){
			return FIRST_ITEM_ID;
		}else{
			p1--;
		}
		return child.getItemId(p1);
	}

	@Override
	public void registerDataSetObserver(DataSetObserver p1) {
		// TODO: Implement this method
		child.registerDataSetObserver(p1);
	}

	@Override
	public Object getItem(int p1) {
		// TODO: Implement this method
		if(p1==0){
			return null;
		}else{
			p1--;
		}
		return child.getItem(p1);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver p1) {
		// TODO: Implement this method
		child.unregisterDataSetObserver(p1);
	}

	@Override
	public int getItemViewType(int p1) {
		// TODO: Implement this method
		if(p1==0){
			return 0;
		}else{
			p1--;
		}
		return child.getItemViewType(p1);
	}

	@Override
	public int getCount() {
		// TODO: Implement this method
		return child.getCount()+1;
	}

	@Override
	public View getView(int p1, View p2, ViewGroup p3) {
		// TODO: Implement this method
		if(p1==0){
			if(p2==null){
				return li.inflate(R.layout.void_view,null);
			}
			return p2;
		}else{
			p1--;
		}
		return child.getView(p1,p2,p3);
	}

	@Override
	public boolean isEnabled(int p1) {
		// TODO: Implement this method
		if(p1==0){
			return true;
		}else{
			p1--;
		}
		return child.isEnabled(p1);
	}

	@Override
	public boolean hasStableIds() {
		// TODO: Implement this method
		return child.hasStableIds();
	}
}
