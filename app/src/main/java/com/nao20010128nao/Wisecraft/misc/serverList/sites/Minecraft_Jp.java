package com.nao20010128nao.Wisecraft.misc.serverList.sites;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.nao20010128nao.Wisecraft.misc.serverList.MslServer;

/**
 * Parser class for "minecraft.jp"
 */
public class Minecraft_Jp implements ServerListSite {
	private static final List<String> PATH_BLACK_LIST = Arrays.asList("score", "vote", "player", "uptime", "ping",
			"recent", "random", "comment");

	public Minecraft_Jp() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public boolean matches(URL url) {
		// TODO 自動生成されたメソッド・スタブ
		return url.getHost().equalsIgnoreCase("minecraft.jp");
	}

	@Override
	public boolean hasMultipleServers(URL url) {
		// TODO 自動生成されたメソッド・スタブ
		if (isPathStartsFromServers(url) & isSingleServer(url.getPath()))
			return false;
		if (isPathStartsFromServers(url) | url.getPath().replace("/", "").equals("")
				| !isSingleServer(url.getPath()))
			return true;
		return false;
	}

	@Override
	public List<MslServer> getServers(URL url) throws IOException {
		// TODO 自動生成されたメソッド・スタブ
		if (isPathStartsFromServers(url) & isSingleServer(url.getPath())) {
			// Single server page
			String ip = url.getPath().substring(9);
			if (!ip.contains("."))
				// Server is private
				return null;
			return Arrays.asList(MslServer.makeServerFromString(ip, false));
		}
		if (isPathStartsFromServers(url) | url.getPath().replace("/", "").equals("") | !isSingleServer(url.getPath())) {
			List<MslServer> list = new ArrayList<>();
			Document page = Jsoup.connect(url.toString()).userAgent("Mozilla").get();
			Elements elems = page.select("html > body > #wrap > #content > table > tbody > tr > td.address");
			for (Element e : elems) {
				String ip = e.html();
				if (ip.equals("(非公開)"))
					// Server is private
					continue;
				list.add(MslServer.makeServerFromString(ip, false));
			}
			return list;
		}
		return null;
	}

	private boolean isPathStartsFromServers(URL url) {
		return url.getPath().replace("/", "").toLowerCase().startsWith("servers");
	}

	private boolean isSingleServer(String path) {
		String[] s = path.toLowerCase().split("\\/");
		if (s.length <= 1)
			return false;
		// System.err.println(s[2]);
		String act = s[2].split("\\:")[0];
		if (PATH_BLACK_LIST.contains(act))
			return false;
		if (act.contains("."))
			return true;
		return false;
	}
}
