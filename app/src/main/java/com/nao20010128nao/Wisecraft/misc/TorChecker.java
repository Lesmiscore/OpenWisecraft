package com.nao20010128nao.Wisecraft.misc;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.net.URL;
import java.io.IOException;
import org.jsoup.select.Elements;

public class TorChecker
{
	public static boolean checkTor1()throws IOException{
		Document html=Jsoup.parse(new URL("http://check.torproject.org/?lang=en_US"),1000);
		Elements torStat=html.select("h1");
		return false;
	}
}
