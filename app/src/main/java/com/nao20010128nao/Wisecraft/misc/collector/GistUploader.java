package com.nao20010128nao.Wisecraft.misc.collector;
import android.content.*;
import android.util.*;
import com.google.firebase.remoteconfig.*;
import com.google.gson.annotations.*;
import com.google.gson.reflect.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import java.util.*;
import org.eclipse.egit.github.core.*;
import org.eclipse.egit.github.core.client.*;
import org.eclipse.egit.github.core.service.*;

public class GistUploader extends ContextWrapper implements CollectorMainUploaderProvider {
	
	boolean inited=false;
	Context ctx=this;
	
	SharedPreferences sb;
	String uuid;
	FirebaseRemoteConfig frc;
	GitHubClient ghc;
	Repository repo;
	List<RepositoryContents> cont;
	
	public GistUploader(){
		super(TheApplication.instance);
	}
	
	@Override
	public boolean isAvailable() throws Throwable {
		initLocal();
		return inited&&(TheApplication.instance.fbCfgLoader.isSuccessful()&&ghc.getRemainingRequests()!=0);
	}

	@Override
	public CollectorMainUploaderProvider.Interface forInterface() {
		return new Interface();
	}
	
	void initLocal(){
		if(!TheApplication.instance.fbCfgLoader.isSuccessful())return;
		if(inited)return;
		inited=true;
		uuid=TheApplication.instance.uuid;
		sb=getSharedPreferences("majeste",MODE_PRIVATE);
		frc=TheApplication.instance.firebaseRemoteCfg;
		frc.activateFetched();
		ghc=new GitHubClient().setCredentials(frc.getString("information_upload_user"), frc.getString("information_upload_pass"));
		try {
			repo = new RepositoryService(ghc).getRepository(frc.getString("information_upload_host_user"), frc.getString("information_upload_host_name"));
			cont = new ContentsService(ghc).getContents(repo);
			Log.d("GistUploader", "initLocal OK");
		} catch (Throwable e) {
			DebugWriter.writeToE("GistUploader",e);
			Log.d("GistUploader", "de-initing...");
			inited=false;
			return;
		}
	}
	
	class Interface implements CollectorMainUploaderProvider.Interface {

		@Override
		public void init() throws Throwable {
			initLocal();
			if(!inited)throw new Throwable("init error");
		}

		@Override
		public boolean doUpload(String uuid, String filename, String content) throws Throwable {
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
					return false;
				}
				params.put("content", Base64.encodeToString(file, Base64.NO_WRAP));
				ghc.put("/repos/"+frc.getString("information_upload_host_user")+"/"+frc.getString("information_upload_host_name")+"/contents/" + filename, params, TypeToken.get(ContentUpload.class).getType());
				Log.d("CollectorMain", "uploaded");
				return true;
			} catch (Throwable e) {
				DebugWriter.writeToE("CollectorMain",e);
				if(e.getMessage().contains("\"sha\" wasn't supplied"))return true;//let CollectorMain delete entry
				return false;
			}
		}
		
		private String getHash(List<RepositoryContents> cont, String filename) {
			for (RepositoryContents o:cont)
				if (o.getName().equalsIgnoreCase(filename))
					return o.getSha();
			return null;
		}
	}
	
	public static class ContentUpload {
		@SerializedName("content")
		public RepositoryContents content;
		@SerializedName("commit")
		public RepositoryCommit commit;
		@SerializedName("parents")
		public RepositoryCommit[] parents;
	}
}
