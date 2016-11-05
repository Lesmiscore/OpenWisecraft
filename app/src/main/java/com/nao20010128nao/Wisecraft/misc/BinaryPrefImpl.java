package com.nao20010128nao.Wisecraft.misc;

import android.content.*;
import com.nao20010128nao.OTC.*;
import java.io.*;
import java.util.*;

public class BinaryPrefImpl implements SharedPreferences {
	protected Map<String, Object> data, allCache;
	boolean unchanged = false;

	public BinaryPrefImpl() {
		this(new OrderTrustedMap<String,Object>());
	}

	public BinaryPrefImpl(Map<String, ?> map) {
		data = validateMap(new HashMap<>(map));
	}

	public BinaryPrefImpl(File f) throws IOException {
		this(readAllFromFile(f));
	}

	public BinaryPrefImpl(byte[] b) throws IOException {
		this(readAllFromBytes(b));
	}

	public BinaryPrefImpl(InputStream is) throws IOException {
		this(is, true);
	}

	public BinaryPrefImpl(InputStream is, boolean close) throws IOException {
		this(readAllFromStream(is, close));
	}

	@Override
	public Map<String, ?> getAll() {
		return Collections.unmodifiableMap(new HashMap<>(data));
	}

	@Override
	public String getString(String key, String defValue) {
		Object o = data.get(key);
		if (!(o instanceof String))
			return defValue;
		return (String) o;
	}

	@Override
	public Set<String> getStringSet(String key, Set<String> defValues) {
		Object o = data.get(key);
		if (!(o instanceof Set<?>))
			return defValues;
		return (Set<String>) o;
	}

	@Override
	public int getInt(String key, int defValue) {
		Object o = data.get(key);
		if (!(o instanceof Integer))
			return defValue;
		return (Integer) o;
	}

	@Override
	public long getLong(String key, long defValue) {
		Object o = data.get(key);
		if (!(o instanceof Long))
			return defValue;
		return (Long) o;
	}

	@Override
	public float getFloat(String key, float defValue) {
		Object o = data.get(key);
		if (!(o instanceof Float))
			return defValue;
		return (Float) o;
	}

	@Override
	public boolean getBoolean(String key, boolean defValue) {
		Object o = data.get(key);
		if (!(o instanceof Boolean))
			return defValue;
		return (Boolean) o;
	}

	@Override
	public boolean contains(String key) {
		return data.containsKey(key);
	}

	@Override
	public Editor edit() {
		return new BPIEdt();
	}

	@Override
	public void registerOnSharedPreferenceChangeListener(
		OnSharedPreferenceChangeListener listener) {
		// Do nothing here
	}

	@Override
	public void unregisterOnSharedPreferenceChangeListener(
		OnSharedPreferenceChangeListener listener) {
		// Do nothing here
	}

	public byte[] toBytes() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			dos.writeInt(data.size());
			for (Map.Entry<String,Object> ent:data.entrySet()) {
				try {
					dos.writeUTF(ent.getKey());
					Object o = ent.getValue();
					if (o instanceof String) {
						dos.writeByte(0);
						dos.writeUTF((String) o);
					} else if (o instanceof Set<?>) {
						dos.writeByte(1);
						dos.writeInt(((Set) o).size());
						for (String s:(Set<String>)o)
							dos.writeUTF(s);
					} else if (o instanceof Integer) {
						dos.writeByte(2);
						dos.writeInt((int) o);
					} else if (o instanceof Long) {
						dos.writeByte(3);
						dos.writeLong((long) o);
					} else if (o instanceof Float) {
						dos.writeByte(4);
						dos.writeFloat((float) o);
					} else if (o instanceof Boolean) {
						dos.writeByte(5);
						dos.writeBoolean((boolean) o);
					}
				} catch (Exception e) {
					// Unreachable
				}
			}

