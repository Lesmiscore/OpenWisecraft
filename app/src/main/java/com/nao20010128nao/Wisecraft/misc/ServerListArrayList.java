package com.nao20010128nao.Wisecraft.misc;

import com.annimon.stream.*;

import java.util.*;
import java.util.concurrent.*;

public class ServerListArrayList extends CopyOnWriteArrayList<Server> implements ServerListProvider {
    public ServerListArrayList() {

    }

    public ServerListArrayList(Collection<Server> col) {
        super(col);
    }

    public ServerListArrayList(int cap) {
    }

    @Override
    public boolean contains(Object object) {
        return object != null && Stream.of(this).anyMatch(object::equals);
    }

    @Override
    public boolean add(Server object) {
        if (object == null) return false;
        return super.add(object);
    }

    @Override
    public void addIntoList(Server s) {
        add(s);
    }

    @Override
    public void removeFromList(Server s) {
        remove(s);
    }

    @Override
    public boolean contains(Server s) {
        return contains((Object) s);
    }
}
