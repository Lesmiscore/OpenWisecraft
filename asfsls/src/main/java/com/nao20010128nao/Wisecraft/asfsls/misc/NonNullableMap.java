package com.nao20010128nao.Wisecraft.asfsls.misc;

import java.util.HashMap;
import java.util.Map;

public class NonNullableMap<K> extends HashMap<K, Boolean> {
    public NonNullableMap() {
        super();
    }

    public NonNullableMap(int capacity) {
        super(capacity);
    }

    public NonNullableMap(int capacity, float loadFactor) {
        super(capacity, loadFactor);
    }

    public NonNullableMap(Map<K, Boolean> map) {
        super(map);
    }

    @Override
    public Boolean get(Object key) {
        Boolean b = super.get(key);
        if (b == null)
            return false;
        return b;
    }
}
