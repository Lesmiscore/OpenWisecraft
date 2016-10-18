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
		V old=v;
		v = nV;
		return old;
	}
	@Override
	public K getKey() {
		return k;
	}
	@Override
	public V getValue() {
		return v;
	}
}
