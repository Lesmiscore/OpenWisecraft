package com.nao20010128nao.Wisecraft.misc;

import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.os.*;
import android.support.v7.app.*;
import android.support.v7.view.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.*;
import fi.iki.elonen.*;
import fi.iki.elonen.NanoHTTPD.*;
import java.io.*;
import java.net.*;
import java.util.regex.*;

import android.support.v7.view.ContextThemeWrapper;
public class MCSkinViewerDialog extends AppCompatDialog 
{
	String player;
	FrameLayout progress;
	WebView skinViewer;
	SkinViewerHttpServer server;
	Handler h=new Handler();
	public MCSkinViewerDialog(android.content.Context context) {
		this(context,ThemePatcher.getDefaultDialogStyle(context));
	}

    public MCSkinViewerDialog(android.content.Context context, int theme) {
		super(context,theme);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.skin_viewer_dialog);
		progress=(FrameLayout)findViewById(R.id.loading);
		skinViewer=(WebView)findViewById(R.id.skinViewingWebView);
		
		try {
			(server=new SkinViewerHttpServer()).start(5000, true);
			skinViewer.setWebViewClient(new WebViewClient(){});
			skinViewer.getSettings().setJavaScriptEnabled(true);
			skinViewer.loadUrl("http://localhost:65432/index.html");
		} catch (IOException e) {
			WisecraftError.report("MCSkinViewerDialog",e);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if(server!=null)server.stop();
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
		boolean webGlChecked=false;
		public SkinViewerHttpServer(){
			super(65432);
		}

		@Override
		public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
			if(session.getUri().endsWith("/xhr/webgl_available")&!webGlChecked){
				// Web GL is available so show WebView
				h.post(new Runnable(){
						public void run(){
							progress.setVisibility(View.GONE);
							skinViewer.setVisibility(View.VISIBLE);
						}
					});
				webGlChecked=true;
				return newFixedLengthResponse("");
			}
			if(session.getUri().endsWith("/xhr/webgl_bad")&!webGlChecked){
				// Web GL is NOT available so we use another browser
				h.post(new Runnable(){
						public void run(){
							progress.setVisibility(View.GONE);
							skinViewer.setVisibility(View.VISIBLE);
						}
					});
				webGlChecked=true;
				return newFixedLengthResponse("");
			}
			if(session.getUri().endsWith("/skins/image.png")){
				try{
					return newChunkedResponse(cast(Response.Status.OK),Utils.getMimeType(".png"),new URL("https://crafatar.com/skins/"+player).openStream());
				}catch(Throwable e){
					DebugWriter.writeToE("MCSkinViewerDialog",e);
				}
			}
			if(session.getUri().endsWith("/background.css")){
				StringBuilder css=new StringBuilder();
				css
				.append("body{")
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
				DebugWriter.writeToE("MCSkinViewerDialog",e);
			}
			return newFixedLengthResponse(cast(Response.Status.NOT_FOUND),"application/octet-stream","");
		}
	}
	
	Response.IStatus cast(Response.Status stat){
		return (Response.IStatus)((Object)stat);
	}
}
