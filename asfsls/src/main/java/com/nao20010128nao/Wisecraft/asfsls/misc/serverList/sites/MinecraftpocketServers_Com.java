package com.nao20010128nao.Wisecraft.asfsls.misc.serverList.sites;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Parser class for "minecraftpocket-servers.com"
 */
public class MinecraftpocketServers_Com implements ServerListSite {

    public MinecraftpocketServers_Com() {
        // TODO 自動生成されたコンストラクター・スタブ
    }

    @Override
    public boolean matches(URL url) {
        // TODO 自動生成されたメソッド・スタブ
        return url.getHost().equalsIgnoreCase("minecraftpocket-servers.com");
    }

    @Override
    public boolean hasMultipleServers(URL url) throws IOException {
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
            Document page = Jsoup.connect(url.toString()).userAgent("Mozilla").get();
            Elements elems = page.select("html > body > div > div > div > div > table > tbody > tr > td > strong");
            return Arrays.asList(MslServer.makeServerFromString(elems.get(1).html(), true));
        }
        if (isPathStartsFromServers(url) | url.getPath().replace("/", "").equals("") | !isSingleServer(url.getPath())) {
            List<MslServer> list = new ArrayList<>();
            Document page = Jsoup.connect(url.toString()).userAgent("Mozilla").get();
            Elements elems = page.select("html > body > div > div > table > tbody > tr > td > strong");
            for (Element e : elems) {
                String ip = e.html();
                if (ip.startsWith("#"))
                    continue;
                list.add(MslServer.makeServerFromString(ip, true));
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
        String act = s[2];
        if (act.startsWith("server-s"))
            return true;
        return false;
    }
}
