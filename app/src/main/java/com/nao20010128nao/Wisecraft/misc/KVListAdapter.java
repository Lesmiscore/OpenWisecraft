package com.nao20010128nao.Wisecraft.misc;
import android.content.*;
import android.view.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.*;
import java.util.*;

public class KVListAdapter<K,V> extends AppBaseArrayAdapter<Map.Entry<K,V>> {
	public KVListAdapter(Context ctx) {
		super(ctx, 0, new ArrayList<Map.Entry<K,V>>());
	}
	public View getView(int pos, View v, ViewGroup ignore) {
		if (v == null)
			v = getLayoutInflater().inflate(R.layout.data, null);
		((TextView)v.findViewById(R.id.k)).setText(getItem(pos).getKey().toString());
		((TextView)v.findViewById(R.id.v)).setText(getItem(pos).getValue().toString());
		return v;
	}
	LayoutInflater getLayoutInflater() {
		return (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
}
