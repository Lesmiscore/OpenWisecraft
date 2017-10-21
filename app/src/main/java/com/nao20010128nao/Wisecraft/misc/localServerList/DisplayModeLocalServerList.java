package com.nao20010128nao.Wisecraft.misc.localServerList;

import com.annimon.stream.*;
import com.nao20010128nao.Wisecraft.misc.*;

import java.util.*;
import com.nao20010128nao.Wisecraft.misc.*;

/**
 * Created by lesmi on 17/10/21.
 */
@ForDisplayMode
public class DisplayModeLocalServerList implements LocalServerList {
    private List<Server> servers=null;

    @Override
    public List<Server> load() {
        if(servers==null){
            List<Server> list=new ArrayList<>();
            /* Online */
            list.add(new Server("localhost",19132,Protobufs.Server.Mode.PE,null));
            /* Waiting */
            list.add(new Server("localhost",19133,Protobufs.Server.Mode.PE,null));
            /* Offline */
            list.add(new Server("localhost",19134,Protobufs.Server.Mode.PE,null));
            list.add(new Server("localhost",25565,Protobufs.Server.Mode.PC,null));
            list.add(new Server("localhost",19500,Protobufs.Server.Mode.PE,"Wisecraft"));
            servers=Collections.unmodifiableList(list);
        }
        return Stream.of(servers).map(Server::cloneAsServer).distinct().toList();
    }

    @Override
    public void save(List<Server> servers) {

    }
}
