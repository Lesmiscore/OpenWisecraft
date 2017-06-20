package com.nao20010128nao.Wisecraft.misc;

public interface IdContainer<T extends IdContainer> {
    public T setIntId(int id);

    public int getIntId();
}
