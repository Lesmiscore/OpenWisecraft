package com.nao20010128nao.Wisecraft.misc.serverList;

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
        if (spl.length == 2) {
            // IP & port
            s.port = Integer.valueOf(spl[1]);
        } else {
            // IP only
            s.port = isPE ? 19132 : 25565;
        }
        s.isPE = isPE;
        return s;
    }
}
