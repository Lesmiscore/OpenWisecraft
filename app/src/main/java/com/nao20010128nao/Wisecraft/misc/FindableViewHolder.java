package com.nao20010128nao.Wisecraft.misc;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class FindableViewHolder extends RecyclerView.ViewHolder
{
	public FindableViewHolder(View v){
		super(v);
	}
	public View findViewById(int resId) {
		return itemView.findViewById(resId);
	}
}