			return baos.toByteArray();
		} catch (IOException e) {
			DebugWriter.writeToE("BinaryPrefImpl", e);
			return new byte[4];
		}
	}

	private class BPIEdt implements Editor {
		Map<String, Object> data = new HashMap<>();
		Set<String> removes=new HashSet<>();

		@Override
		public Editor putString(String key, String value) {
			data.put(key, value);
			removes.remove(key);
			return this;
		}

		@Override
		public Editor putStringSet(String key, Set<String> values) {
			data.put(key, Collections.unmodifiableSet(new HashSet<>(values)));
			removes.remove(key);
			return this;
		}

		@Override
		public Editor putInt(String key, int value) {
			data.put(key, value);
			removes.remove(key);
			return this;
		}

		@Override
		public Editor putLong(String key, long value) {
			data.put(key, value);
			removes.remove(key);
			return this;
		}

		@Override
		public Editor putFloat(String key, float value) {
			data.put(key, value);
			removes.remove(key);
			return this;
		}

		@Override
		public Editor putBoolean(String key, boolean value) {
			data.put(key, value);
			removes.remove(key);
			return this;
		}

		@Override
		public Editor remove(String key) {
			data.remove(key);
			removes.add(key);
			return this;
		}

		@Override
		public Editor clear() {
			data.clear();
			removes.addAll(BinaryPrefImpl.this.data.keySet());
			return this;
		}

		@Override
		public boolean commit() {
			apply();
			return true;
		}

		@Override
		public void apply() {
			BinaryPrefImpl.this.data.putAll(data);
			for (String k:removes)BinaryPrefImpl.this.data.remove(k);
			data = null;
		}
	}

	private static Map<String, ?> readAllFromFile(File f) throws IOException {
		if (!f.exists()) {
			return Collections.emptyMap();
		}
		return readAllFromStream(new FileInputStream(f), true);
	}

	private static Map<String, ?> readAllFromStream(InputStream is,
													boolean close) throws IOException {
		Map<String, Object> map = new HashMap<>(10);
		DataInputStream dis = null;
		try {
			dis = new DataInputStream(is);
			int len = dis.readInt();
			for (int i = 0; i < len; i++) {
				String key = dis.readUTF();
				byte mode = dis.readByte();
				switch (mode) {
					case 0:// String
						map.put(key, dis.readUTF());
						break;
					case 1:// String Set
						int sLen = dis.readInt();
						Set<String> set = new HashSet<>(sLen);
						for (int j = 0; j < sLen; j++)
							set.add(dis.readUTF());
						map.put(key, Collections.unmodifiableSet(set));
						break;
					case 2:// int
						map.put(key, dis.readInt());
						break;
					case 3:// long
						map.put(key, dis.readLong());
						break;
					case 4:// float
						map.put(key, dis.readFloat());
						break;
					case 5:// boolean
						map.put(key, dis.readBoolean());
						break;
				}
			}
		} finally {
			if (dis != null&close)
				dis.close();
		}
		return map;
	}

	private static Map<String, ?> readAllFromBytes(byte[] array) {
		try {
			return readAllFromStream(new ByteArrayInputStream(array), true);
		} catch (IOException e) {
			return null;
		}
	}

	private static HashMap<String,Object> validateMap(Map<String,Object> map) {
		HashMap<String,Object> result=new HashMap<>();
		for (Map.Entry<String,Object> ent:map.entrySet()) {
			Object o = ent.getValue();
			if (o instanceof String) {
				result.put(ent.getKey(), o);
			} else if (o instanceof Set<?>) {
				result.put(ent.getKey(), Collections.unmodifiableSet(new HashSet<String>((Set<String>)o)));
			} else if (o instanceof Integer) {
				result.put(ent.getKey(), o);
			} else if (o instanceof Long) {
				result.put(ent.getKey(), o);
			} else if (o instanceof Float) {
				result.put(ent.getKey(), o);
			} else if (o instanceof Boolean) {
				result.put(ent.getKey(), o);
			}
		}
		return result;
	}
}
