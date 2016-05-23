package com.nao20010128nao.Wisecraft.misc.pinger.pc;

import com.nao20010128nao.Wisecraft.misc.pinger.*;
import java.util.*;

/**
 * References: http://wiki.vg/Server_List_Ping
 * https://gist.github.com/thinkofdeath/6927216
 */
public class Reply implements ServerPingResult,PCQueryResult {
	public String description;
	public Players players;
	public Version version;
	public String favicon;
	public ModInfo modinfo;
	
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
	
	public class ModInfo{
		public String type;
		public ModListContent[] modList;
	}
	
	public class ModListContent{
		public String modid;
		public String version;
	}
}
