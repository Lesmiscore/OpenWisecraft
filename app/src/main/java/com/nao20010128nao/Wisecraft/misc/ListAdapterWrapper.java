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
		// TODO: Implement this method
		child.registerDataSetObserver(p1);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver p1) {
		// TODO: Implement this method
		child.unregisterDataSetObserver(p1);
	}

	@Override
	public boolean isEnabled(int p1) {
		// TODO: Implement this method
		return child.isEnabled(p1);
	}

	@Override
	public long getItemId(int p1) {
		// TODO: Implement this method
		return child.getItemId(p1);
	}

	@Override
	public int getItemViewType(int p1) {
		// TODO: Implement this method
		return child.getItemViewType(p1);
	}

	@Override
	public Object getItem(int p1) {
		// TODO: Implement this method
		return child.getItem(p1);
	}

	@Override
	public int getViewTypeCount() {
		// TODO: Implement this method
		return child.getViewTypeCount();
	}

	@Override
	public View getView(int p1, View p2, ViewGroup p3) {
		// TODO: Implement this method
		return child.getView(p1,p2,p3);
	}

	@Override
	public boolean isEmpty() {
		// TODO: Implement this method
		return child.isEmpty();
	}

	@Override
	public boolean areAllItemsEnabled() {
		// TODO: Implement this method
		return child.areAllItemsEnabled();
	}

	@Override
	public int getCount() {
		// TODO: Implement this method
		return child.getCount();
	}

	@Override
	public boolean hasStableIds() {
		// TODO: Implement this method
		return child.hasStableIds();
	}

	@Override
	public int hashCode() {
		// TODO: Implement this method
		return child.hashCode();
	}
}
