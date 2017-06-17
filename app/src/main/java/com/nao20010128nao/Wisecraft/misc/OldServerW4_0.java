package com.nao20010128nao.Wisecraft.misc;

import android.text.*;
import com.google.gson.annotations.*;

// Server class for <=4.0

public class OldServerW4_0 {
    @SerializedName("ip")
    public String ip;
    @SerializedName("port")
    public int port;
    @SerializedName("mode")//TODO: change to enum, if I can
    public int mode;//0 is PE, 1 is PC
    @SerializedName("name")
    public String name;//,tag;

    @Override
    public int hashCode() {
        return ((((ip == null ? 0 : ip.hashCode()) << 6 ^ port) << 6) ^ mode) << 6 ^ (name == null ? 0 : name.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OldServerW4_0)) {
            return false;
        }
        OldServerW4_0 os = (OldServerW4_0) o;
        return os.ip.equals(ip) & os.port == port & os.mode == mode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (/*ip.matches(Constant.IPV6_PATTERN)*/false) {
            sb.append('[').append(ip).append(']');//IPv6
        } else {
            sb.append(ip);//IPv4
        }
        sb.append(':').append(port);
        return sb.toString();
    }

    public String resolveVisibleTitle() {
        if (TextUtils.isEmpty(name))
            return toString();
        else
            return name;
    }

    public OldServerW4_0 cloneAsServer() {
        OldServerW4_0 s = new OldServerW4_0();
        cloneInto(s);
        return s;
    }

    public Protobufs.Server toProtobufServer() {
        return Protobufs.Server.newBuilder().setIp(ip).setPort(port).setMode(Protobufs.Server.Mode.forNumber(mode)).build();
    }

    public final void cloneInto(OldServerW4_0 dest) {
        dest.ip = ip;
        dest.port = port;
        dest.mode = mode;
        dest.name = name;
    }

    public static OldServerW4_0 from(OldServerW3_2 a) {
        OldServerW4_0 s = new OldServerW4_0();
        s.ip = a.ip;
        s.port = a.port;
        s.mode = a.mode;
        return s;
    }

    public static OldServerW4_0 from(OldServerW1_3 a) {
        OldServerW4_0 s = new OldServerW4_0();
        s.ip = a.ip;
        s.port = a.port;
        s.mode = a.isPC ? 1 : 0;
        return s;
    }

    public static OldServerW4_0 from(Protobufs.Server a) {
        OldServerW4_0 s = new OldServerW4_0();
        s.ip = a.getIp();
        s.port = a.getPort();
        s.mode = a.getMode().getNumber();
        return s;
    }
}
