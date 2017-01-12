package com.nao20010128nao.Wisecraft.misc.pinger.pc;

import com.google.gson.annotations.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import com.nao20010128nao.Wisecraft.misc.pinger.*;
import java.util.*;

/**
 * References: http://wiki.vg/Server_List_Ping
 * https://gist.github.com/thinkofdeath/6927216
 */
@ShouldBeKept
public class Reply implements ServerPingResult,PCQueryResult,ShouldBeKept2 {
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
	
	@ShouldBeKept
	public class Players implements ShouldBeKept2{
		@SerializedName("max")
		public int max;
		@SerializedName("online")
		public int online;
		@SerializedName("sample")
		public List<Player> sample;
	}

	@ShouldBeKept
	public class Player implements ShouldBeKept2{
		@SerializedName("name")
		public String name;
		@SerializedName("id")
		public String id;
	}

	@ShouldBeKept
	public class Version implements ShouldBeKept2{
		@SerializedName("name")
		public String name;
		@SerializedName("protocol")
		public int protocol;
	}
	
	@ShouldBeKept
	public class ModInfo implements ShouldBeKept2{
		@SerializedName("type")
		public String type;
		@SerializedName("modList")
		public ModListContent[] modList;
	}
	
	@ShouldBeKept
	public class ModListContent implements ShouldBeKept2{
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
