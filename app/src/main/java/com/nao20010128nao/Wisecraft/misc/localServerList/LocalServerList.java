package com.nao20010128nao.Wisecraft.misc.localServerList;

import com.nao20010128nao.Wisecraft.misc.Server;

import java.util.List;

/**
 * Created by lesmi on 17/09/24.
 */

public interface LocalServerList {
    List<Server> load();
    void save(List<Server> servers);
}
