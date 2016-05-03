package com.nao20010128nao.Wisecraft.misc.pinger.pc;

import com.nao20010128nao.Wisecraft.misc.pinger.ServerPingResult;
import java.util.List;

/**
 * References: http://wiki.vg/Server_List_Ping
 * https://gist.github.com/thinkofdeath/6927216
 */
public class Reply19 implements ServerPingResult,PCQueryResult {
	public Description description;
	public Players players;
	public Version version;
	public String favicon;
	public ModInfo modinfo;//TODO: Is this correct?

	public class Players {
		public int max;
		public int online;
		public List<Player> sample;
	}

	public class Player {
		public String name;
		public String id;
	}

	public class Version {
		public String name;
		public int protocol;
	}

	public class Description{
		public String text;
	}
	
	public class ModInfo{
		public String type;
		public ModListContent[] modList;
	}

	public class ModListContent{
		public String modid;
		public String version;
	}
}