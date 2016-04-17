package com.nao20010128nao.Wisecraft.misc;
import android.util.Log;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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
