package com.nao20010128nao.Wisecraft.misc;

public class Server {
	public String ip;
	public int port;
	public boolean isPC;

	@Override
	public int hashCode() {
		// TODO: Implement this method
		return ip.hashCode() ^ port^((Boolean)isPC).hashCode();
	}

	@Override
	public boolean equals(Object o) {
		// TODO: Implement this method
		if (!(o instanceof Server)) {
			return false;
		}
		Server os=(Server)o;
		return os.ip.equals(ip) & os.port == port & (os.isPC ^ isPC) == false;
	}

	@Override
	public String toString() {
		// TODO: Implement this method
		return ip + ":" + port;
	}

	public Server cloneAsServer() {
		Server s=new Server();
		s.ip = ip;
		s.port = port;
		s.isPC = isPC;
		return s;
	}
}
