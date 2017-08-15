package com.nao20010128nao.Wisecraft.asfsls.misc.serverList.sites;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.nao20010128nao.Wisecraft.asfsls.misc.json.WJOUtils;
import com.nao20010128nao.Wisecraft.asfsls.misc.json.WisecraftJsonObject;
import com.nao20010128nao.Wisecraft.asfsls.misc.serverList.MslServer;
import com.nao20010128nao.Wisecraft.misc.CompatUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser class for "pmmp.jp.net".
 */

public class Pmmp_Jp_Net implements ServerListSite {
    private static final String BASE64CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    private static final String PORT_NUMBER_PATTERN = "^[0-9]{1,5}$";

    public Pmmp_Jp_Net() {
        // TODO 自動生成されたコンストラクター・スタブ
    }

    @Override
    public boolean matches(URL url) {
        // TODO 自動生成されたメソッド・スタブ
        return "pmmp.jp.net".equalsIgnoreCase(url.getHost())
            | "mc-pe.online".equalsIgnoreCase(url.getHost())
            | "minecraftpe.jp".equalsIgnoreCase(url.getHost());
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
        Writer w=null;
        try{
            w = new OutputStreamWriter(con.getOutputStream());
            w.write("id=" + generateId() + "&notify=" + generateNotify() + "&app=2");
            w.flush();
        }finally {
            CompatUtils.safeClose(w);
        }
        Reader r=null;
        try{
            r = new InputStreamReader(con.getInputStream(), "UTF-8");
            WisecraftJsonObject sl = WJOUtils.parse(r);
            List<MslServer> result = new ArrayList<>();
            for (WisecraftJsonObject jo : sl.get("servers")) {
                String ip, port;
                if (jo.has("ip")) {
                    ip = jo.get("ip").getAsString();
                } else {
                    ip = null;
                }
                if (jo.has("port")) {
                    port = jo.get("port").getAsString();
                } else {
                    port = null;
                }
                if (TextUtils.isEmpty(ip) | TextUtils.isEmpty(port))
                    continue;
                if (!ip.contains("."))
                    continue;
                if (!port.matches(PORT_NUMBER_PATTERN))
                    continue;
                MslServer s = new MslServer();
                s.ip = ip;
                s.port = Integer.valueOf(port);
                s.isPE = true;
                result.add(s);
            }
            return result;
        }finally {
            CompatUtils.safeClose(r);
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
}
