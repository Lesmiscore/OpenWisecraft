package com.nao20010128nao.Wisecraft.misc;

public class Server {
	public String ip;
	public int port;
	public int mode;//0 is PE, 1 is PC
	public String name;//,tag;

	@Override
	public int hashCode() {
		// TODO: Implement this method
		return ip.hashCode() ^ port ^ mode^name.hashCode();
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
		if(ip.matches(Constant.IPV6_PATTERN)){
			sb.append('[').append(ip).append(']');//IPv6
		}else{
			sb.append(ip);//IPv4
		}
		sb.append(':').append(port);
		return sb.toString();
	}
	
	public String resolveVisibleTitle(){
		if(android.text.TextUtils.isEmpty(name))
			return toString();
		else
			return name;
	}

	public Server cloneAsServer() {
		Server s=new Server();
		cloneInto(s);
		return s;
	}
    
    public final void cloneInto(Server dest){
        dest.ip=ip;
        dest.port=port;
        dest.mode=mode;
		dest.name=name;
    }
	
	public static Server from(OldServer35 a){
		Server s=new Server();
		s.ip=a.ip;
		s.port=a.port;
		s.mode=a.mode;
		return s;
	}
	
	public static Server from(OldServer19 a){
		Server s=new Server();
		s.ip=a.ip;
		s.port=a.port;
		s.mode=a.isPC?1:0;
		return s;
	}
}
