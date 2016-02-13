package com.nao20010128nao.Wisecraft.rcon;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.rconclient.rcon.AuthenticationException;
import com.google.rconclient.rcon.RCon;
import com.nao20010128nao.Wisecraft.Constant;
import com.nao20010128nao.Wisecraft.struct.WCH_ServerInfo;
import java.io.IOException;

import static com.nao20010128nao.Wisecraft.Utils.*;

public class RConModified extends RCon {
	Gson gson=new Gson();
	WCH_ServerInfo servi;
	public RConModified(String ip, int port, char[] password)throws IOException,AuthenticationException {
		super(ip, port, password);
		try {
			servi = gson.fromJson(send("wisecraft wisecraft info"), WCH_ServerInfo.class);
		} catch (JsonSyntaxException e) {
			//If the Wisecraft Helper Plugin does not applied,
			servi = null;
		}
	}

	@Override
	public String[] list() throws IOException, AuthenticationException {
		// TODO: Implement this method
		if (servi != null) {
			try {
				return gson.fromJson(send("wisecraft wisecraft player list"), String[].class);
			} catch (JsonSyntaxException e) {
				//If the Wisecraft Helper Plugin does not applied,
				//use /list instead.
			}
		}
		String[] data=lines(send("list"));
		dump(data);
		if (data.length >= 2) {
			return data[1].split("\\, ");
		}
		return Constant.EMPTY_STRING_ARRAY;
	}

	@Override
	public String[] banList() throws IOException, AuthenticationException {
		// TODO: Implement this method
		String[] data=lines(send("banlist"));
		dump(data);
		if (data.length >= 2) {
			return data[1].split("\\, ");
		}
		return Constant.EMPTY_STRING_ARRAY;
	}

	@Override
	public String[] banIPList() throws IOException, AuthenticationException {
		// TODO: Implement this method
		String[] data=lines(send("banlist ips"));
		dump(data);
		if (data.length >= 2) {
			return data[1].split("\\, ");
		}
		return Constant.EMPTY_STRING_ARRAY;
	}

	public WCH_ServerInfo getServerInfo() {
		return servi;
	}

	private void dump(String[] s) {
		System.out.println(s.length);
		for (String as:s)System.out.println(as);
	}
}
