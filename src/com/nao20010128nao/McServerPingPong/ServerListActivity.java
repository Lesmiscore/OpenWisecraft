package com.nao20010128nao.McServerPingPong;
import android.app.*;
import android.os.*;
import android.widget.*;
import java.util.*;
import query.*;
import android.view.*;
import com.google.gson.*;
import android.content.*;
import android.preference.*;
import android.graphics.drawable.*;
import android.util.*;

public class ServerListActivity extends ListActivity{
	ServerPingProvider spp=new ServerPingProvider();
	Gson gson=new Gson();
	SharedPreferences pref;
	ServerList sl;
	List<Server> list;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setListAdapter(sl=new ServerList());
		getListView().setOnItemClickListener(sl);
		pref=PreferenceManager.getDefaultSharedPreferences(this);
		loadServers();
		/*Server s=new Server();
		s.ip="setsuna.info";
		s.port=19132;
		sl.add(s);
		s=new Server();
		s.ip="sg.lbsg.net";
		s.port=19132;
		sl.add(s);*/
	}
	@Override
	protected void onDestroy() {
		// TODO: Implement this method
		super.onDestroy();
		saveServers();
	}
	public void loadServers(){
		Server[] sa=gson.fromJson(pref.getString("servers","[]"),Server[].class);
		sl.clear();
		sl.addAll(sa);
	}
	public void saveServers(){
		String json;
		pref.edit().putString("servers",json=gson.toJson(list.toArray(new Server[list.size()]),Server[].class)).commit();
		Log.d("json",json);
	}
	static String deleteDecorations(String decorated) {
		StringBuilder sb=new StringBuilder();
		char[] chars=decorated.toCharArray();
		int offset=0;
		while (chars.length > offset) {
			if (chars[offset] == '§') {
				offset += 2;
				continue;
			}
			sb.append(chars[offset]);
			offset++;
		}
		Log.d("esc", sb.toString());
		return sb.toString();
	}
	class ServerList extends ArrayAdapter<Server> implements AdapterView.OnItemClickListener{
		List<View> cached=new ArrayList();
		public ServerList(){
			super(ServerListActivity.this,0,list=new ArrayList<Server>());
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO: Implement this method
			final View layout=getLayoutInflater().inflate(R.layout.quickstatus,null,false);
			Server s=getItem(position);
			layout.setTag(s);
			spp.putInQueue(s,new ServerPingProvider.PingHandler(){
				public void onPingFailed(Server s){
					runOnUiThread(new Runnable(){
							public void run(){
								layout.findViewById(R.id.statColor).setBackground(new ColorDrawable(getResources().getColor(R.color.stat_error)));
								}
							});
				}
				public void onPingArrives(final ServerStatus s){
					runOnUiThread(new Runnable(){
						public void run(){
							layout.findViewById(R.id.statColor).setBackground(new ColorDrawable(getResources().getColor(R.color.stat_ok)));
							final String title;
							Map<String,String> m=s.response.getData();
							if (m.containsKey("hostname")) {
								title = deleteDecorations(m.get("hostname"));
							} else if (m.containsKey("motd")) {
								title = deleteDecorations(m.get("motd"));
							} else {
								title = s.ip + ":" + s.port;
							}
							((TextView)layout.findViewById(R.id.serverName)).setText(deleteDecorations(title));
							((TextView)layout.findViewById(R.id.pingMillis)).setText(s.ping+" ms");
							list.set(position,s);
						}
					});
				}
			});
			((TextView)layout.findViewById(R.id.serverName)).setText(R.string.working);
			((TextView)layout.findViewById(R.id.pingMillis)).setText(R.string.working);
			((TextView)layout.findViewById(R.id.serverAddress)).setText(s.ip+":"+s.port);
			layout.findViewById(R.id.statColor).setBackground(new ColorDrawable(getResources().getColor(R.color.stat_pending)));
			if(cached.size()<position){
				cached.add(layout);
			}else{
				cached.set(position,layout);
			}
			return layout;
		}
		public View getCachedView(int position){
			return cached.get(position);
		}
		@Override
		public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
			// TODO: Implement this method
			Server s=getItem(p3);
			if(s instanceof ServerStatus){
				ServerInfoActivity.stat=(ServerStatus)s;
			}
		}

		@Override
		public void add(ServerListActivity.Server object) {
			// TODO: Implement this method
			if(!list.contains(object))super.add(object);
		}

		@Override
		public void addAll(ServerListActivity.Server[] items) {
			// TODO: Implement this method
			for(Server s:items)add(s);
		}

		@Override
		public void addAll(Collection<? extends ServerListActivity.Server> collection) {
			// TODO: Implement this method
			for(Server s:collection)add(s);
		}
	}
	public static class Server{
		public String ip;
		public int port;

		@Override
		public int hashCode() {
			// TODO: Implement this method
			return ip.hashCode()^port;
		}

		@Override
		public boolean equals(Object o) {
			// TODO: Implement this method
			if(!(o instanceof Server)){
				return false;
			}
			Server os=(Server)o;
			return os.ip.equals(ip)&os.port==port;
		}
	}
	public static class ServerStatus extends Server{
		public QueryResponseUniverse response;
		public long ping;

		@Override
		public int hashCode() {
			// TODO: Implement this method
			return super.hashCode()^((Long)ping).hashCode()^response.hashCode();
		}
	}
}
