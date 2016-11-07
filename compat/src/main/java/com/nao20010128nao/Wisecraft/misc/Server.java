package com.nao20010128nao.Wisecraft.misc;

import android.text.*;
import com.google.gson.annotations.*;

public class Server {
	@SerializedName("ip")
	public String ip;
	@SerializedName("port")
	public int port;
	@SerializedName("mode")
	public int mode;//0 is PE, 1 is PC
	@SerializedName("name")
	public String name;//,tag;

	@Override
	public int hashCode() {
		return ((((ip==null?0:ip.hashCode())<<6 ^ port)<<6) ^ mode)<<6 ^ (name==null?0:name.hashCode());
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Server)) {
			return false;
		}
		Server os=(Server)o;
		return os.ip.equals(ip) & os.port == port & os.mode == mode;
	}

	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		if(/*ip.matches(Constant.IPV6_PATTERN)*/false){
			sb.append('[').append(ip).append(']');//IPv6
		}else{
			sb.append(ip);//IPv4
		}
		sb.append(':').append(port);
		return sb.toString();
	}
	
	public String resolveVisibleTitle(){
		if(TextUtils.isEmpty(name))
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
