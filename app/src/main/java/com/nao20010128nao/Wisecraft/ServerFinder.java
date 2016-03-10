package com.nao20010128nao.Wisecraft;
import android.app.*;
import android.content.*;
import android.widget.*;
import java.util.*;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import com.nao20010128nao.MCPing.pc.Reply;
import com.nao20010128nao.MCPing.pe.FullStat;
import com.nao20010128nao.Wisecraft.ServerListActivity.Server;
import com.nao20010128nao.Wisecraft.ServerListActivity.ServerStatus;
import com.nao20010128nao.Wisecraft.misc.AppBaseArrayAdapter;
import com.nao20010128nao.Wisecraft.misc.SprPair;
import com.nao20010128nao.Wisecraft.pingEngine.UnconnectedPing;
import com.nao20010128nao.Wisecraft.provider.NormalServerPingProvider;
import com.nao20010128nao.Wisecraft.provider.ServerPingProvider;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.nao20010128nao.Wisecraft.Utils.*;
public class ServerFinder extends ListActivity
{
	ServerList sl;
	List<Server> list;
	String ip;
	boolean isPC;
	View dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setListAdapter(sl = new ServerList());
		getListView().setOnItemClickListener(sl);
		ip = getIntent().getStringExtra("ip");
		isPC = getIntent().getBooleanExtra("ispc", false);
		new AlertDialog.Builder(this)
			.setTitle(R.string.testServer)
			.setView(dialog = getLayoutInflater().inflate(R.layout.server_finder_start, null, false))
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface di, int w) {
					String ip=((EditText)dialog.findViewById(R.id.ip)).getText().toString();
					int startPort=new Integer(((EditText)dialog.findViewById(R.id.startPort)).getText().toString());
					int endPort=new Integer(((EditText)dialog.findViewById(R.id.endPort)).getText().toString());
					boolean isPC=((CheckBox)dialog.findViewById(R.id.pc)).isChecked();
					startFinding(ip,startPort,endPort,isPC);
				}
			})
			.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface di, int w) {
					di.dismiss();
					finish();
				}
			})
			.setCancelable(false)
			.show();
		if(ip!=null)((EditText)dialog.findViewById(R.id.ip)).setText(ip);
	}
	private void startFinding(String ip,int startPort,int endPort,boolean isPC){
		
	}
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}
	class ServerList extends AppBaseArrayAdapter<ServerStatus> implements AdapterView.OnItemClickListener {
		List<View> cached=new ArrayList();
		public ServerList() {
			super(ServerFinder.this, 0, list = new ArrayList<Server>());
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO: Implement this method
			if (cached.size() > position) {
				View v=cached.get(position);
				if (v != null) {
					return v;
				}
			}
			//if(convertView!=null)return convertView;
			final View layout=getLayoutInflater().inflate(R.layout.quickstatus, null, false);
			ServerStatus s=getItem(position);
			layout.setTag(s);
			((ImageView)layout.findViewById(R.id.statColor)).setImageDrawable(new ColorDrawable(getResources().getColor(R.color.stat_ok)));
			
			final String title;
			if (s.response instanceof Reply) {//PC
				Reply rep=(Reply)s.response;
				if (rep.description == null) {
					title = s.ip + ":" + s.port;
				} else {
					title = deleteDecorations(rep.description);
				}
			} else if (s.response instanceof UnconnectedPing.UnconnectedPingResult) {
				title = ((UnconnectedPing.UnconnectedPingResult)s.response).getServerName();
			} else {//Unreachable
				title = s.ip + ":" + s.port;
			}
			((TextView)layout.findViewById(R.id.serverName)).setText(deleteDecorations(title));
			((TextView)layout.findViewById(R.id.pingMillis)).setText(s.ping + " ms");
			list.set(position, s);
									
			if (cached.size() <= position) {
				cached.addAll(Constant.ONE_HUNDRED_LENGTH_NULL_LIST);
			}
			cached.set(position, layout);
			return layout;
		}
		public View getCachedView(int position) {
			return cached.get(position);
		}
		public View getViewQuick(int pos) {
			return getView(pos, null, null);
		}
		@Override
		public void onItemClick(AdapterView<?> p1, View p2, final int p3, long p4) {
			// TODO: Implement this method
			final Server s=getItem(p3);
			if (s instanceof ServerStatus) {
				
			}
		}

		@Override
		public void remove(ServerListActivity.ServerStatus object) {
			// TODO: Implement this method
			cached.remove(list.indexOf(object));
			super.remove(object);
		}
	}
}
