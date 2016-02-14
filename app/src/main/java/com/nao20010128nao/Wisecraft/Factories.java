package com.nao20010128nao.Wisecraft;
import java.util.*;

public class Factories {
	public static String[] strArray(int size) {
		if (size == 0)return Constant.EMPTY_STRING_ARRAY;
		return new String[size];
	}
	public static String[] strArray(Collection<String> col) {
		return col.toArray(strArray(col.size()));
	}
	public static byte[] byteArray(int size) {
		return new byte[size];
	}
	public static <T> ArrayList<T> arrayList(){
		return new ArrayList<T>();
	}
	public static <T> ArrayList<T> arrayList(List<T> list){
		return new ArrayList<T>(list);
	}
}
