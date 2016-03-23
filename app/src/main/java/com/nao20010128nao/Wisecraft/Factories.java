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
	public static class FreeSizeNullLenList<T> implements List<T> {
		int length;
		private FreeSizeNullLenList(int length){
			this.length=length;
		}
		
		@Override
		public List<T> subList(int p1, int p2) {
			// TODO: Implement this method
			return null;
		}

		@Override
		public int indexOf(Object p1) {
			// TODO: Implement this method
			return 0;
		}

		@Override
		public void add(int p1, T p2) {
			// TODO: Implement this method
		}

		@Override
		public T set(int p1, T p2) {
			// TODO: Implement this method
			return null;
		}

		@Override
		public boolean add(T p1) {
			// TODO: Implement this method
			return false;
		}

		@Override
		public Iterator<T> iterator() {
			// TODO: Implement this method
			return null;
		}

		@Override
		public <T extends Object> T[] toArray(T[] p1) {
			// TODO: Implement this method
			return null;
		}

		@Override
		public T remove(int p1) {
			// TODO: Implement this method
			return null;
		}

		@Override
		public int size() {
			// TODO: Implement this method
			return 0;
		}

		@Override
		public boolean remove(Object p1) {
			// TODO: Implement this method
			return false;
		}

		@Override
		public boolean containsAll(Collection<?> p1) {
			// TODO: Implement this method
			return false;
		}

		@Override
		public Object[] toArray() {
			// TODO: Implement this method
			return null;
		}

		@Override
		public boolean removeAll(Collection<?> p1) {
			// TODO: Implement this method
			return false;
		}

		@Override
		public boolean addAll(int p1, Collection<? extends T> p2) {
			// TODO: Implement this method
			return false;
		}

		@Override
		public boolean addAll(Collection<? extends T> p1) {
			// TODO: Implement this method
			return false;
		}

		@Override
		public void clear() {
			// TODO: Implement this method
		}

		@Override
		public boolean retainAll(Collection<?> p1) {
			// TODO: Implement this method
			return false;
		}

		@Override
		public int lastIndexOf(Object p1) {
			// TODO: Implement this method
			return 0;
		}

		@Override
		public boolean isEmpty() {
			// TODO: Implement this method
			return false;
		}

		@Override
		public ListIterator<T> listIterator() {
			// TODO: Implement this method
			return null;
		}

		@Override
		public ListIterator<T> listIterator(int p1) {
			// TODO: Implement this method
			return null;
		}

		@Override
		public T get(int p1) {
			// TODO: Implement this method
			return null;
		}

		@Override
		public boolean contains(Object p1) {
			// TODO: Implement this method
			return false;
		}
	}
}
