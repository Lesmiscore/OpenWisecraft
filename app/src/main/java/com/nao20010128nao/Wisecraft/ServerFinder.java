package com.nao20010128nao.Wisecraft;
import android.app.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.provider.*;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import com.nao20010128nao.MCPing.pc.Reply;
import com.nao20010128nao.MCPing.pc.Reply19;
import com.nao20010128nao.Wisecraft.misc.Server;
import com.nao20010128nao.Wisecraft.misc.ServerStatus;
import com.nao20010128nao.Wisecraft.misc.AppBaseArrayAdapter;
import com.nao20010128nao.Wisecraft.pingEngine.UnconnectedPing;
import java.util.ArrayList;
import java.util.List;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.nao20010128nao.Wisecraft.Utils.*;
public class ServerFinder extends ListActivity {
	ServerList sl;
	List<Server> list;
	String ip;
	boolean isPC;
	View dialog,dialog2;
	ServerPingProvider spp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setListAdapter(sl = new ServerList());
		getListView().setOnItemClickListener(sl);
		ip = getIntent().getStringExtra("ip");
		isPC = getIntent().getBooleanExtra("ispc", false);
		new AlertDialog.Builder(this)
			.setTitle(R.string.serverFinder)
			.setView(dialog = getLayoutInflater().inflate(R.layout.server_finder_start, null, false))
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface di, int w) {
					String ip=((EditText)dialog.findViewById(R.id.ip)).getText().toString();
					int startPort=new Integer(((EditText)dialog.findViewById(R.id.startPort)).getText().toString());
					int endPort=new Integer(((EditText)dialog.findViewById(R.id.endPort)).getText().toString());
					boolean isPC=((CheckBox)dialog.findViewById(R.id.pc)).isChecked();
					startFinding(ip, startPort, endPort, isPC);
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
		if (ip != null)((EditText)dialog.findViewById(R.id.ip)).setText(ip);
		((CheckBox)dialog.findViewById(R.id.pc)).setChecked(isPC);
	}
	private void startFinding(final String ip, final int startPort, final int endPort, final boolean isPC) {
		final Dialog d=new AlertDialog.Builder(this)
			.setTitle(R.string.findingServers)
			.setView(dialog2 = getLayoutInflater().inflate(R.layout.server_finder_finding, null, false))
			.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface di, int w) {
					di.dismiss();
					finish();
				}
			})
			.setCancelable(false)
			.show();
		new AsyncTask<Void,ServerStatus,Void>(){
			public Void doInBackground(Void... l) {
				final int max=endPort - startPort;
				
				int threads=new Integer(PreferenceManager.getDefaultSharedPreferences(ServerFinder.this).getString("parallels", "6"));
				if (isPC) {
					spp = new PCMultiServerPingProvider(threads);
				} else {
					spp = new UnconnectedMultiServerPingProvider(threads);
				}

				for (int p=startPort;p < endPort;p++) {
					Server s=new Server();
					s.ip = ip;
					s.port = p;
					s.isPC = isPC;
					spp.putInQueue(s, new ServerPingProvider.PingHandler(){
							public void onPingArrives(ServerStatus s) {
								publishProgress(s);
								update(max);
							}
							public void onPingFailed(Server s) {
								update(max);
							}
						});
				}
				return null;
			}
			public void onProgressUpdate(ServerStatus... s) {
				sl.addAll(s);
			}
			private void update(final int max) {
				runOnUiThread(new Runnable(){
						public void run() {
							((ProgressBar)dialog2.findViewById(R.id.perc)).setMax(max);
							((ProgressBar)dialog2.findViewById(R.id.perc)).setProgress(((ProgressBar)dialog2.findViewById(R.id.perc)).getProgress() + 1);
							((TextView)dialog2.findViewById(R.id.status)).setText(((ProgressBar)dialog2.findViewById(R.id.perc)).getProgress() + "/" + max);
							if (((ProgressBar)dialog2.findViewById(R.id.perc)).getProgress() == max) {
								d.dismiss();
							}
						}
					});
			}
		}.execute();
	}
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}

	@Override
	protected void onDestroy() {
		// TODO: Implement this method
		super.onDestroy();
		if(spp!=null){
			spp.clearQueue();
			spp.stop();
		}
	}
	
	class ServerList extends AppBaseArrayAdapter<ServerStatus> implements AdapterView.OnItemClickListener {
		List<View> cached=new ArrayList<>();
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
			if (s.response instanceof Reply19) {//PC 1.9~
				Reply19 rep=(Reply19)s.response;
				if (rep.description == null) {
					title = s.ip + ":" + s.port;
				} else {
					title = deleteDecorations(rep.description.text);
				}
			} else if (s.response instanceof Reply) {//PC
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
			((TextView)layout.findViewById(R.id.serverAddress)).setText(s.port + "");

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
				new AlertDialog.Builder(ServerFinder.this)
					.setTitle(s.toString())
					.setItems(R.array.serverFinderMenu,new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface di,int w){
							switch(w){
								case 0:
									ServerListActivityImpl.instance.get().sl.add(s);
									break;
							}
						}
					})
					.show();
			}
		}

		@Override
		public void remove(ServerStatus object) {
			// TODO: Implement this method
			cached.remove(list.indexOf(object));
			super.remove(object);
		}
	}
}
