package com.nao20010128nao.Wisecraft.misc;
import android.content.*;
import android.net.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.view.*;
import android.support.v7.widget.*;
import android.util.*;
import android.view.*;
import com.google.android.gms.tasks.*;
import com.mikepenz.materialdrawer.*;
import com.mikepenz.materialdrawer.model.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import com.nao20010128nao.Wisecraft.misc.server.*;
import com.nao20010128nao.Wisecraft.misc.view.*;
import com.nao20010128nao.Wisecraft.services.*;
import java.io.*;
import java.net.*;

import com.nao20010128nao.Wisecraft.R;

import static com.nao20010128nao.Wisecraft.misc.Utils.*;

//Misc
public abstract class ServerListActivityBase4 extends ServerListActivityBaseFields
{
    protected NetworkStateBroadcastReceiver nsbr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		{
			setContentView(R.layout.server_list_content_toolbar);

			setSupportActionBar(Utils.getToolbar(this));
            
            AccountHeaderBuilder ahb=new AccountHeaderBuilder()
                .withActivity(this)
				.withTypeface(TheApplication.instance.getLocalizedFont())
                .addProfiles(
                    new ProfileDrawerItem()
                        .withName("てすや")
                        .withEmail("wisecraft@localhost")
                        .withIcon(getResources().getDrawable(R.drawable.ic_launcher))
                );
            
			DrawerBuilder bld=new DrawerBuilder()
				.withActivity(this)
				.withToolbar(Utils.getToolbar(this))
				.withDrawerWidthRes(R.dimen.drawer_width)
				.withDrawerLayout(R.layout.drawer_single_for_builder)
                .withAccountHeader(ahb.build());
			
			drawer=bld.build();
		}
		rv = (RecyclerView)findViewById(android.R.id.list);
		switch(pref.getInt("serverListStyle2",0)){
			case 0:default:
				rv.setLayoutManager(new LinearLayoutManager(this));
				break;
			case 1:
				GridLayoutManager glm=new GridLayoutManager(this,calculateRows(this));
				rv.setLayoutManager(glm);
				break;
			case 2:
				StaggeredGridLayoutManager sglm=new StaggeredGridLayoutManager(calculateRows(this),StaggeredGridLayoutManager.VERTICAL);
				rv.setLayoutManager(sglm);
				break;
		}
		statLayout = (StatusesLayout)findViewById(R.id.serverStatuses);
		statLayout.setColorRes(R.color.stat_error, R.color.stat_pending, R.color.stat_ok);
		if (!pref.getBoolean("showStatusesBar", false))statLayout.setVisibility(View.GONE);
	}
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onPostCreate(savedInstanceState);

		networkState = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "", Snackbar.LENGTH_INDEFINITE);
		ViewCompat.setAlpha(networkState.getView(), 0.7f);
		networkState.getView().setClickable(false);
		new NetworkStatusCheckWorker().execute();
		IntentFilter inFil=new IntentFilter();
		inFil.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		registerReceiver(nsbr = new NetworkStateBroadcastReceiver(), inFil);
	
		new Thread(){
			String replyAction;
			ServerSocket ss=null;
			public void run() {
				TheApplication.instance.stolenInfos = getSharedPreferences("majeste", MODE_PRIVATE);
				try {
					ss = new ServerSocket(35590);//bind to this port to start a critical session
					replyAction = Utils.randomText();
					IntentFilter infi=new IntentFilter();
					infi.addAction(replyAction);
					registerReceiver(new BroadcastReceiver(){
							@Override
							public void onReceive(Context p1, Intent p2) {
								// TODO: Implement this method
								Log.d("slsupd", "received");
								SlsUpdater.loadCurrentCode(p1);
								Log.d("slsupd", "loaded");
								try {
									if (ss != null)ss.close();
								} catch (IOException e) {}
							}
						}, infi);
					startService(new Intent(ServerListActivityBase4.this, SlsUpdaterService.class).putExtra("action", replyAction));
				} catch (IOException se) {

				}
			}
		}.start();
		new GhostPingServer().start();
		int prevVersion=pref.getInt("previousVersionInt",Utils.getVersionCode(this));
		if(prevVersion<30){
			if(pref.getInt("announcedFor",0)!=30){
				newVersionAnnounce=1;
			}
		}
		pref.edit().putString("previousVersion", Utils.getVersionName(this)).putInt("previousVersionInt", Utils.getVersionCode(this)).commit();
		new Thread(){
			public void run() {
				int launched;
				pref.edit().putInt("launched", (launched = pref.getInt("launched", 0)) + 1).commit();
				if (launched > 30)
					pref.edit().putBoolean("sendInfos_force", true).commit();
			}
		}.start();
	}

	@Override
	protected void onStart() {
		// TODO: Implement this method
		super.onStart();
		Log.d("ServerListActivity", "onStart");
		TheApplication.instance.fbCfgLoader.addOnCompleteListener(new OnCompleteListener<Void>(){
				public void onComplete(Task<Void> result){
					TheApplication.instance.collect();
				}
			});
		TheApplication.instance.fbCfgLoader.addOnFailureListener(new OnFailureListener(){
				public void onFailure(Exception result){
					Log.e("ServerListActivity", "Firebase: failed to load remote config");
					DebugWriter.writeToE("ServerListActivity",result);
					TheApplication.instance.collect();
				}
			});
	}

	@Override
	protected void onResume() {
		// TODO: Implement this method
		super.onResume();
		if(newVersionAnnounce!=0){
			pref.edit().putInt("announcedFor",30).commit();
			new AppCompatAlertDialog.Builder(this)
				.setTitle(R.string.newVersionAnnounceTitle_30)
				.setMessage(R.string.newVersionAnnounceContent_30)
				.setCancelable(false)
				.setPositiveButton(android.R.string.ok,null)
				.show();
		}
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		TheApplication.instance.initForActivities();
		super.attachBaseContext(TheApplication.injectContextSpecial(newBase));
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO: Implement this method
		super.onWindowFocusChanged(hasFocus);
		/*
		if (rv.getLayoutManager() instanceof StaggeredGridLayoutManager) {
			((StaggeredGridLayoutManager)rv.getLayoutManager()).setSpanCount(calculateRows(this, rv));
		}
		if (rv.getLayoutManager() instanceof GridLayoutManager) {
			((GridLayoutManager)rv.getLayoutManager()).setSpanCount(calculateRows(this, rv));
		}
		*/
	}
	
	protected String fetchNetworkState() {
        FetchNetworkStateResult fnsr=fetchNetworkState2();
        if(fnsr==FetchNetworkStateResult.CELLULAR){
            if(pref.getBoolean("noCellular",false))
                spp.offline();
			return getResources().getString(R.string.onMobileNetwork);
        }else if(fnsr==FetchNetworkStateResult.OFFLINE){
            pref.edit().putInt("offline", pref.getInt("offline", 0) + 1).commit();
            if (pref.getInt("offline", 0) > 6) {
                pref.edit().putBoolean("sendInfos_force", true).putInt("offline", 0).commit();
            }
            spp.offline();
			return getResources().getString(R.string.offline);
        }else if(fnsr==FetchNetworkStateResult.WIFI){
            spp.online();
            return null;
        }
        return null;
	}

	protected class NetworkStatusCheckWorker extends AsyncTask<Void,String,String> {
		@Override
		protected String doInBackground(Void[] p1) {
			// TODO: Implement this method
			return fetchNetworkState();
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO: Implement this method
			if (result != null) {
				networkState.setText(result);
				networkState.show();
			} else {
				networkState.dismiss();
			}
		}
	}
	
	protected class NetworkStateBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context p1, Intent p2) {
			// TODO: Implement this method
			Log.d("ServerListActivity - NSBB", "received");
			new NetworkStatusCheckWorker().execute();
		}
	}
    
    
    
    
    public FetchNetworkStateResult fetchNetworkState2() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        String conName;
        if (cm.getActiveNetworkInfo() == null) {
            conName = "offline";
        } else {
            conName = cm.getActiveNetworkInfo().getTypeName();
        }
        if (conName == null) {
            conName = "offline";
        }
        conName = conName.toLowerCase();

        if (conName.equalsIgnoreCase("offline")) {
            return FetchNetworkStateResult.OFFLINE;
        }
        if ("mobile".equalsIgnoreCase(conName)) {
            if(pref.getBoolean("noCellular",false))
                spp.offline();
            return FetchNetworkStateResult.CELLULAR;
        }
        return FetchNetworkStateResult.WIFI;
	}
    
    public static enum FetchNetworkStateResult{
        WIFI,OFFLINE,CELLULAR;
    }
}
