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
	ServerPingProvider spp=new NormalServerPingProvider();
	ServerList sl;
	List<Server> list;
	int clicked=-1;
	ProgressDialog waitDialog;
	int times,port;
	String ip;
	boolean isPC;
	View dialog;
	Map<Server,Boolean> pinging=new HashMap<Server,Boolean>(){
		@Override
		public Boolean get(Object key) {
			// TODO: Implement this method
			Boolean b= super.get(key);
			if (b == null) {
				return false;
			}
			return b;
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setListAdapter(sl = new ServerList());
		getListView().setOnItemClickListener(sl);
		ip = getIntent().getStringExtra("ip");
		port = getIntent().getIntExtra("port", -1);
		isPC = getIntent().getBooleanExtra("ispc", false);
		new AlertDialog.Builder(this)
			.setTitle(R.string.testServer)
			.setView(dialog = getLayoutInflater().inflate(R.layout.test_server_dialog, null, false))
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface di, int w) {
					di.dismiss();
					String nu=((EditText)dialog.findViewById(R.id.pingTimes)).getText().toString();
					times = new Integer(nu);
					for (int i=0;i < times;i++) {
						Server s=new Server();
						s.ip = ip;
						s.port = port;
						s.isPC = isPC;
						sl.add(s);
					}
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
	}
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}
	class ServerList extends AppBaseArrayAdapter<Server> implements AdapterView.OnItemClickListener {
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
			Server s=getItem(position);
			layout.setTag(s);
			spp.putInQueue(s, new ServerPingProvider.PingHandler(){
					public void onPingFailed(final Server s) {
						runOnUiThread(new Runnable(){
								public void run() {
									((ImageView)layout.findViewById(R.id.statColor)).setImageDrawable(new ColorDrawable(getResources().getColor(R.color.stat_error)));
									((TextView)layout.findViewById(R.id.serverName)).setText(s.ip + ":" + s.port);
									((TextView)layout.findViewById(R.id.pingMillis)).setText(R.string.notResponding);
									pinging.put(s, false);
								}
							});
					}
					public void onPingArrives(final ServerStatus sv) {
						runOnUiThread(new Runnable(){
								public void run() {
									((ImageView)layout.findViewById(R.id.statColor)).setImageDrawable(new ColorDrawable(getResources().getColor(R.color.stat_ok)));
									final String title;
									if (sv.response instanceof FullStat) {//PE
										FullStat fs=(FullStat)sv.response;
										Map<String,String> m=fs.getData();
										if (m.containsKey("hostname")) {
											title = deleteDecorations(m.get("hostname"));
										} else if (m.containsKey("motd")) {
											title = deleteDecorations(m.get("motd"));
										} else {
											title = sv.ip + ":" + sv.port;
										}
									} else if (sv.response instanceof Reply) {//PC
										Reply rep=(Reply)sv.response;
										if (rep.description == null) {
											title = sv.ip + ":" + sv.port;
										} else {
											title = deleteDecorations(rep.description);
										}
									} else if (sv.response instanceof SprPair) {//PE?
										SprPair sp=((SprPair)sv.response);
										if (sp.getB() instanceof UnconnectedPing.UnconnectedPingResult) {
											title = ((UnconnectedPing.UnconnectedPingResult)sp.getB()).getServerName();
										} else if (sp.getA() instanceof FullStat) {
											FullStat fs=(FullStat)sp.getA();
											Map<String,String> m=fs.getData();
											if (m.containsKey("hostname")) {
												title = deleteDecorations(m.get("hostname"));
											} else if (m.containsKey("motd")) {
												title = deleteDecorations(m.get("motd"));
											} else {
												title = sv.ip + ":" + sv.port;
											}
										} else {
											title = sv.ip + ":" + sv.port;
										}
									} else if (sv.response instanceof UnconnectedPing.UnconnectedPingResult) {
										title = ((UnconnectedPing.UnconnectedPingResult)sv.response).getServerName();
									} else {//Unreachable
										title = sv.ip + ":" + sv.port;
									}
									((TextView)layout.findViewById(R.id.serverName)).setText(deleteDecorations(title));
									((TextView)layout.findViewById(R.id.pingMillis)).setText(sv.ping + " ms");
									list.set(position, sv);
									pinging.put(sv, false);
								}
							});
					}
				});
			((TextView)layout.findViewById(R.id.serverName)).setText(R.string.working);
			((TextView)layout.findViewById(R.id.pingMillis)).setText(R.string.working);
			((TextView)layout.findViewById(R.id.serverAddress)).setText(s.ip + ":" + s.port);
			((ImageView)layout.findViewById(R.id.statColor)).setImageDrawable(new ColorDrawable(getResources().getColor(R.color.stat_pending)));
			if (cached.size() <= position) {
				cached.addAll(Constant.ONE_HUNDRED_LENGTH_NULL_LIST);
			}
			cached.set(position, layout);
			pinging.put(s, true);
			return layout;
		}
		public View getCachedView(int position) {
			return cached.get(position);
		}
		public View getViewQuick(int pos) {
			return getView(pos, null, null);
		}
		@Override
		public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
			// TODO: Implement this method
			Server s=getItem(p3);
			clicked = p3;
			if (s instanceof ServerStatus) {
				ServerInfoActivity.stat = (ServerStatus)s;
				startActivityForResult(new Intent(ServerFinder.this, ServerInfoActivity.class).putExtra("nonUpd", true), 0);
			}
		}

		@Override
		public void remove(ServerListActivity.Server object) {
			// TODO: Implement this method
			cached.remove(list.indexOf(object));
			super.remove(object);
		}
	}
}
