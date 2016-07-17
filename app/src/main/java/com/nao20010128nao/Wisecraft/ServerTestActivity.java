package com.nao20010128nao.Wisecraft;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.preference.*;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import com.nao20010128nao.Wisecraft.misc.pinger.pc.*;
import com.nao20010128nao.Wisecraft.misc.pinger.pe.*;
import com.nao20010128nao.Wisecraft.pingEngine.*;
import com.nao20010128nao.Wisecraft.provider.*;
import java.lang.ref.*;
import java.util.*;
import uk.co.chrisjenx.calligraphy.*;

import static com.nao20010128nao.Wisecraft.misc.Utils.*;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v4.content.ContextCompat;

class ServerTestActivityImpl extends AppCompatActivity implements ServerListActivityInterface {
	static WeakReference<ServerTestActivityImpl> instance=new WeakReference(null);

	ServerPingProvider spp=new NormalServerPingProvider();
	RecyclerServerList sl;
	List<Server> list;
	int clicked=-1;
	ProgressDialog waitDialog;
	int times,port;
	String ip;
	int mode;
	View dialog;
	SharedPreferences pref;
	RecyclerView rv;
	Map<Integer,Boolean> pinging=new HashMap<Integer,Boolean>(){
		@Override
		public Boolean get(Object key) {
			// TODO: Implement this method
			Boolean b= super.get(key);
			if (b == null) {
				return false;
			}
			return b;
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		if (pref.getBoolean("useBright", false)) {
			setTheme(R.style.AppTheme_Bright);
			getTheme().applyStyle(R.style.AppTheme_Bright, true);
		}
		super.onCreate(savedInstanceState);
		boolean usesOldInstance=false;
		if (instance.get() != null) {
			sl = instance.get().sl;
			pinging = instance.get().pinging;
			usesOldInstance = true;
		} else {
			sl = new RecyclerServerList(this);
		}
		instance = new WeakReference(this);
        setContentView(R.layout.recycler_view_content);
		rv = (RecyclerView)findViewById(android.R.id.list);
		rv.setLayoutManager(new LinearLayoutManager(this));
		rv.setAdapter(sl);
		ip = getIntent().getStringExtra("ip");
		port = getIntent().getIntExtra("port", -1);
		mode = getIntent().getIntExtra("ispc", 0);
		if (usesOldInstance & sl.getItemCount() != 0) {

		} else {
			new AppCompatAlertDialog.Builder(this, R.style.AppAlertDialog)
				.setTitle(R.string.testServer)
				.setView(dialog = getLayoutInflater().inflate(R.layout.test_server_dialog, null, false))
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface di, int w) {
						di.dismiss();
						String nu=((EditText)dialog.findViewById(R.id.pingTimes)).getText().toString();
						try {
							times = Integer.valueOf(nu);
						} catch (NumberFormatException e) {
							finish();
							return;
						}
						for (int i=0;i < times;i++) {
							Server s=new Server();
							s.ip = ip;
							s.port = port;
							s.mode = mode;
							sl.add(s);
                            final int position=i;
							pinging.put(position, true);
							spp.putInQueue(s, new ServerPingProvider.PingHandler(){
									public void onPingFailed(final Server s) {
										runOnUiThread(new Runnable(){
												public void run() {
													list.set(position, s);
                                                    sl.notifyItemChanged(position);
													pinging.put(position, false);
												}
											});
									}
									public void onPingArrives(final ServerStatus sv) {
										runOnUiThread(new Runnable(){
												public void run() {
													list.set(position, sv);
                                                    sl.notifyItemChanged(position);
                                                    pinging.put(position, false);
												}
											});
									}
								});
						}
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
		}

		if (pref.getBoolean("colorFormattedText", false) & pref.getBoolean("darkBackgroundForServerName", false)) {
			BitmapDrawable bd=(BitmapDrawable)getResources().getDrawable(R.drawable.soil);
			bd.setTargetDensity(getResources().getDisplayMetrics());
			bd.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
			rv.setBackgroundDrawable(bd);
		}
	}
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(TheApplication.injectContextSpecial(newBase));
	}

