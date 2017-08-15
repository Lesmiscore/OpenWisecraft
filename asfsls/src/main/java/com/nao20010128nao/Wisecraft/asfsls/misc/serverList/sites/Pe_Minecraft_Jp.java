package com.nao20010128nao.Wisecraft.asfsls.misc.serverList.sites;

import com.annimon.stream.Stream;
import com.nao20010128nao.Wisecraft.asfsls.misc.serverList.MslServer;

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
 * Parser class for "pe.minecraft.jp"
 */
public class Pe_Minecraft_Jp implements ServerListSite {
    private static final List<String> PATH_BLACK_LIST = Arrays.asList("score", "vote", "player", "uptime", "ping",
        "recent", "random", "comment");

    public Pe_Minecraft_Jp() {
        // TODO 自動生成されたコンストラクター・スタブ
    }

    @Override
    public boolean matches(URL url) {
        // TODO 自動生成されたメソッド・スタブ
        return "pe.minecraft.jp".equalsIgnoreCase(url.getHost());
    }

    @Override
    public boolean hasMultipleServers(URL url) {
        // TODO 自動生成されたメソッド・スタブ
        return !(isPathStartsFromServers(url) & isSingleServer(url.getPath())) && isPathStartsFromServers(url) | "".equals(url.getPath().replace("/", "")) | !isSingleServer(url.getPath());
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
            return Arrays.asList(MslServer.makeServerFromString(ip, true));
        }
        if (isPathStartsFromServers(url) | url.getPath().replace("/", "").equals("") | !isSingleServer(url.getPath())) {
            Document page = Jsoup.connect(url.toString()).userAgent("Mozilla").get();
            Elements elems = page.select("html > body > #wrap > #content > table > tbody > tr > td.address");
            return Stream.of(elems)
                .map(Element::html)
                .filterNot("(非公開)"::equals)
                .map(ip->MslServer.makeServerFromString(ip, true))
                .toList();
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
        return !PATH_BLACK_LIST.contains(act) && act.contains(".");
    }
}
