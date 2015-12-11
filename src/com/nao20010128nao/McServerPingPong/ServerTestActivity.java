package com.nao20010128nao.McServerPingPong;
import android.app.*;
import android.content.*;
import android.graphics.drawable.*;
import android.os.*;
import android.preference.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.google.gson.*;
import java.io.*;
import java.util.*;
import query.*;
import com.nao20010128nao.McServerPingPong.ServerListActivity.*;

public class ServerTestActivity extends ListActivity{
	ServerPingProvider spp=new NormalServerPingProvider();
	ServerList sl;
	List<Server> list;
	int clicked=-1;
	ProgressDialog waitDialog;
	int times,port;
	String ip;
	Map<Server,Boolean> pinging=new HashMap<Server,Boolean>(){
		@Override
		public Boolean get(Object key) {
			// TODO: Implement this method
			Boolean b= super.get(key);
			if(b==null){
				return false;
			}
			return b;
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setListAdapter(sl=new ServerList());
		getListView().setOnItemClickListener(sl);
		ip=getIntent().getStringExtra("ip");
		port=getIntent().getIntExtra("port",-1);
		times=getIntent().getIntExtra("times",-1);
		for(int i=0;i<times;i++){
			Server s=new Server();
			s.ip=ip;
			s.port=port;
			sl.add(s);
		}
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
	public void showWorkingDialog(){
		if(waitDialog!=null){
			hideWorkingDialog();
		}
		waitDialog= new ProgressDialog(this);
		waitDialog.setIndeterminate(true);
		waitDialog.setMessage(getResources().getString(R.string.working));
		waitDialog.setCancelable(false);
		waitDialog.show();
	}
	public void hideWorkingDialog(){
		if(waitDialog==null){
			return;
		}
		waitDialog.cancel();
		waitDialog=null;
	}
	class ServerList extends ArrayAdapter<Server> implements AdapterView.OnItemClickListener{
		List<View> cached=new ArrayList();
		public ServerList(){
			super(ServerTestActivity.this,0,list=new ArrayList<Server>());
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO: Implement this method
			if(cached.size()>position)return cached.get(position);
			//if(convertView!=null)return convertView;
			final View layout=getLayoutInflater().inflate(R.layout.quickstatus,null,false);
			Server s=getItem(position);
			layout.setTag(s);
			spp.putInQueue(s,new ServerPingProvider.PingHandler(){
					public void onPingFailed(final Server s){
						runOnUiThread(new Runnable(){
								public void run(){
									layout.findViewById(R.id.statColor).setBackground(new ColorDrawable(getResources().getColor(R.color.stat_error)));
									((TextView)layout.findViewById(R.id.serverName)).setText(s.ip+":"+s.port);
									((TextView)layout.findViewById(R.id.pingMillis)).setText(R.string.notResponding);
									pinging.put(s,false);
								}
							});
					}
					public void onPingArrives(final ServerStatus sv){
						runOnUiThread(new Runnable(){
								public void run(){
									int position=list.indexOf(sv);
									if(position==-1){
										return;
									}
									layout.findViewById(R.id.statColor).setBackground(new ColorDrawable(getResources().getColor(R.color.stat_ok)));
									final String title;
									Map<String,String> m=sv.response.getData();
									if (m.containsKey("hostname")) {
										title = deleteDecorations(m.get("hostname"));
									} else if (m.containsKey("motd")) {
										title = deleteDecorations(m.get("motd"));
									} else {
										title = sv.ip + ":" + sv.port;
									}
									((TextView)layout.findViewById(R.id.serverName)).setText(deleteDecorations(title));
									((TextView)layout.findViewById(R.id.pingMillis)).setText(sv.ping+" ms");
									list.set(position,sv);
									pinging.put(sv,false);
								}
							});
					}
				});
			((TextView)layout.findViewById(R.id.serverName)).setText(R.string.working);
			((TextView)layout.findViewById(R.id.pingMillis)).setText(R.string.working);
			((TextView)layout.findViewById(R.id.serverAddress)).setText(s.ip+":"+s.port);
			layout.findViewById(R.id.statColor).setBackground(new ColorDrawable(getResources().getColor(R.color.stat_pending)));
			if(cached.size()<=position){
				cached.add(layout);
			}else{
				cached.set(position,layout);
			}
			pinging.put(s,true);
			return layout;
		}
		public View getCachedView(int position){
			return cached.get(position);
		}
		public View getViewQuick(int pos){
			return getView(pos,null,null);
		}
		@Override
		public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
			// TODO: Implement this method
			Server s=getItem(p3);
			clicked=p3;
			if(s instanceof ServerStatus){
				ServerInfoActivity.stat=(ServerStatus)s;
				startActivityForResult(new Intent(ServerTestActivity.this,ServerInfoActivity.class),0);
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

		@Override
		public void remove(ServerListActivity.Server object) {
			// TODO: Implement this method
			cached.remove(list.indexOf(object));
			super.remove(object);
		}
	}
}
