package com.nao20010128nao.McServerPingPong.rcon;
import com.google.rconclient.rcon.*;
import com.nao20010128nao.McServerPingPong.*;
import java.io.*;
import java.util.*;

public class RConModified extends RCon
{
	public RConModified(String ip,int port,char[] password)throws IOException,AuthenticationException{
		super(ip,port,password);
	}

	@Override
	public String[] list() throws IOException, AuthenticationException {
		// TODO: Implement this method
		String[] data=lines(send("list"));
		dump(data);
		if(data.length>=2){
			return data[1].split("\\, ");
		}
		return Consistant.EMPTY_STRING_ARRAY;
	}

	@Override
	public String[] banList() throws IOException, AuthenticationException {
		// TODO: Implement this method
		String[] data=lines(send("banlist"));
		dump(data);
		if(data.length>=2){
			return data[1].split("\\, ");
		}
		return Consistant.EMPTY_STRING_ARRAY;
	}

	@Override
	public String[] banIPList() throws IOException, AuthenticationException {
		// TODO: Implement this method
		String[] data=lines(send("banlist ips"));
		dump(data);
		if(data.length>=2){
			return data[1].split("\\, ");
		}
		return Consistant.EMPTY_STRING_ARRAY;
	}
	
	private String[] lines(String s)throws IOException{
		BufferedReader br=new BufferedReader(new StringReader(s));
		List<String> tmp=new ArrayList(2);
		String line=null;
		while(null!=(line=br.readLine()))tmp.add(line);
		return tmp.toArray(new String[tmp.size()]);
	}
	private void dump(String[] s){
		System.out.println(s.length);
		for(String as:s)System.out.println(as);
	}
}
