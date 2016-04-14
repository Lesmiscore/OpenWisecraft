package com.nao20010128nao.Wisecraft.misc;
import java.io.*;
import java.util.*;
import javax.crypto.*;

import com.nao20010128nao.Wisecraft.Utils;
import java.security.SecureRandom;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class HeavilyEncryptedBinaryPrefImpl extends BinaryPrefImpl
{
	public HeavilyEncryptedBinaryPrefImpl() {
		// TODO 自動生成されたコンストラクター・スタブ
		super();
	}

	public HeavilyEncryptedBinaryPrefImpl(Map<String, ?> map) {
		// TODO 自動生成されたコンストラクター・スタブ
		super(map);
	}

	public HeavilyEncryptedBinaryPrefImpl(File f) throws IOException {
		// TODO 自動生成されたコンストラクター・スタブ
		this(readAllFromFile(f));
	}

	public HeavilyEncryptedBinaryPrefImpl(byte[] b) throws IOException {
		// TODO 自動生成されたコンストラクター・スタブ
		this(readAllFromBytes(b));
	}

	public HeavilyEncryptedBinaryPrefImpl(InputStream is) throws IOException {
		this(is, true);
	}

	public HeavilyEncryptedBinaryPrefImpl(InputStream is, boolean close) throws IOException {
		this(readAllFromStream(is, close));
	}

	@Override
	public byte[] toBytes() {
		// TODO: Implement this method
		try {
			byte[] base= super.toBytes();
			ByteArrayOutputStream fos=new ByteArrayOutputStream();
			fos.write("".getBytes());
			OutputStream os=fos;
			SecureRandom sr = new SecureRandom();
			for(int i=0;i<40;i++){
				byte[] PREF_KEY = new byte[16];
				byte[] PREF_IV = new byte[16];
	
				sr.nextBytes(PREF_KEY);
				sr.nextBytes(PREF_IV);
				Cipher cip = Cipher.getInstance("AES/CBC/PKCS5Padding");
				cip.init(Cipher.ENCRYPT_MODE,
						 new SecretKeySpec(PREF_KEY, "aes".toUpperCase()),
						 new IvParameterSpec(PREF_IV));
				os.write(PREF_KEY);
				os.write(PREF_IV);
				os=new CipherOutputStream(os,cip);
			}
			os.write(base);
			os.close();
			return fos.toByteArray();
		} catch (Throwable e) {
			return "\0\0\0\0".getBytes();
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
			dis.readLong();dis.readLong();//header
			for(int i=0;i<40;i++){
				byte[] PREF_KEY = new byte[16];
				byte[] PREF_IV = new byte[16];
				
				Cipher cip = Cipher.getInstance("AES/CBC/PKCS5Padding");
				dis.readFully(PREF_KEY);
				dis.readFully(PREF_IV);
				cip.init(Cipher.DECRYPT_MODE,
						 new SecretKeySpec(PREF_KEY, "aes".toUpperCase()),
						 new IvParameterSpec(PREF_IV));
				dis=new DataInputStream(new ByteArrayInputStream(Utils.readAll(new CipherInputStream(dis, cip))));
			}
			
			return readAllFromStreamRaw(dis, close);
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
