package com.nao20010128nao.Wisecraft.misc;
import android.content.Context;
import android.net.ConnectivityManager;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.io.File;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import com.nao20010128nao.Wisecraft.Utils;
import android.util.Log;
import dalvik.system.DexClassLoader;
import com.nao20010128nao.McServerList.sites.ServerListSite;
import com.nao20010128nao.McServerList.ServerAddressFetcher;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import com.nao20010128nao.Wisecraft.ServerGetActivity;
import java.util.ArrayList;
import java.util.Arrays;

public class SlsUpdater extends Thread
{
	Context ctx;
	public SlsUpdater(Context conte){
		ctx=conte;
		new File(ctx.getFilesDir(),"mcserverlist").mkdirs();
	}
	@Override
	public void run() {
		// TODO: Implement this method
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
					return;
				}
			}

			//delete tmp and download the file
			File tmp=new File(ctx.getFilesDir(), "mcserverlist/tmp.dex");
			File dat=new File(ctx.getFilesDir(), "mcserverlist/dat.dex");
			tmp.delete();
			FileOutputStream fos=null;
			InputStream is=null;
			Data d;
			try {
				fos = new FileOutputStream(dat);
				is = new URL("http://nao20010128nao.github.io/wisecraft/todaiji.dex").openConnection().getInputStream();
				byte[] buf=new byte[1024];
				int r=0;
				while (true) {
					r = is.read(buf);
					if (r <= 0) {
						break;
					}
					fos.write(buf, 0, r);
				}
			} catch (Throwable e) {

			} finally {
				try {
					if (fos != null)fos.close();
					if (is != null)is.close();
				} catch (IOException e) {}
				d=writeVersions(cache);
			}
			if (!(cache.contains("tmp.minwc")|cache.contains("dat.minwc"))) {
				Log.d("slsupd", "broken dex file downloaded");
				loadCurrentCode(d.dxl);
				return;
			}
			if (Utils.getVersionCode(ctx) < cache.getInt("tmp.minwc", 0)) {
				Log.d("slsupd", "unsupported minimum Wisecraft version:" + cache.getInt("tmp.minwc", 0));
				loadCurrentCode(d.dxl);
				return;
			}
			loadCurrentCode(writeVersions(cache).dxl);
		} catch (Throwable e) {
			DebugWriter.writeToE("slsupd",e);
		}
	}
	public Data writeVersions(SharedPreferences cache){
		if(new File(ctx.getFilesDir(),"mcserverlist/dat.dex").exists()){
			try {
				DexClassLoader dxl=new DexClassLoader(new File(ctx.getFilesDir(), "mcserverlist/dat.dex").getAbsolutePath(), ctx.getCacheDir().getAbsolutePath(), null, ctx.getClassLoader());
				Data d=getDataFromProvider(dxl.loadClass("com.nao20010128nao.Todai_ji.Providers"));
				cache.edit().putString("dat.vcode",d.vcode).putInt("dat.minwc",d.minwc).apply();
				d.dxl=dxl;
				return d;
			} catch (Throwable e) {
				cache.edit().remove("dat.vcode").remove("dat.minwc").apply();
				DebugWriter.writeToE("slsupd",e);
			}
		}else{
			cache.edit().remove("dat.vcode").remove("dat.minwc").apply();
		}
		return null;
	}
	public void loadCurrentCode(ClassLoader dxl){
		try {
			//DexClassLoader dxl=new DexClassLoader(new File(ctx.getFilesDir(), "mcserverlist/dat.dex").getAbsolutePath(), ctx.getCacheDir().getAbsolutePath(), null, ctx.getClassLoader());
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
			}else if(servDomains instanceof List<String>){
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
}
