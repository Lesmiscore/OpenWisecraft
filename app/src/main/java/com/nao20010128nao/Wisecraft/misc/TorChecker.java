package com.nao20010128nao.Wisecraft.misc;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.net.URL;
import java.io.IOException;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import android.util.Log;

public class TorChecker
{
	public static boolean checkTor1()throws IOException{
		Document html=Jsoup.connect("http://check.torproject.org/?lang=en_US").userAgent("Mozilla").get();
		Element torStat=html.select("div>h1").get(0);
		Log.d("TorChecker",torStat.text());
		if(torStat.text().toLowerCase().contains("Conguratulations")){
			return true;
		}
		return false;
	}
}
