package com.nao20010128nao.Wisecraft.misc;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.net.URL;
import java.io.IOException;

public class TorChecker
{
	public static boolean checkTor1()throws IOException{
		Document html=Jsoup.parse(new URL("http://check.torproject.org/?lang=en_US"),1000);
		
		return false;
	}
}
