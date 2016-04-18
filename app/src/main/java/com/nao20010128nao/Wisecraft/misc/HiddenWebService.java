package com.nao20010128nao.Wisecraft.misc;
import fi.iki.elonen.NanoHTTPD;
import com.nao20010128nao.Wisecraft.ServerListActivity;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import java.lang.reflect.Field;
import com.nao20010128nao.Wisecraft.Utils;
import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import android.util.Log;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;

public class HiddenWebService extends NanoHTTPD
{
	ServerListActivity.Content act;
	public HiddenWebService(ServerListActivity.Content slal,int port){
		super(port);
		act=slal;
	}

	@Override
	public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
		// TODO: Implement this method
		Response resp=unknownResponse();
		try{
			resp=Utils.requireNonNull(serveInternal(session));
		}catch(Throwable e){
			resp=unknownResponse();
			DebugWriter.writeToE("HiddenWebService",e);
		}
		return resp;
	}
	
	public NanoHTTPD.Response serveInternal(NanoHTTPD.IHTTPSession session) throws Throwable{
		// TODO: Implement this method
		if(session.getUri().equals("/")){
			List<Server> sl=act.getServers();
			Document doc=Jsoup.parse(readAsset("webserver/base.html"));
			Element body=doc.select("body").get(0);
			{
				Element flg=Jsoup.parseBodyFragment(readAsset("webserver/flg_link.html")).select("div#parent").get(0);
				flg.appendElement("a").attr("href","/exit").text("Exit");
				flg.appendElement("a").attr("href","/update").text("Update All");
				body.appendChild(flg);
			}
			body.appendElement("hr");
			for(Server s:sl){
				Document flg_=Jsoup.parseBodyFragment(readAsset("webserver/flg_server.html"));
				Element e=flg_.select("div#parent").get(0);
				e.select("div#parent>div#ip").html(s.ip);
				e.select("div#parent>div#port").html(s.port+"");
				e.select("div#parent>div#online").html((s instanceof ServerStatus)+"");
				body.appendChild(e).appendElement("hr");
			}
			doc.select("html>head>title").html("Wisecraft "+Utils.getVersionName(act));
			return newFixedLengthResponse(doc.html());
		}
		if(session.getUri().equalsIgnoreCase("/exit")){
			try {
				Document doc=Jsoup.parse(readAsset("webserver/base.html"));
				doc.select("html>head>title").html("Wisecraft " + Utils.getVersionName(act));
				doc.select("body").get(0).appendElement("p").text("Exiting application...");
				return newFixedLengthResponse(doc.html());
			} finally {
				act.runOnUiThread(new Runnable(){public void run(){act.execOption(9);}});
			}
		}
		if(session.getUri().equalsIgnoreCase("/update")){
			try {
				Document doc=Jsoup.parse(readAsset("webserver/base.html"));
				doc.select("html>head>title").html("Wisecraft " + Utils.getVersionName(act));
				doc.select("body").get(0).appendElement("p").text("Updating statuses...");
				return newFixedLengthResponse(doc.html());
			} finally {
				act.runOnUiThread(new Runnable(){public void run(){act.execOption(2);}});
			}
		}
		return unknownResponse();
	}
	
	private Response unknownResponse(){
		return newFixedLengthResponse(getStatus("NOT_FOUND"),"text/plain","Not Found");
	}
	
	public Response.IStatus getStatus(String name){
		try {
			for (Field f:Response.Status.class.getFields()) {
				Response.IStatus stat=(Response.IStatus)f.get(null);//AIDE issues
				if (f.getName().equalsIgnoreCase(name)|stat.getDescription().equalsIgnoreCase(name)|String.valueOf(stat.getRequestStatus()).equalsIgnoreCase(name)) {
					return stat;
				}
			}
			return null;
		} catch (Throwable e) {
			return null;
		}
	}
	
	
	private String readAsset(String s)throws IOException{
		BufferedReader br=new BufferedReader(new InputStreamReader(act.getAssets().open(s)));
		StringWriter sw=new StringWriter();
		char[] buf=new char[128];
		int r=0;
		while((r=br.read(buf))<=0)sw.write(buf,0,r);
		br.close();
		return sw.toString();
	}
}
