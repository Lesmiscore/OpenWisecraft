package com.nao20010128nao.Wisecraft.misc;

import android.support.v7.widget.*;
import android.view.*;

public class FindableViewHolder extends RecyclerView.ViewHolder {
    public FindableViewHolder(View v) {
        super(v);
    }

    public View findViewById(int resId) {
        return itemView.findViewById(resId);
    }

    public <T extends View> T findTypedViewById(int resId) {
        return (T) findViewById(resId);
    }

    public <T extends View> T find(int resId) {
        return findTypedViewById(resId);
    }
}
