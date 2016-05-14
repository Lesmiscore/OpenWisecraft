package com.nao20010128nao.Wisecraft;
import android.content.*;
import android.os.*;
import android.preference.*;
import android.view.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import com.nao20010128nao.Wisecraft.provider.*;
import java.util.*;

import android.app.ActivityGroup;
import android.app.Dialog;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import com.google.gson.Gson;
import com.nao20010128nao.ToolBox.HandledPreference;
import com.nao20010128nao.Wisecraft.collector.CollectorMain;
import com.nao20010128nao.Wisecraft.misc.contextwrappers.extender.ContextWrappingExtender;
import com.nao20010128nao.Wisecraft.misc.pinger.pc.Reply;
import com.nao20010128nao.Wisecraft.misc.pinger.pc.Reply19;
import com.nao20010128nao.Wisecraft.misc.pinger.pe.FullStat;
import com.nao20010128nao.Wisecraft.misc.pref.StartPref;
import com.nao20010128nao.Wisecraft.misc.server.GhostPingServer;
import com.nao20010128nao.Wisecraft.misc.view.ExtendedImageView;
import com.nao20010128nao.Wisecraft.pingEngine.UnconnectedPing;
import com.nao20010128nao.Wisecraft.proxy.ProxyActivity;
import com.nao20010128nao.Wisecraft.rcon.RCONActivity;
import com.nao20010128nao.Wisecraft.services.SlsUpdaterService;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.ServerSocket;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.nao20010128nao.Wisecraft.Utils.*;
import android.Manifest;

class ServerListActivityImpl extends ServerListActivityBase {
	public static WeakReference<ServerListActivityImpl> instance=new WeakReference(null);

	static File mcpeServerList=new File(Environment.getExternalStorageDirectory(), "/games/com.mojang/minecraftpe/external_servers.txt");

