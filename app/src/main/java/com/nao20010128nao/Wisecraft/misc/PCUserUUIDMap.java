package com.nao20010128nao.Wisecraft.misc;
import java.util.HashMap;

public class PCUserUUIDMap extends HashMap<String,String>
{
	private final String ALLOWED_CHARACTERS="abcdefghijklmnopqrstuvwxyzABCDEGHIJKLMNOPQRSTUVWXYZ_";
	@Override
	public String put(String key, String value) {
		// TODO: Implement this method
		if(!allowedAsUsername(key))
			return null;
		return super.put(key, value);
	}
	private boolean allowedAsUsername(String s){
		if(s.length()>15)return false;
		for(char c:s.toCharArray())
			if(!ALLOWED_CHARACTERS.contains(String.valueOf(c)))
				return false;
		return true;
	}
}
