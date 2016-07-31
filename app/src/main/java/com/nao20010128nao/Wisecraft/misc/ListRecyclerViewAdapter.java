package com.nao20010128nao.Wisecraft.misc;
import android.support.v7.widget.*;
import android.view.*;
import java.util.*;
public abstract class ListRecyclerViewAdapter<VH extends RecyclerView.ViewHolder,LType> extends RecyclerView.Adapter<VH>
{
	List<LType> list;
	public ListRecyclerViewAdapter(List<LType> lst){
		list=lst;
	}
	public LType getItem(int ofs){
		return list.get(ofs);
	}


	public void add(LType object) {
		// TODO: Implement this method
		list.add(object);
		notifyItemInserted(getItemCount());
	}

	public void addAll(LType[] items) {
		// TODO: Implement this method
		for (LType s:items)add(s);
	}

	public void addAll(Collection<? extends LType> collection) {
		// TODO: Implement this method
		for (LType s:collection)add(s);
	}

	public void remove(Server object) {
		// TODO: Implement this method
		int ofs=list.indexOf(object);
		list.remove(object);
		notifyItemRemoved(ofs);
	}

	@Override
	public int getItemCount() {
		// TODO: Implement this method
		return list.size();
	}

	@Override
	public abstract void onBindViewHolder(VH parent, int offset);

	@Override
	public abstract VH onCreateViewHolder(ViewGroup parent, int type);
}
