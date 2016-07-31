package com.nao20010128nao.Wisecraft.misc;
import android.content.*;
import android.os.*;
import android.support.v7.widget.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.google.android.gms.tasks.*;
import com.mikepenz.crossfader.*;
import com.mikepenz.materialdrawer.*;
import com.mikepenz.materialdrawer.model.interfaces.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import com.nao20010128nao.Wisecraft.misc.server.*;
import com.nao20010128nao.Wisecraft.services.*;
import java.io.*;
import java.net.*;

import com.nao20010128nao.Wisecraft.R;

import static com.nao20010128nao.Wisecraft.misc.Utils.*;

//Misc
public abstract class ServerListActivityBase4 extends ServerListActivityBaseGrand
{
	protected int newVersionAnnounce=0;
	protected Drawer drawer;
	protected Crossfader crossFader;
	protected MiniDrawer sideMenu;
	protected RecyclerView rv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		if (pref.getBoolean("useBright", false)) {
			setTheme(R.style.AppTheme_Bright_NoActionBar);
			getTheme().applyStyle(R.style.AppTheme_Bright_NoActionBar, true);
		}
		super.onCreate(savedInstanceState);
		{
			setContentView(R.layout.server_list_content_toolbar);

			setSupportActionBar(Utils.getToolbar(this));
			DrawerBuilder bld=new DrawerBuilder()
				.withActivity(this)
				.withToolbar(Utils.getToolbar(this))
				.withDrawerWidthRes(R.dimen.drawer_width)
				.withDrawerLayout(R.layout.drawer_single_for_builder);
			
			switch(getResources().getInteger(R.integer.server_list_layout_mode)*0){
				case 0:
					drawer=bld.build();
					break;
				case 1:
					drawer=bld.buildView();
					sideMenu=drawer.getMiniDrawer();
					sideMenu.withOnMiniDrawerItemClickListener(new MiniDrawer.OnMiniDrawerItemClickListener(){
							public boolean onItemClick(View p1, int p2, IDrawerItem p3, int p4){
								sideMenu.getAdapter().deselect();//cf. MiniDrawer#updateItem(long)
								return false;
							}
						});
					View minidrawer=sideMenu.build(this);
					crossFader=new Crossfader()
						.withContent(findViewById(android.R.id.content))
						.withFirst(drawer.getSlider(),getResources().getDimensionPixelSize(R.dimen.drawer_width))
						.withSecond(minidrawer,getResources().getDimensionPixelSize(R.dimen.minidrawer_width))
						.withSavedInstance(savedInstanceState)
						.build();
					break;
				case 2:
					drawer=bld.buildView();
					((FrameLayout)findViewById(R.id.drawer)).addView(drawer.getSlider());
					break;
			}
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
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onPostCreate(savedInstanceState);
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
	
}
