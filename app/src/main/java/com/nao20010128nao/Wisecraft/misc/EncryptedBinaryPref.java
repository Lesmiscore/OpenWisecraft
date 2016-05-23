package com.nao20010128nao.Wisecraft.misc;
import java.io.*;
import java.security.*;
import java.util.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public class EncryptedBinaryPref extends BinaryPrefImpl
{
	private static final byte[] PREF_KEY = new byte[16];
	private static final byte[] PREF_IV = new byte[16];
	
	public EncryptedBinaryPref() {
		// TODO 自動生成されたコンストラクター・スタブ
		super();
	}

	public EncryptedBinaryPref(Map<String, ?> map) {
		// TODO 自動生成されたコンストラクター・スタブ
		super(map);
	}

	public EncryptedBinaryPref(File f) throws IOException {
		// TODO 自動生成されたコンストラクター・スタブ
		this(readAllFromFile(f));
	}

	public EncryptedBinaryPref(byte[] b) throws IOException {
		// TODO 自動生成されたコンストラクター・スタブ
		this(readAllFromBytes(b));
	}

	public EncryptedBinaryPref(InputStream is) throws IOException {
		this(is, true);
	}

	public EncryptedBinaryPref(InputStream is, boolean close) throws IOException {
		this(readAllFromStream(is, close));
	}

	@Override
	public byte[] toBytes() {
		// TODO: Implement this method
		try {
			byte[] base= super.toBytes();
			ByteArrayOutputStream fos=new ByteArrayOutputStream();
			SecureRandom sr = new SecureRandom();
			sr.nextBytes(PREF_KEY);
			sr.nextBytes(PREF_IV);
			Cipher cip = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cip.init(Cipher.ENCRYPT_MODE,
					 new SecretKeySpec(PREF_KEY, "aes".toUpperCase()),
					 new IvParameterSpec(PREF_IV));
			fos.write(PREF_KEY);
			fos.write(PREF_IV);
			byte[] buf = cip.doFinal(base);
			fos.write(buf);
			
			return fos.toByteArray();
		} catch (Throwable e) {
			return new byte[4];
		}
	}
	
	private static Map<String, ?> readAllFromFile(File f) throws IOException {
		if (!f.exists()) {
			return Collections.emptyMap();
		}
		return readAllFromStream(new FileInputStream(f), true);
	}

	private static Map<String, ?> readAllFromStream(InputStream fis,
													boolean close) throws IOException {
		try {
			DataInputStream dis = new DataInputStream(fis);
			Cipher cip = Cipher.getInstance("AES/CBC/PKCS5Padding");
			dis.readFully(PREF_KEY);
			dis.readFully(PREF_IV);
			cip.init(Cipher.DECRYPT_MODE,
					 new SecretKeySpec(PREF_KEY, "aes".toUpperCase()),
					 new IvParameterSpec(PREF_IV));
			return readAllFromStreamRaw(new CipherInputStream(dis, cip), close);
		} catch (Throwable e) {
			throw new IOException(e);
		} finally {
			if(fis!=null&close)fis.close();
		}
	}

	private static Map<String, ?> readAllFromStreamRaw(InputStream is,
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
						for (int j = 0; j < sLen; j++) {
							set.add(dis.readUTF());
						}
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
			if (dis != null) {
				if (close)
					dis.close();
			}
		}
		return map;
	}

	private static Map<String, ?> readAllFromBytes(byte[] array) {
		try {
			return readAllFromStream(new ByteArrayInputStream(array), true);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			return null;
		}
	}
}
