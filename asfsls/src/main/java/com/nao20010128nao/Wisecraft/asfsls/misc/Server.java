package com.nao20010128nao.Wisecraft.asfsls.misc;

import android.text.TextUtils;

import com.annimon.stream.Stream;
import com.google.gson.annotations.SerializedName;

public class Server {
    @SerializedName("ip")
    public String ip;
    @SerializedName("port")
    public int port;
    @SerializedName("mode")
    public Mode mode;//0 is PE, 1 is PC
    @SerializedName("name")
    public String name;//,tag;

    @Override
    public int hashCode() {
        return ((((ip == null ? 0 : ip.hashCode()) << 6 ^ port) << 6) ^ mode.hashCode()) << 6 ^ (name == null ? 0 : name.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Server)) {
            return false;
        }
        Server os = (Server) o;
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

    public enum Mode {
        PE(0), PC(1);
        final int mode;

        Mode(int m) {
            mode = m;
        }

        public static Mode forNumber(int mode) {
            return Stream.of(values()).filter(a->a.mode==mode).findFirst().orElse(null);
        }
    }
}
