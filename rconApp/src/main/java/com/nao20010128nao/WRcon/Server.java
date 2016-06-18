package com.nao20010128nao.WRcon;

public class Server
{
	public String ip;
	public int port;
	

	@Override
	public int hashCode() {
		// TODO: Implement this method
		return ip.hashCode() ^ port;
	}

	@Override
	public boolean equals(Object o) {
		// TODO: Implement this method
		if (!(o instanceof Server)) {
			return false;
		}
		Server os=(Server)o;
		return os.ip.equals(ip) & os.port == port;
	}

	@Override
	public String toString() {
		// TODO: Implement this method
		return new StringBuilder().append(ip).append(':').append(port).toString();
	}
	
}
