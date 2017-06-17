package com.nao20010128nao.Wisecraft.misc;

import android.view.*;

import java.lang.ref.*;
import java.util.*;

public class PreloadedViews extends HashMap<Integer, WeakReference<View>> {
    Map<Integer, WeakReference<View>> immutable;

    public PreloadedViews(ViewGroup views, int[] ids) {
        for (int i : ids)
            super.put(i, new WeakReference<>(views.findViewById(i)));
        immutable = Collections.unmodifiableMap(this);
    }

    @Override
    public WeakReference<View> put(Integer key, WeakReference<View> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends WeakReference<View>> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Map.Entry<Integer, WeakReference<View>>> entrySet() {
        return immutable.entrySet();
    }

    @Override
    public Set<Integer> keySet() {
        return immutable.keySet();
    }

    @Override
    public Collection<WeakReference<View>> values() {
        return immutable.values();
    }

    public View getView(int id) {
        return get(id).get();
    }
}
