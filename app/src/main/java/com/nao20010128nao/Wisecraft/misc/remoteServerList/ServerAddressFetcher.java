package com.nao20010128nao.Wisecraft.misc.remoteServerList;

import com.nao20010128nao.Wisecraft.misc.collector.*;
import com.nao20010128nao.Wisecraft.misc.remoteServerList.sites.*;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Finds Minecraft multiplayer IP & port from website. Cannot be instantinated.
 */
public class ServerAddressFetcher {
    static Set<ServerListSite> services = new HashSet<>();

    static {
        services.add(new Minecraft_Jp());
        services.add(new Minecraftpeservers_Org());
        services.add(new Minecraftservers_Org());
        // services.add(new MinecraftServersList_Org());//Is this website dead?
        services.add(new Pe_Minecraft_Jp());
        services.add(new MinecraftpocketServers_Com());
        services.add(new MinecraftMp_Com());
        services.add(new Pmmp_Jp_Net());
    }

    private ServerAddressFetcher() {
        // TODO 自動生成されたコンストラクター・スタブ
    }

    /**
     * Finds Minecraft multiplayer IP & port from a website.
     *
     * @param url An URL that is for server's detail or servers list
     * @return A list that was found servers contains. Immutable.
     */
    public static List<MslServer> findServersInWebpage(URL url) throws IOException {
        Set<ServerListSite> service = new HashSet<>();
        for (ServerListSite serv : services)
            if (serv.matches(url))
                service.add(serv);
        if (service.size() == 0)
            throw new IllegalArgumentException("This website is not supported: " + url);
        List<Throwable> errors = new ArrayList<>();
        for (ServerListSite serv : service)
            try {
                List<MslServer> servers = serv.getServers(url);
                if (servers == null)
                    continue;
                return Collections.unmodifiableList(new ArrayList<>(servers));
            } catch (Throwable e) {
                errors.add(e);
                CollectorMain.reportError("ServerAddressFetcher", e);
            }
        int errSize = errors.size();
        Throwable first = errors.get(0);
        for (int i = 0; i < errSize - 1; i++) {
            errors.get(0).addSuppressed(errors.get(1));
            errors.remove(0);
        }
        throw new IllegalArgumentException("Unsupported webpage: " + url, first);
    }

    /**
     * Add a website for finding servers.
     *
     * @param service An instance of ServerListSite class.
     */
    public static void addService(ServerListSite service) {
        services.add(service);
    }
}
