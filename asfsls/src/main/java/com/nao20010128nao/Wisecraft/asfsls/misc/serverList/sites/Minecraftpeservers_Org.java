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
 * Parser class for "minecraftpeservers.org"
 */
public class Minecraftpeservers_Org implements ServerListSite {

    public Minecraftpeservers_Org() {
        // TODO 自動生成されたコンストラクター・スタブ
    }

    @Override
    public boolean matches(URL url) {
        // TODO 自動生成されたメソッド・スタブ
        return "minecraftpeservers.org".equalsIgnoreCase(url.getHost());
    }

    @Override
    public boolean hasMultipleServers(URL url) {
        // TODO 自動生成されたメソッド・スタブ
        if ("".equals(url.getPath().replace("/", ""))
            | url.getPath().replace("/", "").toLowerCase().startsWith("index"))
            return true;
        if (url.getPath().replace("/", "").toLowerCase().startsWith("server"))
            return false;
        return false;
    }

    @Override
    public List<MslServer> getServers(URL url) throws IOException {
        // TODO 自動生成されたメソッド・スタブ
        if (url.getPath().replace("/", "").toLowerCase().startsWith("server")) {
            // Single server page
            Document page = Jsoup.connect(url.toString()).userAgent("Mozilla").get();
            String ip = page.select("html > body > #single > div > #left > table > tbody > tr > td > span").get(2)
                .html();
            return Arrays.asList(MslServer.makeServerFromString(ip, true));
        }
        if ("".equals(url.getPath().replace("/", ""))
            | url.getPath().replace("/", "").toLowerCase().startsWith("index")) {
            Document page = Jsoup.connect(url.toString()).userAgent("Mozilla").get();
            Elements elems = page.select("html > body > #main > div > table > tbody > tr > td > div > p");
            return Stream.of(elems)
                .map(Element::html)
                .map(a->a.substring(29))
                .map(ip->MslServer.makeServerFromString(ip, true))
                .toList();
        }
        return null;
    }
}
