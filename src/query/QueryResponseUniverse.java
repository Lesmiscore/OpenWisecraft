package query;

import java.util.ArrayList;
import java.util.*;
import ch.jamiete.mcping.*;

public class QueryResponseUniverse {
	static byte NULL = 00;
	static byte SPACE = 20;

	private boolean fullstat=true;

	private Map<String,String> datas=new HashMap<>();
	private ArrayList<String> playerList=new ArrayList<>();

	public QueryResponseUniverse(byte[] data) {
		data = ByteUtils.trim(data);
		byte[][] temp = ByteUtils.split(data);
		byte[] d;
		int dataEnds=0;
		for(int i=2;i<temp.length;i+=1){
			if((d = temp[i]).length == 0 ?false: d[0] == 1){
				dataEnds=i;
				break;
			}
		}
		
		if((dataEnds%2)==0)dataEnds--;
		
		for(int i=2;i<dataEnds;i+=2){
			String k=new String(temp[i]);
			String v=new String(temp[i+1]);
			if("".equals(k)|"".equals(v)){
				continue;
			}
			datas.put(k,v);
		}
		
		playerList = new ArrayList<String>();//
		for (int i=dataEnds+2; i < temp.length; i++) {
			playerList.add(new String(temp[i]));
		}
	}
	public QueryResponseUniverse(MinecraftPingReply reply){
		datas.put("description",reply.getDescription());
		datas.put("icon",reply.getFavicon());
		datas.put("version",reply.getVersion().getName());
		datas.put("protocol",reply.getVersion().getProtocol()+"");
		datas.put("maxplayers",reply.getPlayers().getMax()+"");
		datas.put("numplayers",reply.getPlayers().getOnline()+"");
		if(reply.getPlayers().getSample()!=null)
			for(MinecraftPingReply.Player p:reply.getPlayers().getSample())
				playerList.add(p.getName());
	}

	public Map<String,String> getData() {
		return Collections.unmodifiableMap(datas);
	}
	public List<String> getPlayerList() {
		return Collections.unmodifiableList(playerList);
	}

	//TODO getPlayers return hashmap/array/arraylist
}
