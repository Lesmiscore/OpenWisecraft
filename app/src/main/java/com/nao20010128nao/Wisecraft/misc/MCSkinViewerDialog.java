package com.nao20010128nao.Wisecraft.misc;

import android.content.*;
import android.os.*;
import android.support.v7.app.*;
import fi.iki.elonen.*;
import fi.iki.elonen.NanoHTTPD.*;
import java.util.regex.*;

public class MCSkinViewerDialog extends AppCompatDialog 
{
	String player;
	public MCSkinViewerDialog(android.content.Context context) {
		this(context,ThemePatcher.getDefaultDialogStyle(context));
	}

    public MCSkinViewerDialog(android.content.Context context, int theme) {
		super(context,theme);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public String getPlayer() {
		return player;
	}
	
	
	class SkinViewerHttpServer extends NanoHTTPD{
		Context c=getContext();
		public SkinViewerHttpServer(){
			super(65432);
		}

		@Override
		public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
			try{
				String path;
				if(session.getUri().startsWith("/")){
					path="3dskin"+session.getUri();
				}else{
					path="3dskin/"+session.getUri();
				}
				path=path.split(Pattern.quote("&"))[0];
				return newChunkedResponse(cast(Response.Status.OK),Utils.getMimeType(path),c.getAssets().open(path));
			}catch(Throwable e){
				
			}
			return newFixedLengthResponse(cast(Response.Status.NOT_FOUND),"application/octet-stream","");
		}
	}
	
	Response.IStatus cast(Response.Status stat){
		return (Response.IStatus)((Object)stat);
	}
}
