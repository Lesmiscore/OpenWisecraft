package com.nao20010128nao.Wisecraft.misc.pinger.pc;

import com.google.gson.annotations.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import com.nao20010128nao.Wisecraft.misc.pinger.*;

import java.util.*;

/**
 * References: http://wiki.vg/Server_List_Ping
 * https://gist.github.com/thinkofdeath/6927216
 */

public class Reply implements ServerPingResult,PCQueryResult {
	@SerializedName("description")
	public String description;
	@SerializedName("players")
	public Players players;
	@SerializedName("version")
	public Version version;
	@SerializedName("favicon")
	public String favicon;
	@SerializedName("modinfo")
	public ModInfo modinfo;
	
	private String raw;
	
	
	public static class Players{
		@SerializedName("max")
		public int max;
		@SerializedName("online")
		public int online;
		@SerializedName("sample")
		public List<Player> sample;
	}

	
	public static class Player{
		@SerializedName("name")
		public String name;
		@SerializedName("id")
		public String id;
	}

	
	public static class Version{
		@SerializedName("name")
		public String name;
		@SerializedName("protocol")
		public int protocol;
	}
	
	
	public static class ModInfo{
		@SerializedName("type")
		public String type;
		@SerializedName("modList")
		public ModListContent[] modList;
	}
	
	
	public static class ModListContent{
		@SerializedName("modid")
		public String modid;
		@SerializedName("version")
		public String version;
	}

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
