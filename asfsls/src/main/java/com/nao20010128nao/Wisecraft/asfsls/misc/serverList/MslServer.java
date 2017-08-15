package com.nao20010128nao.Wisecraft.asfsls.misc.serverList;

/**
 * A server.
 */
public final class MslServer {
    /**
     * The address of the server
     */
    public String ip;
    /**
     * The port of the server
     */
    public int port;
    /**
     * Used for check the server is for PE or not
     */
    public boolean isPE;

    @Override
    public String toString() {
        // TODO 自動生成されたメソッド・スタブ
        return ip + ":" + port;
    }

    /**
     * Generates a Server instance from a String.
     */
    public static MslServer makeServerFromString(String ip, boolean isPE) {
        String[] spl = ip.split("\\:");
        MslServer s = new MslServer();
        s.ip = spl[0];
        // IP & port
        // IP only
        s.port = spl.length == 2 ? Integer.valueOf(spl[1]) : isPE ? 19132 : 25565;
        s.isPE = isPE;
        return s;
    }
}
