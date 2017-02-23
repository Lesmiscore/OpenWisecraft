package com.nao20010128nao.Wisecraft.misc;

import java.util.*;
import java.lang.reflect.*;

public class SuppliedHashMap<K,V> extends HashMap<K,V> 
{
	final Supplier<K,V> creator;
	final boolean putWhenNotKnown;
	public SuppliedHashMap(Supplier<K,V> creator){
		this(creator,true);
	}

	public SuppliedHashMap(Supplier<K,V> creator,boolean putWhenNotKnown){
		this.creator=creator;
		this.putWhenNotKnown=putWhenNotKnown;
	}
	
	@Override
	public V get(Object key) {
		if(creator!=null & !containsKey(key)){
			V v=creator.supply((K)key);
			if(putWhenNotKnown){
				put((K)key,v);
			}
			return v;
		}
		return super.get(key);
	}
	
	public static <K,V> SuppliedHashMap<K,V> fromConstant(final V value,boolean putWhenNotKnown){
		return new SuppliedHashMap<K,V>(new Supplier<K,V>(){
				public V supply(K a){
					return value;
				}
			},putWhenNotKnown);
	}
	public static <K,V> SuppliedHashMap<K,V> fromClass(final Class<? extends V> clazz,boolean putWhenNotKnown){
		return new SuppliedHashMap<K,V>(new Supplier<K,V>(){
				public V supply(K a){
					try {
						return clazz.newInstance();
					} catch (InstantiationException e) {
						
					} catch (IllegalAccessException e) {
						
					}
					return null;
				}
			},putWhenNotKnown);
	}
	public static <K,V> SuppliedHashMap<K,V> fromClass(final Class<? extends V> clazz,final Class<K> klazz,boolean putWhenNotKnown){
		return new SuppliedHashMap<K,V>(new Supplier<K,V>(){
				public V supply(K a){
					try {
						return clazz.newInstance();
					} catch (InstantiationException e) {

					} catch (IllegalAccessException e) {

					}
					try {
						Constructor c=clazz.getDeclaredConstructor(klazz);
						return (V)c.newInstance(a);
					} catch (NoSuchMethodException e) {
						
					} catch (InstantiationException e) {
						
					} catch (InvocationTargetException e) {
						
					} catch (SecurityException e) {
						
					} catch (IllegalAccessException e) {
						
					} catch (IllegalArgumentException e) {
						
					}
					return null;
				}
			},putWhenNotKnown);
	}
}
