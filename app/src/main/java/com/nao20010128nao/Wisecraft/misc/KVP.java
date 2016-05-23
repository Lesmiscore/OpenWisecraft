package com.nao20010128nao.Wisecraft.misc;
import java.util.*;
public class KVP<K,V> implements Map.Entry<K,V> {
	K k;
	V v;
	public KVP(K k, V v) {
		this.k = k;
		this.v = v;
	}
	@Override
	public V setValue(V nV) {
		// TODO: Implement this method
		V old=v;
		v = nV;
		return old;
	}
	@Override
	public K getKey() {
		// TODO: Implement this method
		return k;
	}
	@Override
	public V getValue() {
		// TODO: Implement this method
		return v;
	}
}
