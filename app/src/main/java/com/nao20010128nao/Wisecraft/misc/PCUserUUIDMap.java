package com.nao20010128nao.Wisecraft.misc;
import java.util.*;
import java.util.regex.Pattern;

public class PCUserUUIDMap extends HashMap<String,String>
{
	private final Pattern MC_USERNAME=Pattern.compile("[a-zA-Z_]{2,15}");
	@Override
	public String put(String key, String value) {
		if(!allowedAsUsername(key))
			return null;
		return super.put(key, value);
	}
	private boolean allowedAsUsername(String s){
		return MC_USERNAME.matcher(s).matches();
	}
}
