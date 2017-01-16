package com.nao20010128nao.Wisecraft.misc.serverList.sites;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.nao20010128nao.Wisecraft.misc.serverList.MslServer;

/**
 * Parser class for "pmmp.jp.net".
 */

public class Pmmp_Jp_Net implements ServerListSite {
	private static final String BASE64CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

	public Pmmp_Jp_Net() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public boolean matches(URL url) {
		// TODO 自動生成されたメソッド・スタブ
		return url.getHost().equalsIgnoreCase("pmmp.jp.net") | url.getHost().equalsIgnoreCase("mc-pe.online");
	}

	@Override
	public boolean hasMultipleServers(URL url) {
		// TODO 自動生成されたメソッド・スタブ
		return true;
	}

	@Override
	public List<MslServer> getServers(URL url) throws IOException {
		// TODO 自動生成されたメソッド・スタブ
		HttpURLConnection con = (HttpURLConnection) new URL("http://api.pmmp.jp.net/list").openConnection();
		con.setDoOutput(true);

		con.setRequestMethod("POST");
		con.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		con.setRequestProperty("User-Agent", "Apache-HttpClient/UNAVAILABLE (java 1.4)");
		Writer w = new OutputStreamWriter(con.getOutputStream());
		w.write("id=" + generateId() + "&notify=" + generateNotify() + "&app=2");
		w.flush();
		PMMP_Servers_List sl = new Gson().fromJson(new InputStreamReader(con.getInputStream(), "UTF-8"),
				PMMP_Servers_List.class);
		List<MslServer> result = new ArrayList<>();
		for (ServerEntry se : sl.servers) {
			if (se.ip == null)
				continue;
			if (!se.ip.contains("."))
				continue;
			MslServer s = new MslServer();
			s.ip = se.ip;
			s.port = new Integer(se.port);
			s.isPE = true;
			result.add(s);
		}
		return result;
	}

	private String generateId() {
		SecureRandom sr = new SecureRandom();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 16; i++)
			sb.append(BASE64CHARS.charAt(Math.abs(sr.nextInt()) % 64));
		return sb.toString();
	}

	private String generateNotify() {
		SecureRandom sr = new SecureRandom();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 140; i++)
			sb.append(BASE64CHARS.charAt(Math.abs(sr.nextInt()) % 64));
		return sb.toString();
	}

	public static class PMMP_Servers_List {
		@SerializedName("status")
		public int status;
		@SerializedName("userID")
		public String userID;
		@SerializedName("servers")
		public ServerEntry[] servers;
	}

	public static class ServerEntry {
		@SerializedName("ver")
		public int ver;
		@SerializedName("notify")
		public int notify;
		@SerializedName("no")
		public int no;
		@SerializedName("status")
		public int status;
		@SerializedName("playerMax")
		public int playerMax;
		@SerializedName("playerNow")
		public int playerNow;
		@SerializedName("_order")
		public long _order;
		@SerializedName("name")
		public String name;
		@SerializedName("description")
		public String description;
		@SerializedName("ip")
		public String ip;
		@SerializedName("port")
		public String port;
		@SerializedName("icon")
		public String icon;
		@SerializedName("topImg")
		public String topImg;
		@SerializedName("owner")
		public String owner;
		@SerializedName("sites")
		public SitesEntry[] sites;
		@SerializedName("categories")
		public CategoriesEntry[] categories;
	}

	public static class SitesEntry {
		@SerializedName("title")
		public String title;
		@SerializedName("url")
		public String url;
		@SerializedName("icon")
		public int icon;
		@SerializedName("schemes")
		public SchememesEntry[] schemes;
	}

	public static class SchememesEntry {
		@SerializedName("title")
		public String title;
		@SerializedName("icon")
		public String icon;
		@SerializedName("scheme")
		public String scheme;
		@SerializedName("allowAndroid")
		public boolean allowAndroid;
	}

	public static class CategoriesEntry {
		@SerializedName("color")
		public int color;
		@SerializedName("mark")
		public int mark;
		@SerializedName("text")
		public String text;
	}
}
