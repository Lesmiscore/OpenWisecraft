package com.nao20010128nao.Wisecraft.misc;
import android.support.v7.widget.*;
import android.view.*;
import java.util.*;
public abstract class ListRecyclerViewAdapter<VH extends RecyclerView.ViewHolder,LType> extends RecyclerView.Adapter<VH> implements List<LType>
{
	List<LType> list;
	List<LType> unmodifiableList;
	public ListRecyclerViewAdapter(List<LType> lst){
		unmodifiableList=Collections.unmodifiableList(list=new ArrayList<LType>(lst));
	}
	public ListRecyclerViewAdapter(){
		this(new ArrayList<LType>());
	}
	public LType getItem(int ofs){
		return list.get(ofs);
	}


	public boolean add(LType object) {
		boolean b=list.add(object);
		notifyItemInserted(getItemCount());
		return b;
	}

	public void addAll(LType[] items) {
		for (LType s:items)add(s);
	}

	public boolean addAll(Collection<? extends LType> collection) {
		boolean b=true;
		for (LType s:collection)b&=add(s);
		return b;
	}

	public boolean remove(Object object) {
		int ofs=list.indexOf(object);
		boolean b=list.remove(object);
		notifyItemRemoved(ofs);
		return b;
	}

	@Override
	public int getItemCount() {
		return list.size();
	}

	@Override
	public abstract void onBindViewHolder(VH parent, int offset);

	@Override
	public abstract VH onCreateViewHolder(ViewGroup parent, int type);

	@Override
	public int lastIndexOf(Object p1) {
		return list.lastIndexOf(p1);
	}

	@Override
	public List<LType> subList(int p1, int p2) {
		return list.subList(p1,p2);
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public boolean addAll(int p1, Collection<? extends LType> p2) {
		boolean b=list.addAll(p1,p2);
		notifyItemRangeInserted(p1,p2.size());
		return b;
	}

	@Override
	public ListIterator<LType> listIterator(int p1) {
		return unmodifiableList.listIterator(p1);
	}

	@Override
	public ListIterator<LType> listIterator() {
		return unmodifiableList.listIterator();
	}

	@Override
	public boolean removeAll(Collection<?> p1) {
		for (Object s:p1)remove(s);
		return true;
	}

	@Override
	public boolean retainAll(Collection<?> p1) {
		return false;
	}

	@Override
	public int size() {
		return getItemCount();
	}

	@Override
	public int indexOf(Object p1) {
		return list.indexOf(p1);
	}

	@Override
	public void add(int p1, LType p2) {
		list.add(p1,p2);
		notifyItemRangeInserted(p1,1);
	}

	@Override
	public boolean containsAll(Collection<?> p1) {
		return list.containsAll(p1);
	}

	@Override
	public LType remove(int p1) {
		LType b=list.remove(p1);
		notifyItemRemoved(p1);
		return b;
	}

	@Override
	public boolean contains(Object p1) {
		return list.contains(p1);
	}

	@Override
	public LType get(int p1) {
		return getItem(p1);
	}

	@Override
	public Iterator<LType> iterator() {
		return unmodifiableList.iterator();
	}

	@Override
	public <T extends Object> T[] toArray(T[] p1) {
		return list.toArray(p1);
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public LType set(int p1, LType p2) {
		LType l=list.set(p1,p2);
		notifyItemChanged(p1);
		return l;
	}

	@Override
	public void clear() {
		int siz=size();
		list.clear();
		notifyItemRangeRemoved(0,siz);
	}
}
