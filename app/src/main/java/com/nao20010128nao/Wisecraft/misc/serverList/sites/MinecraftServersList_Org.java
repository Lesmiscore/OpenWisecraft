package com.nao20010128nao.Wisecraft.misc.serverList.sites;

import com.nao20010128nao.Wisecraft.misc.serverList.*;
import org.jsoup.*;
import org.jsoup.nodes.*;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Parser class for "minecraft-servers-list.org"
 */
public class MinecraftServersList_Org implements ServerListSite {

	public MinecraftServersList_Org() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public boolean matches(URL url) {
		// TODO 自動生成されたメソッド・スタブ
		return url.getHost().equalsIgnoreCase("www.minecraft-servers-list.org")
				| url.getHost().equalsIgnoreCase("minecraft-servers-list.org");
	}

	@Override
	public boolean hasMultipleServers(URL url) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public List<MslServer> getServers(URL url) throws IOException {
		// TODO 自動生成されたメソッド・スタブ
		if (url.getPath().replace("/", "").toLowerCase().startsWith("details")) {
			// Single server page
			Document page = Jsoup.connect(url.toString()).userAgent("Mozilla").get();
			String ip = page
					.select("html > body > div > div > section > div > div > div > div > div.content.col-md-8 > div.box > div.center > h5.text-muted > span.color")
					.get(0).html();
			return Arrays.asList(MslServer.makeServerFromString(ip, false));
		}
		return null;
	}
}
