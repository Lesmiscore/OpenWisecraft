package com.nao20010128nao.Wisecraft.misc.compat;
import java.nio.charset.Charset;

public class CompatCharsets
{
	public static final Charset UTF_8,US_ASCII;
	static{
		try{
			UTF_8=(Charset)Class.forName("java.nio.charset.StandardCharsets").getField("UTF_8").get(null);
		}catch(Throwable e){
			UTF_8=Charset.forName("UTF-8");
		}
		try{
			US_ASCII=(Charset)Class.forName("java.nio.charset.StandardCharsets").getField("US_ASCII").get(null);
		}catch(Throwable e){
			US_ASCII=Charset.forName("US-ASCII");
		}
	}
}
