package com.nao20010128nao.Wisecraft.misc;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.text.*;
import android.text.style.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import com.nao20010128nao.Wisecraft.misc.rcon.*;
import com.nao20010128nao.Wisecraft.rcon.*;
import java.io.*;
import java.lang.reflect.*;
import java.security.*;
import java.util.*;

public class RconModule_Utils{
	public static String deleteDecorations(String decorated) {
		StringBuilder sb=new StringBuilder();
		char[] chars=decorated.toCharArray();
		int offset=0;
		while (chars.length > offset) {
			if (chars[offset] == '§') {
				offset += 2;
				continue;
			}
			sb.append(chars[offset]);
			offset++;
		}
		return sb.toString();
	}
	public static boolean isNullString(String s) {
		if (s == null) 
			return true;
		if ("".equals(s)) 
			return true;
		return false;
	}
	public static String[] lines(String s) {
		try {
			BufferedReader br=new BufferedReader(new StringReader(s));
			List<String> tmp=new ArrayList<>(4);
			String line=null;
			while (null != (line = br.readLine()))tmp.add(line);
			return tmp.toArray(new String[tmp.size()]);
		} catch (Throwable e) {
			return RconModule_Constant.EMPTY_STRING_ARRAY;
		}
	}
	
	public static <T> T requireNonNull(T obj) {
		if (obj == null)
			throw new NullPointerException();
		return obj;
	}
	public static String randomText() {
		return randomText(16);
	}
	public static String randomText(int len) {
		StringBuilder sb=new StringBuilder(len*2);
		byte[] buf=new byte[len];
		new SecureRandom().nextBytes(buf);
		for (byte b:buf)
			sb.append(Character.forDigit(b >> 4 & 0xF, 16)).append(Character.forDigit(b & 0xF, 16));
		return sb.toString();
	}	
	public static byte[] readAll(InputStream is)throws IOException{
		ByteArrayOutputStream os=new ByteArrayOutputStream(1000);
		byte[] buf=new byte[1000];
		try {
			while (true) {
				int r=is.read(buf);
				if (r <= 0)
					break;
				os.write(buf, 0, r);
			}
		} finally {
			is.close();
		}
		return os.toByteArray();
	}
	public <T> List<T>  trueValues(List<T> all, boolean[] balues) {
		List<T> lst=new ArrayList<T>();
		for (int i=0;i < balues.length;i++)
			if (balues[i])
				lst.add(all.get(i));
		return lst;
	}
	public <T> T[] trueValues(T[] all, boolean[] balues) {
		List<T> lst=new ArrayList<T>();
		for (int i=0;i < balues.length;i++)
			if (balues[i])
				lst.add(all[i]);
		return lst.toArray((T[])Array.newInstance(all.getClass().getComponentType(),lst.size()));
	}
}
