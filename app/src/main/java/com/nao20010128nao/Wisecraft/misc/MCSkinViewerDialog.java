package com.nao20010128nao.Wisecraft.misc;

import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.os.*;
import android.support.v7.app.*;
import android.support.v7.view.*;
import com.nao20010128nao.Wisecraft.*;
import fi.iki.elonen.*;
import fi.iki.elonen.NanoHTTPD.*;
import java.net.*;
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
		setContentView(R.layout.skin_viewer_dialog);
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public String getPlayer() {
		return player;
	}
	
	private String getBackgroundColor(){
		TypedArray ta=new ContextThemeWrapper(getContext(),ThemePatcher.getDefaultDialogStyle(getContext())).obtainStyledAttributes(new int[]{android.R.id.background});
		int color;
		try{
			color= ta.getColor(0,Color.BLACK);
		}finally{
			ta.recycle();
		}
		return String.format("#%06X", 0xFFFFFF & color);
	}
	
	
	class SkinViewerHttpServer extends NanoHTTPD{
		Context c=getContext();
		public SkinViewerHttpServer(){
			super(65432);
		}

		@Override
		public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
			if(session.getUri().endsWith("/skins/image.png")){
				try{
					return newChunkedResponse(cast(Response.Status.OK),Utils.getMimeType(".png"),new URL("http://crafater.com/skins/"+player).openStream());
				}catch(Throwable e){
					
				}
			}
			if(session.getUri().endsWith("/background.css")){
				StringBuilder css=new StringBuilder();
				css
				.append("baccground{")
				.append("background-color:").append(getBackgroundColor()).append(";")
				.append("}");
				return newFixedLengthResponse(css.toString());
			}
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
