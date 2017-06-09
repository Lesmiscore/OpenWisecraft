package com.nao20010128nao.Wisecraft.misc.pinger.pc;

import com.google.gson.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import com.nao20010128nao.Wisecraft.misc.json.*;
import com.nao20010128nao.Wisecraft.misc.pinger.*;

import java.io.*;

//For ProGuarded code
public class RawJsonReply implements ServerPingResult,PCQueryResult {
	public final WisecraftJsonObject json;
	
	public RawJsonReply(String s){
		this(new JsonParser().parse(s));
	}
	
	public RawJsonReply(Reader s){
		this(new JsonParser().parse(s));
	}
	
	public RawJsonReply(JsonElement s){
		this(new GsonWJO(s));
	}
	
	public RawJsonReply(WisecraftJsonObject s){
		json=s;
	}
	
	private String raw;
	@Override
	public byte[] getRawResult() {
		return raw.getBytes(CompatCharsets.UTF_8);
	}

	@Override
	public void setRaw(String s) {
		if(raw!=null)return;
		if(s==null)return;
		raw=s;
	}
}
