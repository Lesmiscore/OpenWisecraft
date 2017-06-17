package com.nao20010128nao.Wisecraft.misc;

public interface ServerListProvider {
    void addIntoList(Server s);

    boolean contains(Server s);

    void removeFromList(Server s);
}
