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
			Document doc=Jsoup.parse("<!doctype html><html><head><title></title></head><body></body></html>");
			for(Server s:sl){
				Document flg_=Jsoup.parseBodyFragment("<p>IP:<div id=\"ip\"></div><br>Port:<div id=\"port\"></div><br>Is Online:<div id=\"online\"></div></p>");
				Element e=flg_.select("p").get(0);
				e.select("p>div#ip").html(s.ip);
				e.select("p>div#port").html(s.port+"");
				e.select("p>div#online").html((s instanceof ServerStatus)+"");
				doc.select("body").get(0).appendChild(e);
			}
			doc.select("html>head>title").html("Wisecraft "+Utils.getVersionName(act));
			return newFixedLengthResponse(doc.html());
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
}
