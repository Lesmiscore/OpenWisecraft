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
	}
	public void loadServers(){
		Server[] sa=gson.fromJson(pref.getString("servers","[]"),Server[].class);
		sl.clear();
		sl.addAll(sa);
	}
	public void saveServers(){
		pref.edit().putString("servers",gson.toJson(list.toArray(new Server[list.size()]),Server[].class)).commit();
	}
	class ServerList extends ArrayAdapter<Server> implements AdapterView.OnItemClickListener{
		public ServerList(){
			super(ServerListActivity.this,0,list=new ArrayList<Server>());
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO: Implement this method
			View layout=getLayoutInflater().inflate(R.layout.quickstatus,null,false);
			layout.setTag(getItem(position));
			return layout;
		}

		@Override
		public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
			// TODO: Implement this method
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
