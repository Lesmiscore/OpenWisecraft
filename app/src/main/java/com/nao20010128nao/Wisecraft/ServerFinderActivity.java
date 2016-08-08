package com.nao20010128nao.Wisecraft;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.preference.*;
import android.support.design.widget.*;
import android.support.v4.content.*;
import android.support.v4.view.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import com.nao20010128nao.Wisecraft.misc.pinger.pc.*;
import com.nao20010128nao.Wisecraft.pingEngine.*;
import com.nao20010128nao.Wisecraft.provider.*;
import java.lang.ref.*;
import java.util.*;

import static com.nao20010128nao.Wisecraft.misc.Utils.*;
class ServerFinderActivityImpl extends AppCompatActivity implements ServerListActivityInterface {
	RecyclerServerList sl;
	List<ServerStatus> list;
	String ip;
	int mode;
	View dialog,dialog2;
	ServerPingProvider spp;
	SharedPreferences pref;
	RecyclerView rv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recycler_view_content);
		sl = new RecyclerServerList(this);
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
		rv.setAdapter(sl);
		ip = getIntent().getStringExtra("ip");
		mode = getIntent().getIntExtra("mode", 0);
		new AppCompatAlertDialog.Builder(this, R.style.AppAlertDialog)
			.setTitle(R.string.serverFinder)
			.setView(dialog = getLayoutInflater().inflate(R.layout.server_finder_start, null, false))
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface di, int w) {
					String ip=((EditText)dialog.findViewById(R.id.ip)).getText().toString();
					int startPort=new Integer(((EditText)dialog.findViewById(R.id.startPort)).getText().toString());
					int endPort=new Integer(((EditText)dialog.findViewById(R.id.endPort)).getText().toString());
					boolean isPC=((CheckBox)dialog.findViewById(R.id.pc)).isChecked();
					startFinding(ip, Math.min(startPort,endPort), Math.max(startPort,endPort), isPC);
				}
			})
			.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface di, int w) {
					di.dismiss();
					finish();
				}
			})
			.setOnCancelListener(new DialogInterface.OnCancelListener(){
				public void onCancel(DialogInterface di) {
					finish();
				}
			})
			.show();
		if (ip != null)((EditText)dialog.findViewById(R.id.ip)).setText(ip);
		((CheckBox)dialog.findViewById(R.id.pc)).setChecked(mode == 0 ?false: true);

		if (pref.getBoolean("colorFormattedText", false) & pref.getBoolean("darkBackgroundForServerName", false)) {
			BitmapDrawable bd=(BitmapDrawable)getResources().getDrawable(R.drawable.soil);
			bd.setTargetDensity(getResources().getDisplayMetrics());
			bd.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
			rv.setBackgroundDrawable(bd);
		}
	}
	private void startFinding(final String ip, final int startPort, final int endPort, final boolean isPC) {
		DisplayMetrics dm=getResources().getDisplayMetrics();
		final PopupWindow pw=new PopupWindow(this);
		pw.setTouchable(false);
		pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		pw.setContentView(dialog2 = getLayoutInflater().inflate(R.layout.server_finder_finding, null, false));
		pw.setWidth(Math.min(dm.widthPixels, dm.heightPixels));
		pw.setHeight(getResources().getDimensionPixelSize(R.dimen.server_finder_finding_height));

		ViewCompat.setAlpha(dialog2, 0.7f);

		pw.showAtLocation(getWindow().getDecorView().findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
		;
		setTitle(ip+":("+startPort+"~"+endPort+")");
		new AsyncTask<Void,ServerStatus,Void>(){
			public Void doInBackground(Void... l) {
				final int max=endPort - startPort;

				int threads=Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(ServerFinderActivityImpl.this).getString("parallels", "6"));
				if (isPC) {
					spp = new PCMultiServerPingProvider(threads);
				} else {
					spp = new UnconnectedMultiServerPingProvider(threads);
				}

				for (int p=startPort;p < endPort;p++) {
					Server s=new Server();
					s.ip = ip;
					s.port = p;
					s.mode = isPC ?1: 0;
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
								pw.dismiss();
								Snackbar.make(getWindow().getDecorView(), getResources().getString(R.string.foundServersCount).replace("[NUMBER]", "" + sl.getItemCount()), Snackbar.LENGTH_SHORT).show();
							}
						}
					});
			}
		}.execute();
	}
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(TheApplication.injectContextSpecial(newBase));
	}

	@Override
	protected void onDestroy() {
		// TODO: Implement this method
		super.onDestroy();
		if (spp != null) {
			spp.clearQueue();
			spp.stop();
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO: Implement this method
		super.onWindowFocusChanged(hasFocus);
		/*
		if(rv.getLayoutManager() instanceof StaggeredGridLayoutManager){
			((StaggeredGridLayoutManager)rv.getLayoutManager()).setSpanCount(calculateRows(this,rv));
		}
		if(rv.getLayoutManager() instanceof GridLayoutManager){
			((GridLayoutManager)rv.getLayoutManager()).setSpanCount(calculateRows(this,rv));
		}
		*/
	}
	
	@Override
	public void addIntoList(Server s) {
		// TODO: Implement this method
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO: Implement this method
		super.onActivityResult(requestCode, resultCode, data);
	}

	class RecyclerServerList extends ListRecyclerViewAdapter<ServerStatusWrapperViewHolder,ServerStatus> implements AdapterView.OnItemClickListener {
		ServerFinderActivityImpl sta;

		public RecyclerServerList(ServerFinderActivityImpl parent) {
			super(parent.list = new ArrayList<ServerStatus>());
			sta = parent;
		}

		@Override
		public void onBindViewHolder(ServerStatusWrapperViewHolder viewHolder, final int offset) {
			if (sta.pref.getBoolean("colorFormattedText", false)) {
				if (sta.pref.getBoolean("darkBackgroundForServerName", false)) {
					viewHolder.setDarkness(true);
				} else {
					viewHolder.setDarkness(false);
				}
			} else {
				viewHolder.setDarkness(false);
			}
			ServerStatus s=getItem(offset);
			
			final String title;
			if (s.response instanceof Reply19) {//PC 1.9~
				Reply19 rep=(Reply19)s.response;
				if (rep.description == null) {
					title = s.toString();
				} else {
					title = rep.description.text;
				}
			} else if (s.response instanceof Reply) {//PC
				Reply rep=(Reply)s.response;
				if (rep.description == null) {
					title = s.toString();
				} else {
					title = rep.description;
				}
			} else if (s.response instanceof UnconnectedPing.UnconnectedPingResult) {
				title = ((UnconnectedPing.UnconnectedPingResult)s.response).getServerName();
			} else {//Unreachable
				title = s.toString();
			}
			if (sta.pref.getBoolean("colorFormattedText", false)) {
				if (sta.pref.getBoolean("darkBackgroundForServerName", false)) {
					viewHolder.setServerName(parseMinecraftFormattingCodeForDark(title));
				} else {
					viewHolder.setServerName(parseMinecraftFormattingCode(title));
				}
			} else {
				viewHolder.setServerName(deleteDecorations(title));
			}
			viewHolder
				.setStatColor(ContextCompat.getColor(sta, R.color.stat_ok))
				.hideServerPlayers()
				.setTag(s)
				.setPingMillis(s.ping)
				.setServer(s)
				.setServerAddress(s.port + "");
			applyHandlersForViewTree(viewHolder.itemView,
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						onItemClick(null, v, offset, Long.MIN_VALUE);
					}
				}
			);
		}

		@Override
		public ServerStatusWrapperViewHolder onCreateViewHolder(ViewGroup parent, int type) {
			switch(sta.pref.getInt("serverListStyle2",0)){
				case 0:default:
					return new ServerStatusWrapperViewHolder(sta,false,parent);
				case 1:case 2:
					return new ServerStatusWrapperViewHolder(sta,true,parent);
			}
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			final Server s=getItem(position);
			if (s instanceof ServerStatus) {
				new AppCompatAlertDialog.Builder(ServerFinderActivityImpl.this)
					.setTitle(s.toString())
					.setItems(R.array.serverFinderMenu, new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface di, int w) {
							switch (w) {
								case 0:
									if(!ServerListActivityImpl.instance.get().list.contains(s)){
										ServerListActivityImpl.instance.get().sl.add(s.cloneAsServer());
										ServerListActivityImpl.instance.get().dryUpdate(s,true);
									}
									break;
							}
						}
					})
					.show();
			}
		}
	}
}
public class ServerFinderActivity extends CompatActivityGroup {
	public static WeakReference<ServerFinderActivity> instance=new WeakReference(null);

	boolean nonLoop=false;
	SharedPreferences pref;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		instance = new WeakReference(this);
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		super.onCreate(savedInstanceState);
		getSupportActionBar().hide();
		setContentView(getLocalActivityManager().startActivity("main", new Intent(this, Content.class).putExtras(getIntent())).getDecorView());
	}
	public static class Content extends ServerFinderActivityImpl {public static void deleteRef() {instance = new WeakReference<>(null);}}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO: Implement this method
		if (nonLoop)
			return true;
		nonLoop = true;
		boolean val= getLocalActivityManager().getActivity("main").onCreateOptionsMenu(menu);
		nonLoop = false;
		return val;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO: Implement this method
		if (nonLoop)
			return true;
		nonLoop = true;
		boolean val= getLocalActivityManager().getActivity("main").onOptionsItemSelected(item);
		nonLoop = false;
		return val;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO: Implement this method
		((ActivityResultInterface)getLocalActivityManager().getActivity("main")).onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(TheApplication.injectContextSpecial(newBase));
	}
}