	@Override
	public void onBackPressed() {
		// TODO: Implement this method
		super.onBackPressed();
		instance = new WeakReference(null);
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

	static class RecyclerServerList extends ListRecyclerViewAdapter<STAVH,Server> implements AdapterView.OnItemClickListener {
		ServerTestActivityImpl sta;

		public RecyclerServerList(ServerTestActivityImpl parent) {
			super(parent.list = new ArrayList<Server>());
			sta = parent;
		}

		@Override
		public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
			// TODO: Implement this method
			Server s=getItem(p3);
			sta.clicked = p3;
			if (s instanceof ServerStatus) {
				ServerInfoActivity.stat.add((ServerStatus)s);
				int ofs=ServerInfoActivity.stat.indexOf(s);
				sta.startActivityForResult(new Intent(sta, ServerInfoActivity.class).putExtra("nonUpd", true).putExtra("statListOffset", ofs), 0);
			}
		}

		@Override
		public void onBindViewHolder(ServerTestActivityImpl.STAVH parent, final int offset) {
			// TODO: Implement this method
			View layout=parent.itemView;
			Server s=getItem(offset);
            ((TextView)layout.findViewById(R.id.serverAddress)).setText(s.ip + ":" + s.port);
            ((TextView) layout.findViewById(R.id.serverPlayers)).setText("-/-");
            if (sta.pinging.get(offset)) {
				((TextView)layout.findViewById(R.id.serverName)).setText(R.string.working);
				((TextView)layout.findViewById(R.id.pingMillis)).setText(R.string.working);
				((ImageView)layout.findViewById(R.id.statColor)).setImageDrawable(new ColorDrawable(ContextCompat.getColor(sta, R.color.stat_pending)));
			} else {
				if (s instanceof ServerStatus) {
					ServerStatus sv=(ServerStatus)s;
					((ImageView)layout.findViewById(R.id.statColor)).setImageDrawable(new ColorDrawable(ContextCompat.getColor(sta, R.color.stat_ok)));
					final String title;
					if (sv.response instanceof FullStat) {//PE
						FullStat fs=(FullStat)sv.response;
						Map<String,String> m=fs.getData();
						if (m.containsKey("hostname")) {
							title = m.get("hostname");
						} else if (m.containsKey("motd")) {
							title = m.get("motd");
						} else {
							title = sv.ip + ":" + sv.port;
						}
                        ((TextView) layout.findViewById(R.id.serverPlayers)).setText(fs.getData().get("numplayers") + "/" + fs.getData().get("maxplayers"));
                    } else if (sv.response instanceof Reply19) {//PC 1.9~
						Reply19 rep=(Reply19)sv.response;
						if (rep.description == null) {
							title = sv.ip + ":" + sv.port;
						} else {
							title = rep.description.text;
						}
                        ((TextView) layout.findViewById(R.id.serverPlayers)).setText(rep.players.online + "/" + rep.players.max);
                    } else if (sv.response instanceof Reply) {//PC
						Reply rep=(Reply)sv.response;
						if (rep.description == null) {
							title = sv.ip + ":" + sv.port;
						} else {
							title = rep.description;
						}
                        ((TextView) layout.findViewById(R.id.serverPlayers)).setText(rep.players.online + "/" + rep.players.max);
                    } else if (sv.response instanceof SprPair) {//PE?
						SprPair sp=((SprPair)sv.response);
						if (sp.getB() instanceof UnconnectedPing.UnconnectedPingResult) {
                            UnconnectedPing.UnconnectedPingResult res = (UnconnectedPing.UnconnectedPingResult) sp.getB();
                            title = res.getServerName();
                            ((TextView) layout.findViewById(R.id.serverPlayers)).setText(res.getPlayersCount() + "/" + res.getMaxPlayers());
                        } else if (sp.getA() instanceof FullStat) {
							FullStat fs=(FullStat)sp.getA();
							Map<String,String> m=fs.getData();
							if (m.containsKey("hostname")) {
								title = m.get("hostname");
							} else if (m.containsKey("motd")) {
								title = m.get("motd");
							} else {
								title = sv.ip + ":" + sv.port;
							}
                            ((TextView) layout.findViewById(R.id.serverPlayers)).setText(fs.getData().get("numplayers") + "/" + fs.getData().get("maxplayers"));
                        } else {
							title = sv.ip + ":" + sv.port;
                            ((TextView) layout.findViewById(R.id.serverPlayers)).setText("-/-");
                        }
					} else if (sv.response instanceof UnconnectedPing.UnconnectedPingResult) {
                        UnconnectedPing.UnconnectedPingResult res = (UnconnectedPing.UnconnectedPingResult) sv.response;
                        title = res.getServerName();
                        ((TextView) layout.findViewById(R.id.serverPlayers)).setText(res.getPlayersCount() + "/" + res.getMaxPlayers());
                    } else {//Unreachable
						title = sv.ip + ":" + sv.port;
                        ((TextView) layout.findViewById(R.id.serverPlayers)).setText("-/-");
                    }
					if (sta.pref.getBoolean("colorFormattedText", false)) {
						if (sta.pref.getBoolean("darkBackgroundForServerName", false)) {
							((TextView)layout.findViewById(R.id.serverName)).setText(parseMinecraftFormattingCodeForDark(title));
						} else {
							((TextView)layout.findViewById(R.id.serverName)).setText(parseMinecraftFormattingCode(title));
						}
					} else {
						((TextView)layout.findViewById(R.id.serverName)).setText(deleteDecorations(title));
					}
					((TextView)layout.findViewById(R.id.pingMillis)).setText(sv.ping + " ms");
				} else {
					((ImageView)layout.findViewById(R.id.statColor)).setImageDrawable(new ColorDrawable(ContextCompat.getColor(sta, R.color.stat_error)));
					((TextView)layout.findViewById(R.id.serverName)).setText(s.ip + ":" + s.port);
					((TextView)layout.findViewById(R.id.pingMillis)).setText(R.string.notResponding);
				}
			}
			applyHandlersForViewTree(parent.itemView, new View.OnClickListener(){
					public void onClick(View v) {
						onItemClick(null, v, offset, Long.MIN_VALUE);
					}
				});
		}

		@Override
		public ServerTestActivityImpl.STAVH onCreateViewHolder(ViewGroup viewGroup, int type) {
			// TODO: Implement this method
			int layout;
			if (sta.pref.getBoolean("colorFormattedText", false)) {
				if (sta.pref.getBoolean("darkBackgroundForServerName", false)) {
					layout = R.layout.quickstatus_dark;
				} else {
					layout = R.layout.quickstatus;
				}
			} else {
				layout = R.layout.quickstatus;
			}
			return sta.new STAVH(LayoutInflater.from(sta).inflate(layout, viewGroup, false));
		}

		public void attachNewActivity(ServerTestActivityImpl newSta) {
			sta = newSta;
		}
	}
	class STAVH extends RecyclerView.ViewHolder {
		public STAVH(View v) {
			super(v);
		}
		public View findViewById(int resId) {
			return itemView.findViewById(resId);
		}
	}
}
public class ServerTestActivity extends CompatActivityGroup {
	public static WeakReference<ServerTestActivity> instance=new WeakReference(null);

	boolean nonLoop=false;
	SharedPreferences pref;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		instance = new WeakReference(this);
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		if (pref.getBoolean("useBright", false)) {
			setTheme(R.style.AppTheme_Bright);
			getTheme().applyStyle(R.style.AppTheme_Bright, true);
		}
		super.onCreate(savedInstanceState);
		getSupportActionBar().hide();
		setContentView(getLocalActivityManager().startActivity("main", new Intent(this, Content.class).putExtras(getIntent())).getDecorView());
	}
	public static class Content extends ServerTestActivityImpl {public static void deleteRef() {instance = new WeakReference<>(null);}}
	
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
