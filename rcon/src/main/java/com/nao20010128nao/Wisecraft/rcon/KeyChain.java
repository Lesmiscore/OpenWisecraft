package com.nao20010128nao.Wisecraft.rcon;

public interface KeyChain {
    boolean isPasswordStored(String ip, int port);

    String[] getRecentPassword(String ip, int port);
}
