package com.nao20010128nao.Wisecraft.misc;
import android.support.v7.widget.*;
import android.view.*;
import java.util.*;
public abstract class ListRecyclerViewAdapter<VH extends RecyclerView.ViewHolder,LType> extends RecyclerView.Adapter<VH> implements List<LType>
{
	List<LType> list;
	public ListRecyclerViewAdapter(List<LType> lst){
		list=lst;
	}
	public LType getItem(int ofs){
		return list.get(ofs);
	}


	public boolean add(LType object) {
		// TODO: Implement this method
		boolean b=list.add(object);
		notifyItemInserted(getItemCount());
		return b;
	}

	public void addAll(LType[] items) {
		// TODO: Implement this method
		for (LType s:items)add(s);
	}

	public boolean addAll(Collection<? extends LType> collection) {
		// TODO: Implement this method
		boolean b=true;
		for (LType s:collection)b&=add(s);
		return b;
	}

	public boolean remove(Object object) {
		// TODO: Implement this method
		int ofs=list.indexOf(object);
		boolean b=list.remove(object);
		notifyItemRemoved(ofs);
		return b;
	}

	public void remove(LType e){
		remove((Object)e);
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

	@Override
	public int lastIndexOf(Object p1) {
		// TODO: Implement this method
		return list.lastIndexOf(p1);
	}

	@Override
	public List<LType> subList(int p1, int p2) {
		// TODO: Implement this method
		return list.subList(p1,p2);
	}

	@Override
	public boolean isEmpty() {
		// TODO: Implement this method
		return list.isEmpty();
	}

	@Override
	public boolean addAll(int p1, Collection<? extends LType> p2) {
		// TODO: Implement this method
		boolean b=list.addAll(p1,p2);
		notifyItemRangeInserted(p1,p2.size());
		return b;
	}

	@Override
	public ListIterator<LType> listIterator(int p1) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public ListIterator<LType> listIterator() {
		// TODO: Implement this method
		return null;
	}

	@Override
	public boolean removeAll(Collection<?> p1) {
		// TODO: Implement this method
		for (Object s:p1)remove(s);
		return true;
	}

	@Override
	public boolean retainAll(Collection<?> p1) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public int size() {
		// TODO: Implement this method
		return getItemCount();
	}

	@Override
	public int indexOf(Object p1) {
		// TODO: Implement this method
		return list.indexOf(p1);
	}

	@Override
	public void add(int p1, LType p2) {
		// TODO: Implement this method
		list.add(p1,p2);
		notifyItemRangeInserted(p1,1);
	}

	@Override
	public boolean containsAll(Collection<?> p1) {
		// TODO: Implement this method
		return list.containsAll(p1);
	}

	@Override
	public LType remove(int p1) {
		// TODO: Implement this method
		LType b=list.remove(p1);
		notifyItemRemoved(p1);
		return b;
	}

	@Override
	public boolean contains(Object p1) {
		// TODO: Implement this method
		return list.contains(p1);
	}

	@Override
	public LType get(int p1) {
		// TODO: Implement this method
		return getItem(p1);
	}

	@Override
	public Iterator<LType> iterator() {
		// TODO: Implement this method
		return null;
	}

	@Override
	public <T extends Object> T[] toArray(T[] p1) {
		// TODO: Implement this method
		return list.toArray(p1);
	}

	@Override
	public Object[] toArray() {
		// TODO: Implement this method
		return list.toArray();
	}

	@Override
	public LType set(int p1, LType p2) {
		// TODO: Implement this method
		LType l=list.set(p1,p2);
		notifyItemChanged(p1);
		return l;
	}

	@Override
	public void clear() {
		// TODO: Implement this method
		int siz=size();
		list.clear();
		notifyItemRangeRemoved(0,siz);
	}
}
