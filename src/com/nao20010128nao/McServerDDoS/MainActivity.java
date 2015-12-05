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
		findViewById(R.id.start).setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				String ip=((EditText)findViewById(R.id.ip)).getText().toString();
				int port=Integer.parseInt(((EditText)findViewById(R.id.port)).getText().toString());
				pref
					.edit()
					.putString("ip",ip)
					.putInt("port",port)
					.commit();
				startActivity(new Intent(MainActivity.this,TabsDDoS.class).putExtra("ip",ip).putExtra("port",port));
			}
		});
    }
	public static class TabsDDoS extends FragmentActivity {
		static WeakReference<TabsDDoS> instance=new WeakReference(null);
		
		List<Thread> t=new ArrayList<>();
		ListView players,data;
		FragmentTabHost fth;
		TabHost.TabSpec playersF,dataF;
		
		ArrayAdapter<String> adap;
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
			/*findViewById(R.id.stop).setOnClickListener(new View.OnClickListener(){
					public void onClick(View w){
						finish();
					}
				});*/
			for (int i=0;i < 150;i++){
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
					}
				});
		}
		static void setPlayersView(ListView lv){
			instance.get().setPlayersView_(lv);
		}
		static void setDataView(ListView lv){
			instance.get().setDataView_(lv);
		}
		
		void setPlayersView_(ListView lv){
			players=lv;
			lv.setAdapter(adap);
		}
		void setDataView_(ListView lv){
			data=lv;
			lv.setAdapter(adap2);
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
