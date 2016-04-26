package com.nao20010128nao.Wisecraft.misc;

public class OldServer19 {
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
		if (!(o instanceof OldServer19)) {
			return false;
		}
		OldServer19 os=(OldServer19)o;
		return os.ip.equals(ip) & os.port == port & (os.isPC ^ isPC) == false;
	}

	@Override
	public String toString() {
		// TODO: Implement this method
		return ip + ":" + port;
	}

	public OldServer19 cloneAsServer() {
		OldServer19 s=new OldServer19();
		s.ip = ip;
		s.port = port;
		s.isPC = isPC;
		return s;
	}
}
