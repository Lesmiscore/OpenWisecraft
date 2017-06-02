package com.nao20010128nao.Wisecraft.misc.compat;

import android.widget.*;

import java.util.*;

public class CompatArrayAdapter
{
	public static <T> void addAll(ArrayAdapter<T> adapter,T[] array){
		for(T o:array)
			adapter.add(o);
	}
	public static <T> void addAll(ArrayAdapter<T> adapter,Collection<T> array){
		for(T o:array)
			adapter.add(o);
	}
}
