package com.nao20010128nao.Wisecraft.misc;

public class Server {
	public String ip;
	public int port;
	public int mode;//0 is PE, 1 is PC

	@Override
	public int hashCode() {
		// TODO: Implement this method
		return ip.hashCode() ^ port ^ mode;
	}

	@Override
	public boolean equals(Object o) {
		// TODO: Implement this method
		if (!(o instanceof Server)) {
			return false;
		}
		Server os=(Server)o;
		return os.ip.equals(ip) & os.port == port & os.mode == mode;
	}

	@Override
	public String toString() {
		// TODO: Implement this method
		StringBuilder sb=new StringBuilder();
		if(ip.matches(com.nao20010128nao.Wisecraft.Constant.IPV6_PATTERN)){
			sb.append('[').append(ip).append(']');//IPv6
		}else{
			sb.append(ip);//IPv4
		}
		sb.append(':').append(port);
		return sb.toString();
	}

	public Server cloneAsServer() {
		Server s=new Server();
		s.ip = ip;
		s.port = port;
		s.mode = mode;
		return s;
	}
}
