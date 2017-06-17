package com.nao20010128nao.Wisecraft.misc;

import android.content.*;
import android.view.*;
import android.widget.*;

public class SimpleRecyclerAdapter<T> extends ListRecyclerViewAdapter<FindableViewHolder, T> {
    Context ctx;

    public SimpleRecyclerAdapter(Context c) {
        ctx = c;
    }

    @Override
    public FindableViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        return new VH(LayoutInflater.from(ctx).inflate(android.R.layout.simple_list_item_1, parent, false));
    }

    @Override
    public void onBindViewHolder(FindableViewHolder parent, int offset) {
        ((TextView) parent.findViewById(android.R.id.text1)).setText(getItem(offset).toString());
    }

    public class VH extends FindableViewHolder {
        public VH(View w) {
            super(w);
        }
    }
}
