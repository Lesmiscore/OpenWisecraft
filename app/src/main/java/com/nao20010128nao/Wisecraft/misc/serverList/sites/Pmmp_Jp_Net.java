package com.nao20010128nao.Wisecraft.misc.serverList.sites;

import android.text.*;
import com.google.gson.annotations.*;
import com.nao20010128nao.Wisecraft.misc.json.*;
import com.nao20010128nao.Wisecraft.misc.serverList.*;

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;

/**
 * Parser class for "pmmp.jp.net".
 */

public class Pmmp_Jp_Net implements ServerListSite {
	private static final String BASE64CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
	private static final String PORT_NUMBER_PATTERN="^[0-9]{1,5}$";

	public Pmmp_Jp_Net() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public boolean matches(URL url) {
		// TODO 自動生成されたメソッド・スタブ
		return url.getHost().equalsIgnoreCase("pmmp.jp.net")
			| url.getHost().equalsIgnoreCase("mc-pe.online")
			| url.getHost().equalsIgnoreCase("minecraftpe.jp");
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
		try(Writer w = new OutputStreamWriter(con.getOutputStream())){
			w.write("id=" + generateId() + "&notify=" + generateNotify() + "&app=2");
			w.flush();
		}
		try(Reader r=new InputStreamReader(con.getInputStream(), "UTF-8")){
			WisecraftJsonObject sl=WJOUtils.parse(r);
			List<MslServer> result = new ArrayList<>();
			for (WisecraftJsonObject jo : sl.get("servers")) {
				String ip,port;
				if(jo.has("ip")){
					ip=jo.get("ip").getAsString();
				}else{
					ip=null;
				}
				if(jo.has("port")){
					port=jo.get("port").getAsString();
				}else{
					port=null;
				}
				if (TextUtils.isEmpty(ip)|TextUtils.isEmpty(port))
					continue;
				if (!ip.contains("."))
					continue;
				if(!port.matches(PORT_NUMBER_PATTERN))
					continue;
				MslServer s = new MslServer();
				s.ip = ip;
				s.port = Integer.valueOf(port);
				s.isPE = true;
				result.add(s);
			}
			return result;
		}
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
