package com.nao20010128nao.Wisecraft.misc;

//Server class for <=1.3

import com.google.gson.annotations.*;

public class OldServerW1_3 {
    @SerializedName("ip")
    public String ip;
    @SerializedName("port")
    public int port;
    @SerializedName("isPC")
    public boolean isPC;

    @Override
    public int hashCode() {
        return ip.hashCode() ^ port ^ ((Boolean) isPC).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OldServerW1_3)) {
            return false;
        }
        OldServerW1_3 os = (OldServerW1_3) o;
        return os.ip.equals(ip) & os.port == port & os.isPC == isPC;
    }

    @Override
    public String toString() {
        return ip + ":" + port;
    }

    public OldServerW1_3 cloneAsServer() {
        OldServerW1_3 s = new OldServerW1_3();
        s.ip = ip;
        s.port = port;
        s.isPC = isPC;
        return s;
    }
}