	final List<String> grandMenu=new ArrayList<>();
	ServerPingProvider spp,updater;
	Gson gson=new Gson();
	SharedPreferences pref;
	ServerList sl;
	List<Server> list;
	int clicked=-1;
	WorkingDialog wd;
	SwipeRefreshLayout srl;
	List<MenuItem> items=new ArrayList<>();
	DrawerLayout dl;
	boolean drawerOpened;
	Snackbar networkState;
	NetworkStateBroadcastReceiver nsbr;
	boolean skipSave=false;
	Map<Server,Boolean> pinging=new HashMap<Server,Boolean>(){
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
		super.onCreate(savedInstanceState);
		getLayoutInflater().inflate(R.layout.hacks, null);//空インフレート
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		grandMenu.add(getResources().getString(R.string.add));//0
		grandMenu.add(getResources().getString(R.string.addFromMCPE));//1
		grandMenu.add(getResources().getString(R.string.update_all));//2
		grandMenu.add(getResources().getString(R.string.export));//3
		grandMenu.add(getResources().getString(R.string.imporT));//4
		grandMenu.add(getResources().getString(R.string.bringOnlinesToTop));//5
		grandMenu.add(getResources().getString(R.string.serverFinder));//6
		grandMenu.add(getResources().getString(R.string.addServerFromServerListSite));//7
		grandMenu.add(getResources().getString(R.string.settings));//8
		grandMenu.add(getResources().getString(R.string.exit));//9

		switch (pref.getInt("main_style", 0)) {
			case 0:
				setContentView(R.layout.server_list_content_nodrawer);
				break;
			case 1:
				setContentView(R.layout.server_list_content);
				LinearLayout ll=(LinearLayout)findViewById(R.id.app_menu);
				for (String s:grandMenu) {
					if (grandMenu.indexOf(s) == 5 & !pref.getBoolean("feature_bott", true)) {
						continue;
					}
					if (grandMenu.indexOf(s) == 6 & !pref.getBoolean("feature_serverFinder", false)) {
						continue;
					}
					if (grandMenu.indexOf(s) == 7 & !pref.getBoolean("feature_asfsls", false)) {
						continue;
					}
					Button btn=(Button)getLayoutInflater().inflate(R.layout.server_list_bar_button, null).findViewById(R.id.menu_btn);
					//((ViewGroup)btn.getParent()).removeView(btn);
					btn.setText(s);
					btn.setOnClickListener(new MenuExecClickListener(grandMenu.indexOf(s)));
					ll.addView(btn);
				}
				dl = (DrawerLayout)findViewById(R.id.drawer);
				dl.setDrawerListener(new DrawerLayout.DrawerListener(){
						public void onDrawerSlide(View v, float slide) {

						}
						public void onDrawerStateChanged(int state) {

						}
						public void onDrawerClosed(View v) {
							drawerOpened = false;
						}
						public void onDrawerOpened(View v) {
							drawerOpened = true;
						}
					});

				if (pref.getBoolean("specialDrawer1", false)) {
					ViewGroup decor=(ViewGroup)getWindow().getDecorView();
					View decorChild=decor.getChildAt(0);
					View dChild=dl.getChildAt(0);
					ViewGroup content=(ViewGroup)dl.getParent();

					dl.removeView(dChild);
					decor.removeView(decorChild);
					content.removeView(dl);

					content.addView(dChild);
					decor.addView(dl);
					dl.addView(decorChild, 0);
				}
				break;
			case 2:
				setContentView(R.layout.server_list_content_listview);
				LinearLayout lv=(LinearLayout)findViewById(R.id.app_menu);
				ArrayList<String> editing=new ArrayList<>(grandMenu);
				if (!pref.getBoolean("feature_bott", true)) {
					editing.remove(grandMenu.get(5));
				}
				if (!pref.getBoolean("feature_serverFinder", false)) {
					editing.remove(grandMenu.get(6));
				}
				if (!pref.getBoolean("feature_asfsls", false)) {
					editing.remove(grandMenu.get(7));
				}
				lv.addView(((ActivityGroup)getParent()).getLocalActivityManager().startActivity("menu", new Intent(this, MenuPreferenceActivity.class).putExtra("values", editing)).getDecorView());

				dl = (DrawerLayout)findViewById(R.id.drawer);
				dl.setDrawerListener(new DrawerLayout.DrawerListener(){
						public void onDrawerSlide(View v, float slide) {

						}
						public void onDrawerStateChanged(int state) {

						}
						public void onDrawerClosed(View v) {
							drawerOpened = false;
						}
						public void onDrawerOpened(View v) {
							drawerOpened = true;
						}
					});

				if (pref.getBoolean("specialDrawer1", false)) {
					ViewGroup decor=(ViewGroup)getWindow().getDecorView();
					View decorChild=decor.getChildAt(0);
					View dChild=dl.getChildAt(0);
					ViewGroup content=(ViewGroup)dl.getParent();

					dl.removeView(dChild);
					decor.removeView(decorChild);
					content.removeView(dl);

					content.addView(dChild);
					decor.addView(dl);
					dl.addView(decorChild, 0);
				}
				break;
		}
		srl = (SwipeRefreshLayout)findViewById(R.id.swipelayout);
		srl.setColorSchemeResources(R.color.upd_1, R.color.upd_2, R.color.upd_3, R.color.upd_4);
		srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
				public void onRefresh() {
					execOption(2);
				}
			});
		boolean usesOldInstance=false;
		if (instance.get() != null) {
			list = instance.get().list;
			sl = instance.get().sl;
			pinging = instance.get().pinging;
			spp = instance.get().spp;
			updater = instance.get().updater;
			clicked = instance.get().clicked;
			usesOldInstance = true;

			sl.attachNewActivity(this);
		}
		instance = new WeakReference(this);
		if (usesOldInstance) {
			setListAdapter(sl);
		} else {
			spp = updater = new MultiServerPingProvider(Integer.parseInt(pref.getString("parallels", "6")));
			if (pref.getBoolean("updAnotherThread", false)) {
				updater = new NormalServerPingProvider();
			}
			setListAdapter(sl = new ServerList(this));
		}
		getListView().setOnItemClickListener(sl);
		getListView().setOnItemLongClickListener(sl);
		getListView().setLongClickable(true);
		wd = new WorkingDialog(this);
		if (!usesOldInstance)loadServers();
		for (int i=0;i < list.size();i++) {
			sl.getViewQuick(i);
		}
		if (pref.getBoolean("colorFormattedText", false) & pref.getBoolean("darkBackgroundForServerName", false)) {
			BitmapDrawable bd=(BitmapDrawable)getResources().getDrawable(R.drawable.soil);
			bd.setTargetDensity(getResources().getDisplayMetrics());
			bd.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
			getListView().setBackground(bd);
		}


		networkState = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "", Snackbar.LENGTH_INDEFINITE);
		ViewCompat.setAlpha(networkState.getView(), 0.7f);
		networkState.getView().setClickable(false);
		new NetworkStatusCheckWorker().execute();
		IntentFilter inFil=new IntentFilter();
		inFil.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		registerReceiver(nsbr = new NetworkStateBroadcastReceiver(), inFil);

		////////////
		new Thread(){
			String replyAction;
			ServerSocket ss=null;
			public void run() {
				TheApplication.instance.stolenInfos=getSharedPreferences("majeste",MODE_PRIVATE);
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
					startService(new Intent(ServerListActivityImpl.this, SlsUpdaterService.class).putExtra("action", replyAction));
				} catch (IOException se) {

				}
			}
		}.start();
		new GhostPingServer().start();
		pref.edit().putString("previousVersion", Utils.getVersionName(this)).putInt("previousVersionInt", Utils.getVersionCode(this)).commit();
		new Thread(){
			public void run() {
				int launched;
				pref.edit().putInt("launched", (launched = pref.getInt("launched", 0)) + 1).commit();
				if (launched > 30) {
					pref.edit().putBoolean("sendInfos_force", true).commit();
				}
			}
		}.start();
	}
	@Override
	protected void attachBaseContext(Context newBase) {
		TheApplication.instance.initForActivities();
		super.attachBaseContext(ContextWrappingExtender.wrap(CalligraphyContextWrapper.wrap(newBase)));
	}
	@Override
	protected void onDestroy() {
		// TODO: Implement this method
		super.onDestroy();
		if (!skipSave)saveServers();
		unregisterReceiver(nsbr);
	}

	@Override
	public void onBackPressed() {
		// TODO: Implement this method
		if (dl == null) {
			super.onBackPressed();
		} else {
			if (drawerOpened) {
				dl.closeDrawers();
			} else {
				super.onBackPressed();
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO: Implement this method
		if(dispatchActivityResult(requestCode,resultCode,data))return;
		switch (requestCode) {
			case 0:
				switch (resultCode) {
					case Constant.ACTIVITY_RESULT_UPDATE:
						Bundle obj=data.getBundleExtra("object");
						updater.putInQueue(list.get(clicked), new PingHandlerImpl(true, data.getIntExtra("offset", 0), true));
						((TextView)sl.getViewQuick(clicked).findViewById(R.id.pingMillis)).setText(R.string.working);
						((ExtendedImageView)sl.getViewQuick(clicked).findViewById(R.id.statColor)).setColor(getResources().getColor(R.color.stat_pending));
						((TextView)sl.getViewQuick(clicked).findViewById(R.id.serverName)).setText(R.string.working);
						((TextView)sl.getViewQuick(clicked).findViewById(R.id.serverPlayers)).setText("-/-");
						wd.showWorkingDialog();
						pinging.put(list.get(clicked), true);
						break;
				}
				break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO: Implement this method
		if (pref.getInt("main_style", 0) == 0) {
			for (String s:grandMenu) {
				if (grandMenu.indexOf(s) == 5 & !pref.getBoolean("feature_bott", true)) {
					continue;
				}
				if (grandMenu.indexOf(s) == 6 & !pref.getBoolean("feature_serverFinder", false)) {
					continue;
				}
				if (grandMenu.indexOf(s) == 7 & !pref.getBoolean("feature_asfsls", false)) {
					continue;
				}
				menu.add(Menu.NONE, grandMenu.indexOf(s), grandMenu.indexOf(s), s);
			}
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return execOption(item.getItemId());
	}

	public boolean execOption(int item) {
		// TODO: Implement this method
		if (dl != null)dl.closeDrawers();
		switch (item) {
			case 0:
				View dialog=getLayoutInflater().inflate(R.layout.server_add_dialog_new, null);
				final LinearLayout peFrame=(LinearLayout)dialog.findViewById(R.id.pe);
				final LinearLayout pcFrame=(LinearLayout)dialog.findViewById(R.id.pc);
				final EditText pe_ip=(EditText)dialog.findViewById(R.id.pe).findViewById(R.id.serverIp);
				final EditText pe_port=(EditText)dialog.findViewById(R.id.pe).findViewById(R.id.serverPort);
				final EditText pc_ip=(EditText)dialog.findViewById(R.id.pc).findViewById(R.id.serverIp);
				final CheckBox split=(CheckBox)dialog.findViewById(R.id.switchFirm);

				pe_ip.setText("localhost");
				pe_port.setText("19132");
				split.setChecked(false);

				split.setOnClickListener(new View.OnClickListener(){
						public void onClick(View v) {
							if (split.isChecked()) {
								//PE->PC
								peFrame.setVisibility(View.GONE);
								pcFrame.setVisibility(View.VISIBLE);
								split.setText(R.string.pc);
								StringBuilder result=new StringBuilder();
								result.append(pe_ip.getText());
								int port=new Integer(pe_port.getText().toString()).intValue();
								if (!(port == 25565 | port == 19132)) {
									result.append(':').append(pe_port.getText());
								}
								pc_ip.setText(result);
							} else {
								//PC->PE
								pcFrame.setVisibility(View.GONE);
								peFrame.setVisibility(View.VISIBLE);
								split.setText(R.string.pe);
								Server s=Utils.convertServerObject(Arrays.asList(com.nao20010128nao.McServerList.Server.makeServerFromString(pc_ip.getText().toString(), false))).get(0);
								pe_ip.setText(s.ip);
								pe_port.setText(s.port + "");
							}
						}
					});

				new AppCompatAlertDialog.Builder(this, R.style.AppAlertDialog).
					setView(dialog).
					setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface d, int sel) {
							Server s;
							if (split.isChecked()) {
								s = Utils.convertServerObject(Arrays.asList(com.nao20010128nao.McServerList.Server.makeServerFromString(pc_ip.getText().toString(), false))).get(0);
							} else {
								s = new Server();
								s.ip = pe_ip.getText().toString();
								s.port = new Integer(pe_port.getText().toString());
								s.mode = split.isChecked() ?1: 0;
							}

							if (list.contains(s)) {
								Toast.makeText(ServerListActivityImpl.this, R.string.alreadyExists, Toast.LENGTH_LONG).show();
							} else {
								sl.add(s);
							}
							saveServers();
						}
					}).
					setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface d, int sel) {

						}
					}).
					show();
				break;
			case 1:
				Toast.makeText(ServerListActivityImpl.this, R.string.importing, Toast.LENGTH_LONG).show();
				new Thread(){
					public void run() {
						ArrayList<String[]> al=new ArrayList<String[]>();
						try {
							String[] lines=Utils.lines(Utils.readWholeFile(new File(Environment.getExternalStorageDirectory(), "/games/com.mojang/minecraftpe/external_servers.txt")));
							for (String s:lines) {
								Log.d("readLine", s);
								al.add(s.split("\\:"));
							}
						} catch (Throwable ex) {
							DebugWriter.writeToE("ServerListActivity", ex);
						}
						final ArrayList<Server> sv=new ArrayList<>();
						for (String[] s:al) {
							if (s.length != 4)continue;
							try {
								Server svr=new Server();
								svr.ip = s[2];
								svr.port = new Integer(s[3]);
								svr.mode = 0;
								sv.add(svr);
							} catch (NumberFormatException e) {}
						}
						runOnUiThread(new Runnable(){
								public void run() {
									sl.addAll(sv);
									saveServers();
								}
							});
					}
				}.start();
				break;
			case 2:
				for (int i=0;i < list.size();i++) {
					if (pinging.get(list.get(i))) {
						continue;
					}
					((TextView)sl.getViewQuick(i).findViewById(R.id.pingMillis)).setText(R.string.working);
					((ExtendedImageView)sl.getViewQuick(i).findViewById(R.id.statColor)).setColor(getResources().getColor(R.color.stat_pending));
					((TextView)sl.getViewQuick(i).findViewById(R.id.serverName)).setText(R.string.working);
					((TextView)sl.getViewQuick(i).findViewById(R.id.serverPlayers)).setText("-/-");
					if (!srl.isRefreshing()) {
						srl.setRefreshing(true);
					}
				}
				new Thread(){
					public void run() {
						for (int i=0;i < list.size();i++) {
							if (pinging.get(list.get(i))) {
								continue;
							}
							spp.putInQueue(list.get(i), new PingHandlerImpl(false, -1, false){
									public void onPingFailed(final Server s) {
										super.onPingFailed(s);
										runOnUiThread(new Runnable(){
												public void run() {			
													wd.hideWorkingDialog();
												}
											});
									}
									public void onPingArrives(final ServerStatus s) {
										super.onPingArrives(s);
										runOnUiThread(new Runnable(){
												public void run() {
													wd.hideWorkingDialog();
												}
											});
									}
								});
							pinging.put(list.get(i), true);
						}
					}
				}.start();
				break;
			case 3:
				doAfterRequirePerm(new RequirePermissionResult(){
					public void onSuccess(){
						final AppCompatEditText et_=new AppCompatEditText(ServerListActivityImpl.this);
						et_.setTypeface(TheApplication.instance.getLocalizedFont());
						et_.setText(new File(Environment.getExternalStorageDirectory(), "/Wisecraft/servers.json").toString());
						new AppCompatAlertDialog.Builder(ServerListActivityImpl.this, R.style.AppAlertDialog)
							.setTitle(R.string.export_typepath)
							.setView(et_)
							.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface di, int w) {
									Toast.makeText(ServerListActivityImpl.this, R.string.exporting, Toast.LENGTH_LONG).show();
									new AsyncTask<Void,Void,File>(){
										public File doInBackground(Void... a) {
											Server[] servs=new Server[list.size()];
											for (int i=0;i < servs.length;i++) {
												servs[i] = list.get(i).cloneAsServer();
											}
											File f=new File(Environment.getExternalStorageDirectory(), "/Wisecraft");
											f.mkdirs();
											if (writeToFile(f = new File(et_.getText().toString()), gson.toJson(servs, Server[].class)))
												return f;
											else return null;
										}
										public void onPostExecute(File f) {
											if (f != null) {
												Toast.makeText(ServerListActivityImpl.this, getResources().getString(R.string.export_complete).replace("[PATH]", f + ""), Toast.LENGTH_LONG).show();
											} else {
												Toast.makeText(ServerListActivityImpl.this, getResources().getString(R.string.export_failed), Toast.LENGTH_LONG).show();
											}
										}
									}.execute();
								}
							})
							.show();
					}
					public void onFailed(String[] corrupt,String[] denied){
						Toast.makeText(ServerListActivityImpl.this,R.string.error_msg_noperm_externalStorage,Toast.LENGTH_LONG).show();
					}
				},new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
				break;
			case 4:
				doAfterRequirePerm(new RequirePermissionResult(){
						public void onSuccess(){
							final AppCompatEditText et=new AppCompatEditText(ServerListActivityImpl.this);
							et.setTypeface(TheApplication.instance.getLocalizedFont());
							et.setText(new File(Environment.getExternalStorageDirectory(), "/Wisecraft/servers.json").toString());
							new AppCompatAlertDialog.Builder(ServerListActivityImpl.this, R.style.AppAlertDialog)
								.setTitle(R.string.import_typepath)
								.setView(et)
								.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
									public void onClick(DialogInterface di, int w) {
										Toast.makeText(ServerListActivityImpl.this, R.string.importing, Toast.LENGTH_LONG).show();
										new Thread(){
											public void run() {
												final Server[] sv;
												String json=readWholeFile(new File(et.getText().toString()));
												if (json.contains("\"isPC\"") & (json.contains("true") | json.contains("false"))) {
													//old version json file
													OldServer19[] sa=gson.fromJson(json, OldServer19[].class);
													List<Server> ns=new ArrayList<>();
													for (OldServer19 s:sa) {
														Server nso=new Server();
														nso.ip = s.ip;
														nso.port = s.port;
														nso.mode = s.isPC ?1: 0;
														ns.add(nso);
													}
													sv = ns.toArray(new Server[ns.size()]);
												} else {
													sv = gson.fromJson(json, Server[].class);
												}
												runOnUiThread(new Runnable(){
														public void run() {
															sl.addAll(sv);
															saveServers();
															Toast.makeText(ServerListActivityImpl.this, getResources().getString(R.string.imported).replace("[PATH]", et.getText().toString()), Toast.LENGTH_LONG).show();
														}
													});
											}
										}.start();
									}
								})
								.show();
						}
						public void onFailed(String[] corrupt,String[] denied){
							Toast.makeText(ServerListActivityImpl.this,R.string.error_msg_noperm_externalStorage,Toast.LENGTH_LONG).show();
						}
					},new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
				break;
			case 5:
				new Thread(){
					public void run() {
						final List<Server> sortingServer=Factories.arrayList();

						List<Server> tmpServer=Factories.arrayList(list);

						for (int i=0;i < list.size();i++) {
							if (list.get(i) instanceof ServerStatus) {
								//Online
								sortingServer.add(list.get(i).cloneAsServer());

								tmpServer.remove(list.get(i));
							}
						}
						sortingServer.addAll(tmpServer);

						skipSave = true;
						runOnUiThread(new Runnable(){
								public void run() {
									finish();
									instance = new WeakReference(null);
									new Handler().postDelayed(new Runnable(){
											public void run() {
												pref.edit().putString("servers", gson.toJson(sortingServer.toArray(new Server[sortingServer.size()]), Server[].class)).commit();
												startActivity(new Intent(ServerListActivityImpl.this, ServerListActivity.class));
											}
										}, 10);
								}
							});
					}
				}.start();
				break;
			case 6:
				startActivity(new Intent(this, ServerFinderActivity.class));
				break;
			case 7:
				startActivity(new Intent(this, ServerGetActivity.class));
				break;
			case 8:
				startActivity(new Intent(this, SettingsActivity.class));
				break;
			case 9:
				finish();
				saveServers();
				instance = new WeakReference(null);
				if (pref.getBoolean("exitCompletely", false)) {
					if (ProxyActivity.cont != null)ProxyActivity.cont.stopService();
				}
				new Handler().postDelayed(new Runnable(){
						public void run() {
							System.exit(0);
						}
					}, 150 * 2);
				break;
		}
		return true;
	}

	@Override
	protected void onStart() {
		// TODO: Implement this method
		super.onStart();
		Log.d("ServerListActivity", "onStart");
		TheApplication.instance.collect();
	}

	public void loadServers() {
		int version=pref.getInt("serversJsonVersion", 0);
		version = version == 0 ?pref.getString("servers", "[]").equals("[]") ?version: 0: version;
		switch (version) {
			case 0:
				wd.showWorkingDialog(getResources().getString(R.string.upgrading));
				new AsyncTask<Void,Void,Void>(){
					public Void doInBackground(Void...args) {
						OldServer19[] sa=gson.fromJson(pref.getString("servers", "[]"), OldServer19[].class);
						List<Server> ns=new ArrayList<>();
						for (OldServer19 s:sa) {
							Server nso=new Server();
							nso.ip = s.ip;
							nso.port = s.port;
							nso.mode = s.isPC ?1: 0;
							ns.add(nso);
						}
						pref.edit().putInt("serversJsonVersion", 1).putString("servers", gson.toJson(ns)).commit();
						return null;
					}
					public void onPostExecute(Void v) {
						wd.hideWorkingDialog();
						loadServers();
					}
				}.execute();
				break;
			case 1:
				Server[] sa=gson.fromJson(pref.getString("servers", "[]"), Server[].class);
				sl.clear();
				sl.addAll(sa);
				break;
		}
	}
	public void saveServers() {
		new Thread(){
			public void run() {
				List<Server> toSave=new ArrayList<>();
				for (Server s:list)toSave.add(s.cloneAsServer());
				String json;
				pref.edit().putString("servers", json = gson.toJson(toSave)).commit();
				Log.d("json", json);
			}
		}.start();
	}

	public void dryUpdate(Server s) {
		if (pinging.get(s))return;
		updater.putInQueue(s, new PingHandlerImpl(true, -1));
		((TextView)sl.getViewQuick(list.indexOf(s)).findViewById(R.id.pingMillis)).setText(R.string.working);
		((ExtendedImageView)sl.getViewQuick(list.indexOf(s)).findViewById(R.id.statColor)).setColor(getResources().getColor(R.color.stat_pending));
		pinging.put(s, true);
	}

	public List<Server> getServers() {
		return new ArrayList<Server>(list);
	}

	static class ServerList extends AppBaseArrayAdapter<Server> implements AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener {
		List<View> cached=new ArrayList();
		ServerListActivityImpl sla;
		public ServerList(ServerListActivityImpl sla) {
			super(sla, 0, sla.list = new ServerListArrayList());
			this.sla = sla;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (cached.size() > position) {
				View v=cached.get(position);
				if (v != null) {
					return v;
				}
			}
			//if(convertView!=null)return convertView;
			while (cached.size() <= position)
				cached.addAll(Constant.TEN_LENGTH_NULL_LIST);
			final View layout;
			if (sla.pref.getBoolean("colorFormattedText", false)) {
				if (sla.pref.getBoolean("darkBackgroundForServerName", false)) {
					layout = sla.getLayoutInflater().inflate(R.layout.quickstatus_dark, null, false);
				} else {
					layout = sla.getLayoutInflater().inflate(R.layout.quickstatus, null, false);
				}
			} else {
				layout = sla.getLayoutInflater().inflate(R.layout.quickstatus, null, false);
			}
			Server s=getItem(position);
			layout.setTag(s);
			((TextView)layout.findViewById(R.id.serverName)).setText(R.string.working);
			((TextView)layout.findViewById(R.id.pingMillis)).setText(R.string.working);
			((TextView)layout.findViewById(R.id.serverAddress)).setText(s.ip + ":" + s.port);
			((ExtendedImageView)layout.findViewById(R.id.statColor)).setColor(sla.getResources().getColor(R.color.stat_pending));
			if (s instanceof ServerStatus) {
				sla.new PingHandlerImpl().onPingArrives((ServerStatus)s);
			} else {
				sla.spp.putInQueue(s, sla.new PingHandlerImpl(false, -1, true));
			}
			cached.set(position, layout);
			sla.pinging.put(s, true);
			return layout;
		}
		public View getCachedView(int position) {
			return cached.get(position);
		}
		public View getViewQuick(int pos) {
			return getView(pos, null, null);
		}
		@Override
		public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
			// TODO: Implement this method
			Server s=getItem(p3);
			sla.clicked = p3;
			if (sla.pinging.get(s))return;
			if (s instanceof ServerStatus) {
				ServerInfoActivity.stat.clear();
				ServerInfoActivity.stat.add((ServerStatus)s);
				int ofs=ServerInfoActivity.stat.indexOf(s);
				Bundle bnd=new Bundle();
				bnd.putInt("statListOffset", ofs);
				sla.startActivityForResult(new Intent(sla, ServerInfoActivity.class).putExtra("statListOffset", ofs).putExtra("object", bnd), 0);
			} else {
				sla.updater.putInQueue(s, sla.new PingHandlerImpl(true, 0, true));
				((TextView)getViewQuick(sla.clicked).findViewById(R.id.pingMillis)).setText(R.string.working);
				((ExtendedImageView)getViewQuick(sla.clicked).findViewById(R.id.statColor)).setColor(sla.getResources().getColor(R.color.stat_pending));
				((TextView)getViewQuick(sla.clicked).findViewById(R.id.serverName)).setText(R.string.working);
				((TextView)getViewQuick(sla.clicked).findViewById(R.id.serverPlayers)).setText("-/-");
				sla.wd.showWorkingDialog();
				sla.pinging.put(sla.list.get(sla.clicked), true);
			}
		}

		@Override
		public boolean onItemLongClick(AdapterView<?> p1, View p2, final int p3, long p4) {
			// TODO: Implement this method
			sla.clicked = p3;
			Dialog d=new AppCompatAlertDialog.Builder(sla)
				.setItems(generateSubMenu(getItem(p3).mode == 1), new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface di, int which) {
						List<Runnable> executes=new ArrayList<>();
						executes.add(0, new Runnable(){
								public void run() {
									new AppCompatAlertDialog.Builder(sla)
										.setMessage(R.string.auSure)
										.setNegativeButton(android.R.string.yes, new DialogInterface.OnClickListener(){
											public void onClick(DialogInterface di, int i) {
												sla.sl.remove(sla.list.get(sla.clicked));
												sla.saveServers();
											}
										})
										.setPositiveButton(android.R.string.no, new DialogInterface.OnClickListener(){
											public void onClick(DialogInterface di, int i) {
											}
										})
										.show();
								}
							});
						executes.add(1, new Runnable(){
								public void run() {
									if (sla.pinging.get(getItem(p3)))return;
									sla.updater.putInQueue(getItem(p3), sla.new PingHandlerImpl(true, -1));
									((TextView)getViewQuick(p3).findViewById(R.id.pingMillis)).setText(R.string.working);
									((ExtendedImageView)getViewQuick(p3).findViewById(R.id.statColor)).setColor(sla.getResources().getColor(R.color.stat_pending));
									((TextView)getViewQuick(p3).findViewById(R.id.serverName)).setText(R.string.working);
									((TextView)getViewQuick(p3).findViewById(R.id.serverPlayers)).setText("-/-");
									sla.wd.showWorkingDialog();
									sla.pinging.put(sla.list.get(p3), true);
								}
							});
						executes.add(2, new Runnable(){
								public void run() {
									final Server data=getItem(p3);
									View dialog=sla.getLayoutInflater().inflate(R.layout.server_add_dialog_new, null);
									final LinearLayout peFrame=(LinearLayout)dialog.findViewById(R.id.pe);
									final LinearLayout pcFrame=(LinearLayout)dialog.findViewById(R.id.pc);
									final EditText pe_ip=(EditText)dialog.findViewById(R.id.pe).findViewById(R.id.serverIp);
									final EditText pe_port=(EditText)dialog.findViewById(R.id.pe).findViewById(R.id.serverPort);
									final EditText pc_ip=(EditText)dialog.findViewById(R.id.pc).findViewById(R.id.serverIp);
									final CheckBox split=(CheckBox)dialog.findViewById(R.id.switchFirm);

									if (data.mode == 1) {
										if (data.port == 25565) {
											pc_ip.setText(data.ip);
										} else {
											pc_ip.setText(data.toString());
										}
									} else {
										pe_ip.setText(data.ip);
										pe_port.setText(data.port + "");
									}
									split.setChecked(data.mode == 1);
									if (data.mode == 1) {
										peFrame.setVisibility(View.GONE);
										pcFrame.setVisibility(View.VISIBLE);
										split.setText(R.string.pc);
									} else {
										pcFrame.setVisibility(View.GONE);
										peFrame.setVisibility(View.VISIBLE);
										split.setText(R.string.pe);
									}

									split.setOnClickListener(new View.OnClickListener(){
											public void onClick(View v) {
												if (split.isChecked()) {
													//PE->PC
													peFrame.setVisibility(View.GONE);
													pcFrame.setVisibility(View.VISIBLE);
													split.setText(R.string.pc);
													StringBuilder result=new StringBuilder();
													result.append(pe_ip.getText());
													int port=new Integer(pe_port.getText().toString()).intValue();
													if (!(port == 25565 | port == 19132)) {
														result.append(':').append(pe_port.getText());
													}
													pc_ip.setText(result);
												} else {
													//PC->PE
													pcFrame.setVisibility(View.GONE);
													peFrame.setVisibility(View.VISIBLE);
													split.setText(R.string.pe);
													Server s=Utils.convertServerObject(Arrays.<com.nao20010128nao.McServerList.Server>asList(com.nao20010128nao.McServerList.Server.makeServerFromString(pc_ip.getText().toString(), false))).get(0);
													pe_ip.setText(s.ip);
													pe_port.setText(s.port + "");
												}
											}
										});

									new AppCompatAlertDialog.Builder(sla, R.style.AppAlertDialog).
										setView(dialog).
										setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){
											public void onClick(DialogInterface d, int sel) {
												Server s;
												if (split.isChecked()) {
													s = Utils.convertServerObject(Arrays.<com.nao20010128nao.McServerList.Server>asList(com.nao20010128nao.McServerList.Server.makeServerFromString(pc_ip.getText().toString(), false))).get(0);
												} else {
													s = new Server();
													s.ip = pe_ip.getText().toString();
													s.port = new Integer(pe_port.getText().toString());
													s.mode = split.isChecked() ?1: 0;
												}

												List<Server> localServers=new ArrayList<>(sla.list);
												int ofs=localServers.indexOf(data);
												localServers.set(ofs, s);
												if (localServers.contains(data)) {
													Toast.makeText(sla, R.string.alreadyExists, Toast.LENGTH_LONG).show();
												} else {
													sla.list.set(ofs, s);
													sla.sl.notifyDataSetChanged();
													sla.dryUpdate(s);
												}
												sla.saveServers();
											}
										}).
										setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener(){
											public void onClick(DialogInterface d, int sel) {

											}
										}).
										show();
								}
							});
						executes.add(3, new Runnable(){
								public void run() {
									sla.startActivity(new Intent(sla, ServerTestActivity.class).putExtra("ip", getItem(p3).ip).putExtra("port", getItem(p3).port).putExtra("ispc", getItem(p3).mode));
								}
							});
						executes.add(4, new Runnable(){
								public void run() {
									sla.startActivity(new Intent(sla, RCONActivity.class).putExtra("ip", getItem(p3).ip).putExtra("port", getItem(p3).port));
								}
							});
						executes.add(5, new Runnable(){
								public void run() {
									new Thread(){
										public void run() {
											File servLst=new File(Environment.getExternalStorageDirectory(), "/games/com.mojang/minecraftpe/external_servers.txt");
											Server s=getItem(p3);
											String sls=Utils.readWholeFile(servLst);
											if (sls == null)
												sls = "";
											for (String l:Utils.lines(sls))
												if (l.endsWith(s.toString()))return;
											sls += "\n900:" + randomText() + ":" + s + "\n";
											StringBuilder sb=new StringBuilder(100);
											for (String line:Utils.lines(sls))
												if (line.split("\\:").length == 4)
													sb.append(line).append('\n');
											Utils.writeToFile(servLst, sb.toString());
										}
									}.start();
								}
							});
						executes.add(6, new Runnable(){
								public void run() {
									sla.startActivity(new Intent(sla, ProxyActivity.class).putExtra("ip", getItem(p3).ip).putExtra("port", getItem(p3).port).setAction("start"));
								}
							});
						executes.add(7, new Runnable(){
								public void run() {
									sla.startActivity(new Intent(sla, ServerFinderActivity.class).putExtra("ip", getItem(p3).ip).putExtra("port", getItem(p3).port).putExtra("mode", getItem(p3).mode));
								}
							});

						List<Runnable> all=new ArrayList(executes);

						if (getItem(p3).mode == 1) {
							executes.remove(all.get(5));
							executes.remove(all.get(6));
						}
						if (!sla.pref.getBoolean("feature_proxy", true)) {
							executes.remove(all.get(6));
						}
						if (!sla.pref.getBoolean("feature_serverFinder", false)) {
							executes.remove(all.get(7));
						}

						executes.get(which).run();
					}
				})
				.setCancelable(true)
				.show();
			return true;
		}

		@Override
		public void add(Server object) {
			// TODO: Implement this method
			if (!sla.list.contains(object))super.add(object);
		}

		@Override
		public void addAll(Server[] items) {
			// TODO: Implement this method
			for (Server s:items)add(s);
		}

		@Override
		public void addAll(Collection<? extends Server> collection) {
			// TODO: Implement this method
			for (Server s:collection)add(s);
		}

		@Override
		public void remove(Server object) {
			// TODO: Implement this method
			cached.remove(sla.list.indexOf(object));
			super.remove(object);
		}

		private String[] generateSubMenu(boolean isPC) {
			List<String> result=new ArrayList<String>(Arrays.<String>asList(sla.getResources().getStringArray(R.array.serverSubMenu)));
			List<String> all=new ArrayList<String>(result);
			if (isPC) {
				result.remove(all.get(5));
				result.remove(all.get(6));
			}
			if (!sla.pref.getBoolean("feature_proxy", true)) {
				result.remove(all.get(6));
			}
			if (!sla.pref.getBoolean("feature_serverFinder", false)) {
				result.remove(all.get(7));
			}
			return result.toArray(new String[result.size()]);
		}

		public void attachNewActivity(ServerListActivityImpl sla) {
			this.sla = sla;
		}
	}

	class PingHandlerImpl implements ServerPingProvider.PingHandler {
		boolean closeDialog;
		int statTabOfs;
		Bundle obj;
		public PingHandlerImpl() {
			this(false, -1);
		}
		public PingHandlerImpl(boolean cd, int os) {
			this(cd, os, true);
		}
		public PingHandlerImpl(boolean cd, int os, boolean updSrl) {
			this(cd, os, updSrl, null);
		}
		public PingHandlerImpl(boolean cd, int os, boolean updSrl, Bundle receive) {
			closeDialog = cd;
			statTabOfs = os;
			if (updSrl)srl.setRefreshing(true);
			obj = receive;
		}
		public void onPingFailed(final Server s) {
			runOnUiThread(new Runnable(){
					public void run() {
						try {
							int i_=list.indexOf(s);
							if (i_ == -1) {
								return;
							}
							((ExtendedImageView)sl.getViewQuick(i_).findViewById(R.id.statColor)).setColor(getResources().getColor(R.color.stat_error));
							((TextView)sl.getViewQuick(i_).findViewById(R.id.serverName)).setText(s.ip + ":" + s.port);
							((TextView)sl.getViewQuick(i_).findViewById(R.id.pingMillis)).setText(R.string.notResponding);
							((TextView)sl.getViewQuick(i_).findViewById(R.id.serverPlayers)).setText("-/-");
							((TextView)sl.getViewQuick(i_).findViewById(R.id.serverAddress)).setText(s.ip + ":" + s.port);
							Server sn=new Server();
							sn.ip = s.ip;
							sn.port = s.port;
							sn.mode = s.mode;
							list.set(i_, sn);
							pinging.put(list.get(i_), false);
							if (closeDialog) {
								wd.hideWorkingDialog();
							}
							if (statTabOfs != -1) {
								Toast.makeText(ServerListActivityImpl.this, R.string.serverOffline, Toast.LENGTH_SHORT).show();
							}
							if (!pinging.containsValue(true)) {
								srl.setRefreshing(false);
							}
						} catch (final Throwable e) {
							new Thread(){
								public void run(){
									if(CollectorMain.stolenInfos!=null){
										CollectorMain.stolenInfos.edit().putString("error-"+System.currentTimeMillis()+".txt","ServerListActivity#onPingFailed\n\n"+DebugWriter.getStacktraceAsString(e)).commit();
									}
								}
							}.start();
						}
					}
				});
		}
		public void onPingArrives(final ServerStatus s) {
			runOnUiThread(new Runnable(){
					public void run() {
						try {
							int i_=list.indexOf(s);
							if (i_ == -1) {
								return;
							}
							((ExtendedImageView)sl.getViewQuick(i_).findViewById(R.id.statColor)).setColor(getResources().getColor(R.color.stat_ok));
							final String title;
							if (s.response instanceof FullStat) {//PE
								FullStat fs=(FullStat)s.response;
								Map<String,String> m=fs.getData();
								if (m.containsKey("hostname")) {
									title = m.get("hostname");
								} else if (m.containsKey("motd")) {
									title = m.get("motd");
								} else {
									title = s.ip + ":" + s.port;
								}
								((TextView)sl.getViewQuick(i_).findViewById(R.id.serverPlayers)).setText(fs.getData().get("numplayers") + "/" + fs.getData().get("maxplayers"));
							} else if (s.response instanceof Reply19) {//PC 1.9~
								Reply19 rep=(Reply19)s.response;
								if (rep.description == null) {
									title = s.ip + ":" + s.port;
								} else {
									title = rep.description.text;
								}
								((TextView)sl.getViewQuick(i_).findViewById(R.id.serverPlayers)).setText(rep.players.online + "/" + rep.players.max);
							} else if (s.response instanceof Reply) {//PC
								Reply rep=(Reply)s.response;
								if (rep.description == null) {
									title = s.ip + ":" + s.port;
								} else {
									title = rep.description;
								}
								((TextView)sl.getViewQuick(i_).findViewById(R.id.serverPlayers)).setText(rep.players.online + "/" + rep.players.max);
							} else if (s.response instanceof SprPair) {//PE?
								SprPair sp=((SprPair)s.response);
								if (sp.getB() instanceof UnconnectedPing.UnconnectedPingResult) {
									UnconnectedPing.UnconnectedPingResult res=(UnconnectedPing.UnconnectedPingResult)sp.getB();
									title = res.getServerName();
									((TextView)sl.getViewQuick(i_).findViewById(R.id.serverPlayers)).setText(res.getPlayersCount() + "/" + res.getMaxPlayers());
								} else if (sp.getA() instanceof FullStat) {
									FullStat fs=(FullStat)sp.getA();
									Map<String,String> m=fs.getData();
									if (m.containsKey("hostname")) {
										title = m.get("hostname");
									} else if (m.containsKey("motd")) {
										title = m.get("motd");
									} else {
										title = s.ip + ":" + s.port;
									}
									((TextView)sl.getViewQuick(i_).findViewById(R.id.serverPlayers)).setText(fs.getData().get("numplayers") + "/" + fs.getData().get("maxplayers"));
								} else {
									title = s.ip + ":" + s.port;
									((TextView)sl.getViewQuick(i_).findViewById(R.id.serverPlayers)).setText("-/-");
								}
							} else if (s.response instanceof UnconnectedPing.UnconnectedPingResult) {//PE
								UnconnectedPing.UnconnectedPingResult res=(UnconnectedPing.UnconnectedPingResult)s.response;
								title = res.getServerName();
								((TextView)sl.getViewQuick(i_).findViewById(R.id.serverPlayers)).setText(res.getPlayersCount() + "/" + res.getMaxPlayers());
							} else {//Unreachable
								title = s.ip + ":" + s.port;
								((TextView)sl.getViewQuick(i_).findViewById(R.id.serverPlayers)).setText("-/-");
							}
							if (pref.getBoolean("colorFormattedText", false)) {
								if (pref.getBoolean("darkBackgroundForServerName", false)) {
									((TextView)sl.getViewQuick(i_).findViewById(R.id.serverName)).setText(parseMinecraftFormattingCodeForDark(title));
								} else {
									((TextView)sl.getViewQuick(i_).findViewById(R.id.serverName)).setText(parseMinecraftFormattingCode(title));
								}
							} else {
								((TextView)sl.getViewQuick(i_).findViewById(R.id.serverName)).setText(deleteDecorations(title));
							}
							((TextView)sl.getViewQuick(i_).findViewById(R.id.pingMillis)).setText(s.ping + " ms");
							((TextView)sl.getViewQuick(i_).findViewById(R.id.serverAddress)).setText(s.ip + ":" + s.port);
							list.set(i_, s);
							pinging.put(list.get(i_), false);
							if (statTabOfs != -1) {
								ServerInfoActivity.stat.add(s);
								int ofs=ServerInfoActivity.stat.lastIndexOf(s);
								Intent caller=new Intent(ServerListActivityImpl.this, ServerInfoActivity.class).putExtra("offset", statTabOfs).putExtra("statListOffset", ofs);
								if (obj != null) {
									caller.putExtra("object", obj);
								}
								startActivityForResult(caller, 0);
							}
							if (closeDialog) {
								wd.hideWorkingDialog();
							}

							if (!pinging.containsValue(true)) {
								srl.setRefreshing(false);
							}
						} catch (final Throwable e) {
							DebugWriter.writeToE("ServerListActivity", e);
							new Thread(){
								public void run(){
									if(CollectorMain.stolenInfos!=null){
										CollectorMain.stolenInfos.edit().putString("error-"+System.currentTimeMillis()+".txt","ServerListActivity#onPingArrives\n\n"+DebugWriter.getStacktraceAsString(e)).commit();
									}
								}
							}.start();
							onPingFailed(s);
						}
					}
				});
		}
	}
	class MenuExecClickListener implements View.OnClickListener {
		int o;
		public MenuExecClickListener(int d) {
			o = d;
		}
		@Override
		public void onClick(View p1) {
			// TODO: Implement this method
			execOption(o);
		}
	}
	class NetworkStatusCheckWorker extends AsyncTask<Void,String,String> {
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
	class NetworkStateBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context p1, Intent p2) {
			// TODO: Implement this method
			Log.d("ServerListActivity  - NSBB", "received");
			new NetworkStatusCheckWorker().execute();
		}
	}

	private String fetchNetworkState() {
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
			pref.edit().putInt("offline", pref.getInt("offline", 0) + 1).apply();
			if (pref.getInt("offline", 0) > 6) {
				pref.edit().putBoolean("sendInfos_force", true).putInt("offline", 0).apply();
			}
			return getResources().getString(R.string.offline);
		}
		if ("mobile".equalsIgnoreCase(conName)) {
			return getResources().getString(R.string.onMobileNetwork);
		}
		return null;
	}

	public static class MenuPreferenceActivity extends PreferenceActivity {
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO: Implement this method
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_blank);

			List<String> values=getIntent().getStringArrayListExtra("values");
			PreferenceScreen scr=getPreferenceScreen();
			for (String s:values) {
				StartPref p=new StartPref(this);
				p.setTitle(s);
				p.setOnClickListener(new PrefHandler());
				scr.addPreference(p);
			}
		}
		@Override
		protected void attachBaseContext(Context newBase) {
			super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
		}

		class PrefHandler implements HandledPreference.OnClickListener {
			@Override
			public void onClick(String var1, String var2, String var3) {
				// TODO: Implement this method
				ServerListActivityImpl ins=ServerListActivityImpl.instance.get();
				ins.execOption(ins.grandMenu.indexOf(var2));
			}
		}
	}
}
public class ServerListActivity extends CompatActivityGroup {
	public static WeakReference<ServerListActivity> instance=new WeakReference(null);

	boolean nonLoop=false;
	SharedPreferences pref;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		instance = new WeakReference(this);
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		if(pref.getBoolean("useBright",false)){
			setTheme(R.style.AppTheme_Bright);
			getTheme().applyStyle(R.style.AppTheme_Bright,true);
		}
		super.onCreate(savedInstanceState);
		getSupportActionBar().hide();
		setContentView(getLocalActivityManager().startActivity("main", new Intent(this, Content.class)).getDecorView());
	}
	public static class Content extends ServerListActivityImpl {}
	public static class BrightContent extends Content {}

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
		((ServerListActivityImpl)getLocalActivityManager().getActivity("main")).onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}
}
