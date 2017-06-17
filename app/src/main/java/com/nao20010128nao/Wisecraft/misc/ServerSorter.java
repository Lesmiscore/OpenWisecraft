package com.nao20010128nao.Wisecraft.misc;

import java.util.*;

public final class ServerSorter implements Comparator<Server> {
    public static ServerSorter INSTANCE = new ServerSorter();

    @Override
    public int compare(Server p1, Server p2) {
        if (p1 == null & p2 != null) return -1;
        if (p1 != null & p2 == null) return 1;
        if (p1.equals(p2)) {
            return 0;
        }
        if (p1.ip.equals(p2.ip)) {
            if (p1.port == p2.port) {
                if (p1.mode == p2.mode) {
                    return 0;
                } else {
                    if (p1.mode == 0) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            } else {
                return p1.port > p2.port ? 1 : -1;
            }
        } else {
            return p1.ip.compareTo(p2.ip);
        }
    }
}
