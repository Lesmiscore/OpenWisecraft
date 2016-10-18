package com.nao20010128nao.Wisecraft.misc;
import android.database.*;
import android.view.*;
import android.widget.*;

public class ListAdapterWrapper implements ListAdapter
{
	ListAdapter child;
	public ListAdapterWrapper(ListAdapter child){
		this.child=child;
	}
	
	@Override
	public void registerDataSetObserver(DataSetObserver p1) {
		child.registerDataSetObserver(p1);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver p1) {
		child.unregisterDataSetObserver(p1);
	}

	@Override
	public boolean isEnabled(int p1) {
		return child.isEnabled(p1);
	}

	@Override
	public long getItemId(int p1) {
		return child.getItemId(p1);
	}

	@Override
	public int getItemViewType(int p1) {
		return child.getItemViewType(p1);
	}

	@Override
	public Object getItem(int p1) {
		return child.getItem(p1);
	}

	@Override
	public int getViewTypeCount() {
		return child.getViewTypeCount();
	}

	@Override
	public View getView(int p1, View p2, ViewGroup p3) {
		return child.getView(p1,p2,p3);
	}

	@Override
	public boolean isEmpty() {
		return child.isEmpty();
	}

	@Override
	public boolean areAllItemsEnabled() {
		return child.areAllItemsEnabled();
	}

	@Override
	public int getCount() {
		return child.getCount();
	}

	@Override
	public boolean hasStableIds() {
		return child.hasStableIds();
	}

	@Override
	public int hashCode() {
		return child.hashCode();
	}
}
