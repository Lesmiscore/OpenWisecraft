package com.nao20010128nao.Wisecraft.misc.localServerList;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.nao20010128nao.Wisecraft.misc.Server;
import com.nao20010128nao.Wisecraft.misc.Utils;
import com.nao20010128nao.Wisecraft.misc.compat.CompatCharsets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.List;

/**
 * Created by lesmi on 17/09/24.
 */

public class ExternalLocalServerList implements LocalServerList {
    private final File profileDir;
    Gson gson=Utils.newGson();

    public ExternalLocalServerList(File profileDir) {
        this.profileDir = Utils.requireNonNull(profileDir);
    }

    @Override
    public List<Server> load() {
        File serversFile=new File(profileDir,"servers.json");
        if(!serversFile.exists()){
            return Collections.emptyList();
        }
        try {
            return Utils.jsonToServers(Stream.of(Files.readLines(serversFile, CompatCharsets.UTF_8)).collect(Collectors.joining("\n")));
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public void save(List<Server> servers) {
        File serversFile=new File(profileDir,"servers.json");
        Writer writer=null;
        try {
            writer=Files.newWriter(serversFile, CompatCharsets.UTF_8);
            writer.write(gson.toJson(servers));
        } catch (IOException e) {
        } finally {
            try {
                if(writer!=null)
                    writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
