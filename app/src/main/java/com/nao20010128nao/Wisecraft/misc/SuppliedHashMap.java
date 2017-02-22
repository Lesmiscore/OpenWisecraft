package com.nao20010128nao.Wisecraft.misc;

import java.util.*;
import java.lang.reflect.*;

public class SuppliedHashMap<K,V> extends HashMap<K,V> 
{
	final Supplier<V> creator;
	final boolean putWhenNotKnown;
	public SuppliedHashMap(Supplier<V> creator){
		this(creator,true);
	}

	public SuppliedHashMap(Supplier<V> creator,boolean putWhenNotKnown){
		this.creator=creator;
		this.putWhenNotKnown=putWhenNotKnown;
	}
	
	@Override
	public V get(Object key) {
		if(creator!=null & !containsKey(key)){
			V v=creator.supply();
			if(putWhenNotKnown){
				put((K)key,v);
			}
			return v;
		}
		return super.get(key);
	}
	
	public static <K,V> SuppliedHashMap<K,V> fromConstant(final V value,boolean putWhenNotKnown){
		return new SuppliedHashMap<K,V>(new Supplier<V>(){
				public V supply(){
					return value;
				}
			},putWhenNotKnown);
	}
	public static <K,V> SuppliedHashMap<K,V> fromClass(final Class<? extends V> clazz,boolean putWhenNotKnown){
		return new SuppliedHashMap<K,V>(new Supplier<V>(){
				public V supply(){
					try {
						return clazz.newInstance();
					} catch (InstantiationException e) {
						
					} catch (IllegalAccessException e) {
						
					}
					return null;
				}
			},putWhenNotKnown);
	}
}
