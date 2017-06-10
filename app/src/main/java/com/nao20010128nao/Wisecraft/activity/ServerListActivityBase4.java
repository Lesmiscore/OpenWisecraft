package com.nao20010128nao.Wisecraft.activity;

import android.content.*;
import android.net.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.view.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.support.v7.widget.Toolbar;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.mikepenz.materialdrawer.*;
import com.nao20010128nao.Wisecraft.R;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.server.*;
import com.nao20010128nao.Wisecraft.misc.view.*;

import static com.nao20010128nao.Wisecraft.misc.Utils.*;

//Misc
abstract class ServerListActivityBase4 extends ServerListActivityBase5
{
    protected BroadcastReceiver nsbr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		{
			setContentView(R.layout.server_list_content_toolbar);

			setSupportActionBar(Utils.getToolbar(this));
			new Handler().post(() -> {
                Toolbar tb=Utils.getToolbar(ServerListActivityBase4.this);
                TextView tv=Utils.getActionBarTextView(tb);
                if(tv!=null){
                    tv.setGravity(Gravity.CENTER);
                }
            });
            
			DrawerBuilder bld=new DrawerBuilder()
				.withActivity(this)
				.withToolbar(Utils.getToolbar(this))
				.withDrawerWidthRes(R.dimen.drawer_width)
				.withDrawerLayout(R.layout.drawer_single_for_builder);
			
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
		if (true|!pref.getBoolean("showStatusesBar", false))statLayout.setVisibility(View.GONE);
		
		coordinator=(CoordinatorLayout)findViewById(R.id.coordinator);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		indicator = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "", Snackbar.LENGTH_SHORT);
		ViewCompat.setAlpha(indicator.getView(), 0.7f);
		indicator.getView().setClickable(false);
		new NetworkStatusCheckWorker().execute();
		IntentFilter inFil=new IntentFilter();
		inFil.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		registerReceiver(nsbr = new BroadcastReceiver(){
							 @Override
							 public void onReceive(Context p1, Intent p2) {
								 Log.d("ServerListActivity - NSBB", "received");
								 new NetworkStatusCheckWorker().execute();
							 }
						 }, inFil);
	
		new GhostPingServer().start();
		int prevVersion=pref.getInt("previousVersionInt",Utils.getVersionCode(this));
		if(prevVersion<30){
			if(pref.getInt("announcedFor",0)!=30){
				newVersionAnnounce=1;
			}
		}
		pref.edit().putString("previousVersion", Utils.getVersionName(this)).putInt("previousVersionInt", Utils.getVersionCode(this)).commit();
		new Thread(() -> {
            int launched;
            pref.edit().putInt("launched", (launched = pref.getInt("launched", 0)) + 1).commit();
            if (launched > 4)
                pref.edit().putBoolean("sendInfos_force", true).commit();
        }).start();
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d("ServerListActivity", "onStart");
		TheApplication.instance.fbCfgLoader.addOnCompleteListener(result -> TheApplication.instance.collect());
		TheApplication.instance.fbCfgLoader.addOnFailureListener(result -> {
            Log.e("ServerListActivity", "Firebase: failed to load remote config");
            DebugWriter.writeToE("ServerListActivity",result);
            TheApplication.instance.collect();
        });
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(newVersionAnnounce!=0){
			pref.edit().putInt("announcedFor",30).commit();
			new AlertDialog.Builder(this, ThemePatcher.getDefaultDialogStyle(this))
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
		super.onWindowFocusChanged(hasFocus);
		recalculateSpans();
	}

	@Override
	public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
		super.onMultiWindowModeChanged(isInMultiWindowMode);
		recalculateSpans();
	}
	
	public void recalculateSpans(){
		if (rv.getLayoutManager() instanceof StaggeredGridLayoutManager) {
			((StaggeredGridLayoutManager)rv.getLayoutManager()).setSpanCount(calculateRows(this, rv));
		}
		if (rv.getLayoutManager() instanceof GridLayoutManager) {
			((GridLayoutManager)rv.getLayoutManager()).setSpanCount(calculateRows(this, rv));
		}
	}
	
	protected String fetchNetworkState() {
        FetchNetworkStateResult fnsr=fetchNetworkState2();
        if(fnsr==FetchNetworkStateResult.CELLULAR){
            if(pref.getBoolean("noCellular",false))
                spp.offline();
			else
				spp.online();
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
			return fetchNetworkState();
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				indicator.setText(result);
				indicator.show();
			}
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
