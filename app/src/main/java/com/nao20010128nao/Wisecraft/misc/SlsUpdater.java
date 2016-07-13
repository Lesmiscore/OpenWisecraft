package com.nao20010128nao.Wisecraft.misc;
import android.content.*;
import android.net.*;
import android.preference.*;
import android.util.*;
import com.nao20010128nao.McServerList.*;
import com.nao20010128nao.McServerList.sites.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.services.*;
import dalvik.system.*;
import java.io.*;
import java.util.*;

import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.util.*;

import org.apache.commons.codec.binary.Base64;

public class SlsUpdater extends Thread
{
	static boolean execOnce=false;
	SlsUpdaterService ctx;
	public SlsUpdater(SlsUpdaterService conte){
		ctx=conte;
		ctx.getDir("mcserverlist",777);
	}
	@Override
	public void run() {
		if(execOnce){
			ctx.stopSelf();
			return;
		}
		execOnce=true;
		try {
			ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(ctx.CONNECTIVITY_SERVICE);
			SharedPreferences appSettings=PreferenceManager.getDefaultSharedPreferences(ctx);
			SharedPreferences cache=ctx.getSharedPreferences("sls_vers_cache", 0);

			String conName;
			if (cm.getActiveNetworkInfo() == null) {
				conName = "offline";
			} else {
				conName = cm.getActiveNetworkInfo().getTypeName();
			}

			if (conName.equalsIgnoreCase("offline")) {
				Log.d("slsupd", "no connection");
				writeVersions(cache);
				loadCurrentCode();
				return;
			}
			if (!appSettings.getBoolean("allowAutoUpdateSLSCode", false)) {
				Log.d("slsupd", "disabled");
				writeVersions(cache);
				return;
			}
			if (!appSettings.getBoolean("aausc_monnet", false)) {
				if ("mobile".equalsIgnoreCase(conName)) {
					Log.d("slsupd", "mobile update is not allowed");
					writeVersions(cache);
					loadCurrentCode();
					return;
				}
			}

			//delete tmp and download the file
			File tmp=new File(ctx.getFilesDir(), "mcserverlist/tmp.dex");
			File dat=new File(ctx.getFilesDir(), "mcserverlist/dat.dex");
			tmp.delete();
			HttpGet get=new HttpGet("http://nao20010128nao.github.io/wisecraft/todaiji.dex");
			DefaultHttpClient dhc=new DefaultHttpClient();
			try {
				Utils.writeToFileByBytes(tmp,EntityUtils.toByteArray(dhc.execute(get).getEntity()));
			} catch (Throwable e) {
				DebugWriter.writeToE("slsupd",e);
			} finally {
				try {
					dhc.getConnectionManager().shutdown();
				} catch (Throwable e) {}
				writeVersions(cache);
			}
			if (!(cache.contains("tmp.minwc")|cache.contains("dat.minwc"))) {
				Log.d("slsupd", "broken dex file downloaded");
				loadCurrentCode();
				return;
			}
			if (Utils.getVersionCode(ctx) < cache.getInt("tmp.minwc", 0)) {
				Log.d("slsupd", "unsupported minimum Wisecraft version:" + cache.getInt("tmp.minwc", 0));
				loadCurrentCode();
				return;
			}
			dat.delete();
			tmp.renameTo(dat);
			loadCurrentCode();
		} catch (Throwable e) {
			DebugWriter.writeToE("slsupd",e);
		}
	}
	public void writeVersions(SharedPreferences cache){
		if(new File(ctx.getFilesDir(),"mcserverlist/dat.dex").exists()){
			try {
				List<String> args=new ArrayList();
				args.add("dalvikvm");
				args.add("-classpath");
				args.add(ctx.getApplicationInfo().sourceDir);
				args.add(SlsUpdater.class.getName());
				args.add(ctx.getCacheDir().getAbsolutePath());
				args.add(new File(ctx.getFilesDir(), "mcserverlist/dat.dex").getAbsolutePath());
				ProcessBuilder pb=new ProcessBuilder(args);
				pb.directory(ctx.getCacheDir());
				Process proc=pb.start();
				BufferedReader br=new BufferedReader(new InputStreamReader(proc.getInputStream()));
				String data=br.readLine();
				br.close();
				DataInputStream dis=new DataInputStream(new ByteArrayInputStream(Base64.decodeBase64(data.getBytes())));
				cache.edit().putString("dat.vcode",dis.readUTF()).putInt("dat.minwc",dis.readInt()).commit();
				Log.i("slsupd","data-dat:"+data);
			} catch (Throwable e) {
				cache.edit().remove("dat.vcode").remove("dat.minwc").commit();
				DebugWriter.writeToE("slsupd",e);
			}
		}else{
			cache.edit().remove("dat.vcode").remove("dat.minwc").commit();
		}
		if(new File(ctx.getFilesDir(),"mcserverlist/tmp.dex").exists()){
			try {
				List<String> args=new ArrayList();
				args.add("dalvikvm");
				args.add("-classpath");
				args.add(ctx.getApplicationInfo().sourceDir);
				args.add(SlsUpdater.class.getName());
				args.add(ctx.getCacheDir().getAbsolutePath());
				args.add(new File(ctx.getFilesDir(), "mcserverlist/tmp.dex").getAbsolutePath());
				ProcessBuilder pb=new ProcessBuilder(args);
				pb.directory(ctx.getCacheDir());
				Process proc=pb.start();
				BufferedReader br=new BufferedReader(new InputStreamReader(proc.getInputStream()));
				String data=br.readLine();
				br.close();
				DataInputStream dis=new DataInputStream(new ByteArrayInputStream(Base64.decodeBase64(data.getBytes())));
				cache.edit().putString("tmp.vcode",dis.readUTF()).putInt("tmp.minwc",dis.readInt()).commit();
				Log.i("slsupd","data-tmp:"+data);
			} catch (Throwable e) {
				cache.edit().remove("tmp.vcode").remove("tmp.minwc").commit();
				DebugWriter.writeToE("slsupd",e);
			}
		}else{
			cache.edit().remove("tmp.vcode").remove("tmp.minwc").commit();
		}
	}
	public void loadCurrentCode(){
		Intent data=new Intent();
		data.setAction(ctx.replyAction);
		ctx.sendBroadcast(data);//send to the parent process
	}
	public static void loadCurrentCode(Context ctx){
		try {
			DexClassLoader dxl=new DexClassLoader(new File(ctx.getFilesDir(), "mcserverlist/dat.dex").getAbsolutePath(), ctx.getCacheDir().getAbsolutePath(), null, ctx.getClassLoader());
			Class classTodai_ji=dxl.loadClass("com.nao20010128nao.Todai_ji.Providers");
			Object todai_ji=classTodai_ji.newInstance();
			ServerListSite[] services=(ServerListSite[])classTodai_ji.getMethod("getServices").invoke(todai_ji);
			for (ServerListSite sls:services)ServerAddressFetcher.addService(sls);
			Object servDomains;
			try {
				servDomains=classTodai_ji.getMethod("getAddtionalDomains").invoke(todai_ji);
			} catch (Throwable e) {
				servDomains=null;
			}
			if(servDomains==null){
				//do nothing
			}else if(servDomains instanceof List){
				ServerGetActivity.addForServerList=new ArrayList<String>((List<String>)servDomains);
			}else if(servDomains instanceof String[]){
				ServerGetActivity.addForServerList=new ArrayList<>(Arrays.<String>asList((String[])servDomains));
			}
		} catch (Throwable e) {
			DebugWriter.writeToE("slsupd",e);
		}
	}
	public Data getDataFromProvider(Class providerClass){
		try {
			Object prov=providerClass.newInstance();
			Data d=new Data();
			d.vcode = (String)providerClass.getMethod("getVersion").invoke(prov);
			d.minwc = (int)providerClass.getMethod("getWisecraftMinVersion").invoke(prov);
			d.dxl=providerClass.getClassLoader();
			return d;
		} catch (Throwable e) {
			DebugWriter.writeToE("slsupd",e);
		}
		return null;
	}
	
	public class Data{
		public int minwc;
		public String vcode;
		public ClassLoader dxl;
	}
	
	public static void main(String...args)throws Throwable{
		try {
			String cache=args[0];
			String dex=args[1];
			DexClassLoader dxl=new DexClassLoader(dex, cache, null, SlsUpdater.class.getClassLoader());
			Class providerClass=dxl.loadClass("com.nao20010128nao.Todai_ji.Providers");
			Object prov=providerClass.newInstance();
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			DataOutputStream dos=new DataOutputStream(baos);
			dos.writeUTF((String)providerClass.getMethod("getVersion").invoke(prov));
			dos.writeInt((int)providerClass.getMethod("getWisecraftMinVersion").invoke(prov));
			System.out.println(new String(Base64.encodeBase64(baos.toByteArray())).replace("\n", "").replace("\r", ""));
		} catch (Throwable e) {
			DebugWriter.writeToE("slsupd-isolated",e);
		}
	}
}
