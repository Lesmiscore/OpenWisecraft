package com.nao20010128nao.Wisecraft.misc;
import java.util.HashMap;
import java.lang.ref.WeakReference;
import android.view.View;
import android.view.ViewGroup;
import java.util.Map;
import java.util.Collections;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Collection;

public class PreloadedViews extends HashMap<Integer,WeakReference<View>>
{
	Map<Integer,WeakReference<View>> immutable;
	public PreloadedViews(ViewGroup views,int[] ids){
		for(int i:ids)
			super.put(i,new WeakReference<View>(views.findViewById(i)));
		immutable=Collections.unmodifiableMap(this);
	}

	@Override
	public WeakReference<View> put(Integer key, WeakReference<View> value) {
		// TODO: Implement this method
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends Integer, ? extends WeakReference<View>> map) {
		// TODO: Implement this method
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Map.Entry<Integer, WeakReference<View>>> entrySet() {
		// TODO: Implement this method
		return immutable.entrySet();
	}

	@Override
	public Set<Integer> keySet() {
		// TODO: Implement this method
		return immutable.keySet();
	}

	@Override
	public Collection<WeakReference<View>> values() {
		// TODO: Implement this method
		return immutable.values();
	}
	
	public View getView(int id){
		return get(id).get();
	}
}
