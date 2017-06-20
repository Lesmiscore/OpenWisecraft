package com.nao20010128nao.WRcon;

public class Server implements Cloneable {
    public String ip;
    public int port;


    @Override
    public int hashCode() {
        return ip.hashCode() ^ port;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Server)) {
            return false;
        }
        Server os = (Server) o;
        return os.ip.equals(ip) & os.port == port;
    }

    @Override
    public String toString() {
        return new StringBuilder().append(ip).append(':').append(port).toString();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Server s = new Server();
        s.ip = ip;
        s.port = port;
        return s;
    }
}
