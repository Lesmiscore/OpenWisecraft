package com.nao20010128nao.Wisecraft;
import android.content.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.misc.*;
import java.util.*;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import com.nao20010128nao.Wisecraft.misc.compat.AppCompatAlertDialog;
import com.nao20010128nao.Wisecraft.misc.compat.AppCompatListActivity;
import com.nao20010128nao.Wisecraft.misc.pinger.pc.Reply;
import com.nao20010128nao.Wisecraft.misc.pinger.pc.Reply19;
import com.nao20010128nao.Wisecraft.misc.pinger.pe.FullStat;
import com.nao20010128nao.Wisecraft.pingEngine.UnconnectedPing;
import com.nao20010128nao.Wisecraft.provider.NormalServerPingProvider;
import com.nao20010128nao.Wisecraft.provider.ServerPingProvider;
import java.lang.ref.WeakReference;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.nao20010128nao.Wisecraft.Utils.*;

public class ServerTestActivity extends AppCompatListActivity {
	static WeakReference<ServerTestActivity> instance=new WeakReference(null);
	
	ServerPingProvider spp=new NormalServerPingProvider();
	ServerList sl;
	List<Server> list;
	int clicked=-1;
	ProgressDialog waitDialog;
	int times,port;
	String ip;
	int mode;
	View dialog;
	Map<Integer,Boolean> pinging=new HashMap<Integer,Boolean>(){
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
		boolean usesOldInstance=false;
		if(instance.get()!=null){
			sl=instance.get().sl;
			pinging=instance.get().pinging;
			usesOldInstance=true;
		}else{
			sl=new ServerList(this);
		}
		instance=new WeakReference(this);
		setListAdapter(sl);
		getListView().setOnItemClickListener(sl);
		ip = getIntent().getStringExtra("ip");
		port = getIntent().getIntExtra("port", -1);
		mode = getIntent().getIntExtra("ispc", 0);
		if(usesOldInstance&sl.getCount()!=0){
			
		}else{
			new AppCompatAlertDialog.Builder(this,R.style.AppAlertDialog)
				.setTitle(R.string.testServer)
				.setView(dialog = getLayoutInflater().inflate(R.layout.test_server_dialog, null, false))
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface di, int w) {
						di.dismiss();
						String nu=((EditText)dialog.findViewById(R.id.pingTimes)).getText().toString();
						try {
							times = new Integer(nu);
						} catch (NumberFormatException e) {
							finish();
						}
						for (int i=0;i < times;i++) {
							Server s=new Server();
							s.ip = ip;
							s.port = port;
							s.mode = mode;
							sl.add(s);
							sl.getViewQuick(i);
						}
					}
				})
				.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface di, int w) {
						di.dismiss();
						finish();
					}
				})
				.setOnCancelListener(new DialogInterface.OnCancelListener(){
					public void onCancel(DialogInterface di) {
						finish();
					}
				})
				.show();
		}
	}
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}

	@Override
	public void onBackPressed() {
		// TODO: Implement this method
		super.onBackPressed();
		instance=new WeakReference(null);
	}
	
	static class ServerList extends AppBaseArrayAdapter<Server> implements AdapterView.OnItemClickListener {
		List<View> cached=new ArrayList();
		ServerTestActivity sta;
		
		public ServerList(ServerTestActivity parent) {
			super(parent, 0, parent.list = new ArrayList<Server>());
			sta=parent;
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
			final View layout=sta.getLayoutInflater().inflate(R.layout.quickstatus, null, false);
			Server s=getItem(position);
			layout.setTag(s);
			sta.spp.putInQueue(s, new ServerPingProvider.PingHandler(){
					public void onPingFailed(final Server s) {
						sta.runOnUiThread(new Runnable(){
								public void run() {
									((ImageView)layout.findViewById(R.id.statColor)).setImageDrawable(new ColorDrawable(sta.getResources().getColor(R.color.stat_error)));
									((TextView)layout.findViewById(R.id.serverName)).setText(s.ip + ":" + s.port);
									((TextView)layout.findViewById(R.id.pingMillis)).setText(R.string.notResponding);
									sta.pinging.put(position, false);
								}
							});
					}
					public void onPingArrives(final ServerStatus sv) {
						sta.runOnUiThread(new Runnable(){
								public void run() {
									((ImageView)layout.findViewById(R.id.statColor)).setImageDrawable(new ColorDrawable(sta.getResources().getColor(R.color.stat_ok)));
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
									} else if (sv.response instanceof Reply19) {//PC 1.9~
										Reply19 rep=(Reply19)sv.response;
										if (rep.description == null) {
											title = sv.ip + ":" + sv.port;
										} else {
											title = deleteDecorations(rep.description.text);
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
									sta.list.set(position, sv);
									sta.pinging.put(position, false);
								}
							});
					}
				});
			((TextView)layout.findViewById(R.id.serverName)).setText(R.string.working);
			((TextView)layout.findViewById(R.id.pingMillis)).setText(R.string.working);
			((TextView)layout.findViewById(R.id.serverAddress)).setText(s.ip + ":" + s.port);
			((ImageView)layout.findViewById(R.id.statColor)).setImageDrawable(new ColorDrawable(sta.getResources().getColor(R.color.stat_pending)));
			if (cached.size() <= position) {
				cached.addAll(Constant.ONE_HUNDRED_LENGTH_NULL_LIST);
			}
			cached.set(position, layout);
			sta.pinging.put(position, true);
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
			sta.clicked = p3;
			if (s instanceof ServerStatus) {
				ServerInfoActivity.stat.add((ServerStatus)s);
				int ofs=ServerInfoActivity.stat.indexOf(s);
				sta.startActivityForResult(new Intent(sta, ServerInfoActivity.class).putExtra("nonUpd", true).putExtra("statListOffset",ofs), 0);
			}
		}

		@Override
		public void remove(Server object) {
			// TODO: Implement this method
			cached.remove(sta.list.indexOf(object));
			super.remove(object);
		}
		
		public void attachNewActivity(ServerTestActivity newSta){
			sta=newSta;
		}
	}
}
