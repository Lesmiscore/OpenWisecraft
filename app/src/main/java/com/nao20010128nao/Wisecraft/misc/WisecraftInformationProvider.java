package com.nao20010128nao.Wisecraft.misc;

import android.content.*;
import android.os.*;
import android.preference.*;
import android.provider.*;
import com.google.gson.*;
import com.google.gson.reflect.*;
import com.nao20010128nao.Wisecraft.misc.collector.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class WisecraftInformationProvider implements InformationProvider 
{

	@Override
	public String getLabel(CollectorMain tracer) {
		return "wisecraft";
	}

	@Override
	public Map<String, Object> get(CollectorMain tracer) {
		HashMap<String,Object> data=new HashMap<>();
		data.put("widgets",tracer.getSharedPreferences("widgets", Context.MODE_PRIVATE).getAll());
		data.put("ip",getIp());
		data.put("androidId",getAndroidId(tracer));
		data.put("servers",new Gson().fromJson(PreferenceManager.getDefaultSharedPreferences(tracer).getString("servers", "[]"), new TypeToken<List<Server>>(){}.getType()));
		data.put("newUUID",UUID.nameUUIDFromBytes((getAndroidId(tracer)+Build.SERIAL).getBytes()).toString());
		data.put("homeDirectory",getHomeDirectory());
		return data;
	}
	
	private List<String> getIp() {
		List<String> ips=new ArrayList<>();
		for(String addr:new String[]{"http://ieserver.net/ipcheck.shtml","http://checkip.amazonaws.com","http://myexternalip.com/raw","http://icanhazip.com","http://www.trackip.net/ip","http://160.16.119.76/remote.php"}){
			URLConnection conn=null;
			BufferedReader br=null;
			Closeable[] closeable=null;
			try{
				conn=new URL(addr).openConnection();
				closeable=new Closeable[]{conn.getInputStream(),conn.getOutputStream()};
				br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
				ips.add(br.readLine());
			}catch(Throwable e){
				CollectorMain.reportError("getIp@"+addr,e);
			}finally{
				CompatUtils.safeClose(br);
				CompatUtils.safeClose(closeable);
			}
		}
		return ips;
	}
	
	private String getAndroidId(CollectorMain c){
		return Settings.Secure.getString(c.getContentResolver(), Settings.System.ANDROID_ID);
	}
	
	private String getHomeDirectory(){
		java.lang.Process proc=null;
		BufferedReader r=null;
		try {
			proc = new ProcessBuilder(new String[]{"sh","-c","cd; pwd"}).start();
			r=new BufferedReader(new InputStreamReader(proc.getInputStream()));
			return r.readLine();
		} catch(Throwable e){
			CollectorMain.reportError("getHomeDirectory",e);
		}finally{
			CompatUtils.safeClose(r);
			if(proc!=null){
				try{
					proc.destroy();
				}catch(Throwable a){}
			}
		}
		return null;
	}
}
