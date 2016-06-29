package com.nao20010128nao.Wisecraft.old;
import android.content.*;
import android.os.*;
import android.preference.*;
import com.google.gson.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import java.util.*;
import com.nao20010128nao.Wisecraft.misc.Server;
import com.nao20010128nao.Wisecraft.misc.ServerStatus;
import com.nao20010128nao.Wisecraft.misc.ServerSorter;

//Server Sort Part
public class ServerListActivityBase2 extends AppCompatListActivity
{
	SharedPreferences pref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		pref=PreferenceManager.getDefaultSharedPreferences(this);
	}
	
	public void doSort(final List<Server> sl,final com.nao20010128nao.Wisecraft.misc.ServerListActivityBase2.SortKind sk){
		new Thread(){
			public void run(){
				final List<Server> sortingServer=sk.doSort(sl);
				runOnUiThread(new Runnable(){
						public void run() {
							finish();
							ServerListActivityImpl.instance.clear();
							new Handler().postDelayed(new Runnable(){
									public void run() {
										pref.edit().putString("servers", new Gson().toJson(sortingServer.toArray(new Server[sortingServer.size()]), Server[].class)).commit();
										startActivity(new Intent(ServerListActivityBase2.this, ServerListActivity.class));
									}
								}, 10);
						}
					});
			}
		}.start();
	}
}
