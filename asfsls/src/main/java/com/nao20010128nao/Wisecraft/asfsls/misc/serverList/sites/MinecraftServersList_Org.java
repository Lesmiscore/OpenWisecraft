package com.nao20010128nao.Wisecraft.asfsls.misc.serverList.sites;

import com.nao20010128nao.Wisecraft.asfsls.misc.serverList.MslServer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Parser class for "minecraft-servers-list.org"
 */
public class MinecraftServersList_Org implements ServerListSite {

    public MinecraftServersList_Org() {
        // TODO 自動生成されたコンストラクター・スタブ
    }

    @Override
    public boolean matches(URL url) {
        // TODO 自動生成されたメソッド・スタブ
        return "www.minecraft-servers-list.org".equalsIgnoreCase(url.getHost())
            | "minecraft-servers-list.org".equalsIgnoreCase(url.getHost());
    }

    @Override
    public boolean hasMultipleServers(URL url) {
        // TODO 自動生成されたメソッド・スタブ
        return false;
    }

    @Override
    public List<MslServer> getServers(URL url) throws IOException {
        // TODO 自動生成されたメソッド・スタブ
        if (url.getPath().replace("/", "").toLowerCase().startsWith("details")) {
            // Single server page
            Document page = Jsoup.connect(url.toString()).userAgent("Mozilla").get();
            String ip = page
                .select("html > body > div > div > section > div > div > div > div > div.content.col-md-8 > div.box > div.center > h5.text-muted > span.color")
                .get(0).html();
            return Arrays.asList(MslServer.makeServerFromString(ip, false));
        }
        return null;
    }
}
