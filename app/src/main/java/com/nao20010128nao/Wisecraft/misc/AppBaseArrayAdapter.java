package com.nao20010128nao.Wisecraft.misc;
import android.content.Context;
import android.widget.ArrayAdapter;
import java.util.Collection;
import java.util.List;

public class AppBaseArrayAdapter<T> extends ArrayAdapter<T> {
	public AppBaseArrayAdapter(Context context, int resource) {
		super(context, resource);
	}

    public AppBaseArrayAdapter(Context context, int resource, int textViewResourceId) {
		super(context, resource, textViewResourceId);
	}

    public AppBaseArrayAdapter(Context context, int resource, T[] objects) {
		super(context, resource, objects);
	}

    public AppBaseArrayAdapter(Context context, int resource, int textViewResourceId, T[] objects) {
		super(context, resource, textViewResourceId, objects);
	}

    public AppBaseArrayAdapter(Context context, int resource, List<T> objects) {
		super(context, resource, objects);
	}

    public AppBaseArrayAdapter(Context context, int resource, int textViewResourceId, List<T> objects) {
		super(context, resource, textViewResourceId, objects);
	}

	public void addAll(Collection<? extends T> collection) {
		// TODO: Implement this method
		for (T t:collection)
			add(t);
	}

	public void addAll(T[] items) {
		// TODO: Implement this method
		for (T t:items)
			add(t);
	}
}
