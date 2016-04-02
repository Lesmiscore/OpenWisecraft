package com.nao20010128nao.Wisecraft.misc;
import android.content.Context;
import android.net.ConnectivityManager;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.io.File;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

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
		ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(ctx.CONNECTIVITY_SERVICE);
		SharedPreferences appSettings=PreferenceManager.getDefaultSharedPreferences(ctx);
		SharedPreferences cache=ctx.getSharedPreferences("sls_vers_cache",0);
		
		String conName;
		if(cm.getActiveNetworkInfo()==null){
			conName="offline";
		}else{
			conName=cm.getActiveNetworkInfo().getTypeName();
		}
		
		if(conName.equalsIgnoreCase("offline")){
			writeVersions(cache);
			return;
		}
		if(!appSettings.getBoolean("allowAutoUpdateSLSCode",false)){
			writeVersions(cache);
			return;
		}
		if(!appSettings.getBoolean("aausc_monnet",false)){
			if("mobile".equalsIgnoreCase(conName)){
				writeVersions(cache);
				return;
			}
		}
		
		//delete tmp and download the file
		
		writeVersions(cache);
	}
	public void writeVersions(SharedPreferences cache){
		if(new File(ctx.getFilesDir(),"mcserverlist/tmp.dex").exists()){
			try {
				Process proc=new ProcessBuilder().command("dalvikvm", "-classpath", new File(ctx.getFilesDir(), "mcserverlist/tmp.dex").getAbsolutePath(), "com.nao20010128nao.Todai_ji.BeforeCheck").start();
				BufferedReader br=new BufferedReader(new InputStreamReader(proc.getInputStream()));
				String vcode=br.readLine();
				int minWcVers=new Integer(br.readLine());
				cache.edit().putString("tmp.vcode",vcode).putInt("tmp.minwc",minWcVers).apply();
			} catch (IOException e) {
				cache.edit().remove("tmp.vcode").remove("tmp.minwc").apply();
			}
		}else{
			cache.edit().remove("tmp.vcode").remove("tmp.minwc").apply();
		}
		if(new File(ctx.getFilesDir(),"mcserverlist/dat.dex").exists()){
			try {
				Process proc=new ProcessBuilder().command("dalvikvm", "-classpath", new File(ctx.getFilesDir(), "mcserverlist/dat.dex").getAbsolutePath(), "com.nao20010128nao.Todai_ji.BeforeCheck").start();
				BufferedReader br=new BufferedReader(new InputStreamReader(proc.getInputStream()));
				String vcode=br.readLine();
				int minWcVers=new Integer(br.readLine());
				cache.edit().putString("dat.vcode",vcode).putInt("dat.minwc",minWcVers).apply();
			} catch (IOException e) {
				cache.edit().remove("dat.vcode").remove("dat.minwc").apply();
			}
		}else{
			cache.edit().remove("dat.vcode").remove("dat.minwc").apply();
		}
	}
}
