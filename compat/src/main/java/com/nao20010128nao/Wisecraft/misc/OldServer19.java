package com.nao20010128nao.Wisecraft.misc;

//Server class for <=1.3
public class OldServer19 {
	public String ip;
	public int port;
	public boolean isPC;

	@Override
	public int hashCode() {
		return ip.hashCode() ^ port^((Boolean)isPC).hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof OldServer19)) {
			return false;
		}
		OldServer19 os=(OldServer19)o;
		return os.ip.equals(ip) & os.port == port & (os.isPC ^ isPC) == false;
	}

	@Override
	public String toString() {
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
