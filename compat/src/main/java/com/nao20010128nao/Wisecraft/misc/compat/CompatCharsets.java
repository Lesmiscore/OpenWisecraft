package com.nao20010128nao.Wisecraft.misc.compat;
import java.nio.charset.*;

public class CompatCharsets
{
	public static final Charset UTF_8,US_ASCII;
	static{
		Class charsets;
		try {
			charsets=Class.forName("java.nio.charset.StandardCharsets");
		} catch (ClassNotFoundException e) {
			charsets=null;
		}

		Charset utf8=null,usAscii=null;

		if(charsets!=null){
			try {
				utf8=(Charset)charsets.getField("UTF_8").get(null);
			} catch (Throwable e) {/*ignore*/}
			try {
				usAscii=(Charset)charsets.getField("US_ASCII").get(null);
			} catch (Throwable e) {/*ignore*/}
		}

		if(utf8==null)utf8=Charset.forName("UTF-8");
		if(usAscii==null)usAscii=Charset.forName("US-ASCII");

		UTF_8=utf8;
		US_ASCII=usAscii;
	}
}
