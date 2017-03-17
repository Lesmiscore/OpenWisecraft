package com.nao20010128nao.Wisecraft.misc;

import java.util.*;

public class PingingServerMap extends HashMap<Server,Boolean> {
	public PingingServerMap() {
		super();
	}

	@Override
	public Boolean get(Object key) {
		Boolean b= super.get(key);
		if (b == null)
			return false;
		return b;
	}

	@Override
	public Boolean put(Server key, Boolean value) {
		if(!value)return false;
		return super.put(key, value);
	}
}

