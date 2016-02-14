package com.nao20010128nao.Wisecraft;
import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.misc.*;
import java.io.*;
import java.util.*;

import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.gson.Gson;
import com.nao20010128nao.MCPing.ServerPingResult;
import com.nao20010128nao.MCPing.pc.Reply;
import com.nao20010128nao.MCPing.pe.FullStat;
import com.nao20010128nao.Wisecraft.pingEngine.UnconnectedPing;
import com.nao20010128nao.Wisecraft.provider.MultiServerPingProvider;
import com.nao20010128nao.Wisecraft.provider.ServerPingProvider;
import com.nao20010128nao.Wisecraft.rcon.RCONActivity;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.nao20010128nao.Wisecraft.Utils.*;
import com.nao20010128nao.Wisecraft.proxy.ProxyActivity;

public class ServerListActivity extends ListActivity {
	File file=new File(Environment.getExternalStorageDirectory(), "/games/com.mojang/minecraftpe/external_servers.txt");

	ServerPingProvider spp=new MultiServerPingProvider(6);
	Gson gson=new Gson();
	SharedPreferences pref;
	ServerList sl;
	List<Server> list;
	int clicked=-1;
	WorkingDialog wd;
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
		setListAdapter(sl = new ServerList());
		getListView().setOnItemClickListener(sl);
		getListView().setOnItemLongClickListener(sl);
		getListView().setLongClickable(true);
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		wd = new WorkingDialog(this);
		loadServers();
		for(int i=0;i<list.size();i++){
			sl.getViewQuick(i);
		}
	}
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}
	@Override
	protected void onDestroy() {
		// TODO: Implement this method
		super.onDestroy();
		saveServers();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO: Implement this method
		switch (requestCode) {
			case 0:
				switch (resultCode) {
					case Constant.ACTIVITY_RESULT_UPDATE:
						spp.putInQueue(ServerInfoActivity.stat, new PingHandlerImpl(){
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
												ServerInfoActivity.stat = s;
												startActivityForResult(new Intent(ServerListActivity.this, ServerInfoActivity.class), 0);
												wd.hideWorkingDialog();
											}
										});
								}
							});
						((TextView)sl.getViewQuick(clicked).findViewById(R.id.pingMillis)).setText(R.string.working);
						((ImageView)sl.getViewQuick(clicked).findViewById(R.id.statColor)).setImageDrawable(new ColorDrawable(getResources().getColor(R.color.stat_pending)));
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
		menu.add(Menu.NONE, 0, 0, R.string.add);
		menu.add(Menu.NONE, 1, 1, R.string.addFromMCPE);
		menu.add(Menu.NONE, 2, 2, R.string.update_all);
		menu.add(Menu.NONE, 3, 3, R.string.export);
		menu.add(Menu.NONE, 4, 4, R.string.imporT);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO: Implement this method
		switch (item.getItemId()) {
			case 0:
				final Server data=new Server();
				data.ip = "localhost";
				data.port = 19132;
				data.isPC = false;
				View dialog=getLayoutInflater().inflate(R.layout.serveradddialog, null);
				final EditText ip=(EditText)dialog.findViewById(R.id.serverIp);
				final EditText port=(EditText)dialog.findViewById(R.id.serverPort);
				final CheckBox isPc=(CheckBox)dialog.findViewById(R.id.pc);

				ip.setText(data.ip);
				port.setText(data.port + "");

				new AlertDialog.Builder(this).
					setView(dialog).
					setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface d, int sel) {
							data.ip = ip.getText().toString();
							data.port = new Integer(port.getText().toString());
							data.isPC = isPc.isChecked();
							if (list.contains(data)) {
								Toast.makeText(ServerListActivity.this, R.string.alreadyExists, Toast.LENGTH_LONG).show();
							} else {
								sl.add(data);
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
				Toast.makeText(ServerListActivity.this, R.string.importing, Toast.LENGTH_LONG).show();
				new Thread(){
					public void run() {
						ArrayList<String[]> al=new ArrayList<String[]>();
						BufferedReader br=null;
						try {
							br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(Environment.getExternalStorageDirectory(), "/games/com.mojang/minecraftpe/external_servers.txt"))));
							while (true) {
								String s=br.readLine();
								if (s == null)break;
								Log.d("readLine", s);
								al.add(s.split("\\:"));
							}
						} catch (Throwable ex) {
							ex.printStackTrace();
						} finally {
							try {
								if (br != null)
									br.close();
							} catch (IOException e) {

							}
						}
						final ArrayList<Server> sv=new ArrayList<>();
						for (String[] s:al) {
							Server svr=new Server();
							svr.ip = s[2];
							svr.port = new Integer(s[3]);
							svr.isPC = false;
							sv.add(svr);
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
					final int i_=i;
					spp.putInQueue(list.get(i), new PingHandlerImpl(){
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
					((TextView)sl.getViewQuick(i).findViewById(R.id.pingMillis)).setText(R.string.working);
					((ImageView)sl.getViewQuick(i_).findViewById(R.id.statColor)).setImageDrawable(new ColorDrawable(getResources().getColor(R.color.stat_pending)));
					pinging.put(list.get(i), true);
				}
				break;
			case 3:
				new AsyncTask<Void,Void,File>(){
					public File doInBackground(Void... a) {
						Server[] servs=new Server[list.size()];
						for (int i=0;i < servs.length;i++) {
							servs[i] = list.get(i).cloneAsServer();
						}
						File f=new File(Environment.getExternalStorageDirectory(), "/Wisecraft");
						f.mkdirs();
						if (writeToFile(f = new File(f, "servers.json"), gson.toJson(servs, Server[].class)))
							return f;
						else return null;
					}
					public void onPostExecute(File f) {
						if (f != null) {
							Toast.makeText(ServerListActivity.this, getResources().getString(R.string.export_complete).replace("[PATH]", f + ""), Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(ServerListActivity.this, getResources().getString(R.string.export_failed), Toast.LENGTH_LONG).show();
						}
					}
				}.execute();
				break;
			case 4:
				final EditText et=new EditText(this);
				et.setTypeface(TheApplication.instance.getLocalizedFont());
				et.setText(new File(Environment.getExternalStorageDirectory(), "/Wisecraft/servers.json").toString());
				new AlertDialog.Builder(this)
					.setTitle(R.string.import_typepath)
					.setView(et)
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface di, int w) {
							Toast.makeText(ServerListActivity.this, R.string.importing, Toast.LENGTH_LONG).show();
							new Thread(){
								public void run() {
									final Server[] sv=gson.fromJson(readWholeFile(new File(et.getText().toString())), Server[].class);
									runOnUiThread(new Runnable(){
											public void run() {
												sl.addAll(sv);
												saveServers();
											}
										});
								}
							}.start();
						}
					})
					.show();
				break;
		}
		return true;
	}

	public void loadServers() {
		Server[] sa=gson.fromJson(pref.getString("servers", "[]"), Server[].class);
		sl.clear();
		sl.addAll(sa);
	}
	public void saveServers() {
		String json;
		pref.edit().putString("servers", json = gson.toJson(list.toArray(new Server[list.size()]), Server[].class)).commit();
		Log.d("json", json);
	}

	private boolean alreadyAddedInList(Server ser) {
		FileReader fr=null;
		BufferedReader br=null;
		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			while (true) {
				String s=br.readLine();
				if (br == null)break;
				if (s.endsWith(":" + ser.ip + ":" + ser.port + ""))return true;
			}
			return false;
		} catch (Throwable ex) {
			return false;
		} finally {
			try {
				if (fr != null)fr.close();
				if (br != null)br.close();
			} catch (IOException e) {

			}
		}
	}

	class ServerList extends AppBaseArrayAdapter<Server> implements AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener {
		List<View> cached=new ArrayList();
		public ServerList() {
			super(ServerListActivity.this, 0, list = new ArrayList<Server>());
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO: Implement this method
			if (cached.size() > position) {
				View v=cached.get(position);
				if (v != null) {
					return v;
				}
			}
			//if(convertView!=null)return convertView;
			final View layout=getLayoutInflater().inflate(R.layout.quickstatus, null, false);
			Server s=getItem(position);
			layout.setTag(s);
			spp.putInQueue(s, new PingHandlerImpl());
			((TextView)layout.findViewById(R.id.serverName)).setText(R.string.working);
			((TextView)layout.findViewById(R.id.pingMillis)).setText(R.string.working);
			((TextView)layout.findViewById(R.id.serverAddress)).setText(s.ip + ":" + s.port);
			((ImageView)layout.findViewById(R.id.statColor)).setImageDrawable(new ColorDrawable(getResources().getColor(R.color.stat_pending)));
			while (cached.size() <= position)
				cached.addAll(Constant.TEN_LENGTH_NULL_LIST);
			cached.set(position, layout);
			pinging.put(s, true);
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
			clicked = p3;
			if (pinging.get(s))return;
			if (s instanceof ServerStatus) {
				ServerInfoActivity.stat = (ServerStatus)s;
				startActivityForResult(new Intent(ServerListActivity.this, ServerInfoActivity.class), 0);
			} else {
				spp.putInQueue(s, new PingHandlerImpl(){
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
										ServerInfoActivity.stat = s;
										startActivityForResult(new Intent(ServerListActivity.this, ServerInfoActivity.class), 0);
										wd.hideWorkingDialog();
									}
								});
						}
					});
				((TextView)sl.getViewQuick(clicked).findViewById(R.id.pingMillis)).setText(R.string.working);
				((ImageView)sl.getViewQuick(clicked).findViewById(R.id.statColor)).setImageDrawable(new ColorDrawable(getResources().getColor(R.color.stat_pending)));
				wd.showWorkingDialog();
				pinging.put(list.get(clicked), true);
			}
		}

		@Override
		public boolean onItemLongClick(AdapterView<?> p1, View p2, final int p3, long p4) {
			// TODO: Implement this method
			clicked = p3;
			Dialog d=new AlertDialog.Builder(ServerListActivity.this)
				.setItems(R.array.serverSubMenu, new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface di, int which) {
						switch (which) {
							case 0:
								new AlertDialog.Builder(ServerListActivity.this)
									.setMessage(R.string.auSure)
									.setNegativeButton(android.R.string.yes, new DialogInterface.OnClickListener(){
										public void onClick(DialogInterface di, int i) {
											sl.remove(list.get(clicked));
											saveServers();
										}
									})
									.setPositiveButton(android.R.string.no, new DialogInterface.OnClickListener(){
										public void onClick(DialogInterface di, int i) {
										}
									})
									.show();
								break;
							case 1:
								if (pinging.get(getItem(p3)))break;
								spp.putInQueue(getItem(p3), new PingHandlerImpl(){
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
								((TextView)sl.getViewQuick(p3).findViewById(R.id.pingMillis)).setText(R.string.working);
								((ImageView)sl.getViewQuick(p3).findViewById(R.id.statColor)).setImageDrawable(new ColorDrawable(getResources().getColor(R.color.stat_pending)));
								wd.showWorkingDialog();
								pinging.put(list.get(p3), true);
								break;
							case 2:
								final Server data=new Server();
								data.ip = getItem(p3).ip;
								data.port = getItem(p3).port;
								data.isPC = getItem(p3).isPC;
								View dialog=getLayoutInflater().inflate(R.layout.serveradddialog, null);
								final EditText ip=(EditText)dialog.findViewById(R.id.serverIp);
								final EditText port=(EditText)dialog.findViewById(R.id.serverPort);
								final CheckBox isPc=(CheckBox)dialog.findViewById(R.id.pc);

								ip.setText(data.ip);
								port.setText(data.port + "");
								isPc.setChecked(data.isPC);

								new AlertDialog.Builder(getContext()).
									setView(dialog).
									setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){
										public void onClick(DialogInterface d, int sel) {
											data.ip = ip.getText().toString();
											data.port = new Integer(port.getText().toString());
											data.isPC = isPc.isChecked();

											list.set(p3, data);
											spp.putInQueue(getItem(p3), new PingHandlerImpl(){
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
											((TextView)sl.getViewQuick(p3).findViewById(R.id.serverAddress)).setText(data.ip + ":" + data.port);

											saveServers();
										}
									}).
									setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener(){
										public void onClick(DialogInterface d, int sel) {

										}
									}).
									show();
								break;
							case 3:
								startActivity(new Intent(ServerListActivity.this, ServerTestActivity.class).putExtra("ip", getItem(p3).ip).putExtra("port", getItem(p3).port).putExtra("ispc", getItem(p3).isPC));
								break;
							case 4:
								startActivity(new Intent(ServerListActivity.this, RCONActivity.class).putExtra("ip", getItem(p3).ip).putExtra("port", getItem(p3).port));
								break;
							case 5:
								new Thread(){
									public void run() {
										Server s=getItem(p3);
										if (alreadyAddedInList(s)) {
											return;
										}
										try {
											FileWriter fw = new FileWriter(file, true);
											fw.append("900:" + randomText() + ":" + s.ip + ":" + s.port + "\n");
											fw.close();
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
								}.start();
								break;
							case 6:
								startActivity(new Intent(ServerListActivity.this, ProxyActivity.class).putExtra("ip", getItem(p3).ip).putExtra("port", getItem(p3).port));
								break;
						}
					}
				})
				.setCancelable(true)
				.show();
			return true;
		}

		@Override
		public void add(ServerListActivity.Server object) {
			// TODO: Implement this method
			if (!list.contains(object))super.add(object);
		}

		@Override
		public void addAll(ServerListActivity.Server[] items) {
			// TODO: Implement this method
			for (Server s:items)add(s);
		}

		@Override
		public void addAll(Collection<? extends ServerListActivity.Server> collection) {
			// TODO: Implement this method
			for (Server s:collection)add(s);
		}

		@Override
		public void remove(ServerListActivity.Server object) {
			// TODO: Implement this method
			cached.remove(list.indexOf(object));
			super.remove(object);
		}
	}
	public static class Server {
		public String ip;
		public int port;
		public boolean isPC;

		@Override
		public int hashCode() {
			// TODO: Implement this method
			return ip.hashCode() ^ port;
		}

		@Override
		public boolean equals(Object o) {
			// TODO: Implement this method
			if (!(o instanceof Server)) {
				return false;
			}
			Server os=(Server)o;
			return os.ip.equals(ip) & os.port == port & (os.isPC ^ isPC) == false;
		}
		public Server cloneAsServer() {
			Server s=new Server();
			s.ip = ip;
			s.port = port;
			s.isPC = isPC;
			return s;
		}
	}
	public static class ServerStatus extends Server {
		public ServerPingResult response;
		public long ping;
	}
	class PingHandlerImpl implements ServerPingProvider.PingHandler {
		public void onPingFailed(final Server s) {
			runOnUiThread(new Runnable(){
					public void run() {
						int i_=list.indexOf(s);

						((ImageView)sl.getViewQuick(i_).findViewById(R.id.statColor)).setImageDrawable(new ColorDrawable(getResources().getColor(R.color.stat_error)));
						((TextView)sl.getViewQuick(i_).findViewById(R.id.serverName)).setText(s.ip + ":" + s.port);
						((TextView)sl.getViewQuick(i_).findViewById(R.id.pingMillis)).setText(R.string.notResponding);
						Server sn=new Server();
						sn.ip = s.ip;
						sn.port = s.port;
						sn.isPC = s.isPC;
						list.set(i_, sn);
						pinging.put(list.get(i_), false);
					}
				});
		}
		public void onPingArrives(final ServerStatus s) {
			runOnUiThread(new Runnable(){
					public void run() {
						int i_=list.indexOf(s);

						((ImageView)sl.getViewQuick(i_).findViewById(R.id.statColor)).setImageDrawable(new ColorDrawable(getResources().getColor(R.color.stat_ok)));
						final String title;
						if (s.response instanceof FullStat) {//PE
							FullStat fs=(FullStat)s.response;
							Map<String,String> m=fs.getData();
							if (m.containsKey("hostname")) {
								title = deleteDecorations(m.get("hostname"));
							} else if (m.containsKey("motd")) {
								title = deleteDecorations(m.get("motd"));
							} else {
								title = s.ip + ":" + s.port;
							}
						} else if (s.response instanceof Reply) {//PC
							Reply rep=(Reply)s.response;
							if (rep.description == null) {
								title = s.ip + ":" + s.port;
							} else {
								title = deleteDecorations(rep.description);
							}
						} else if (s.response instanceof SprPair) {//PE?
							SprPair sp=((SprPair)s.response);
							if (sp.getB() instanceof UnconnectedPing.UnconnectedPingResult) {
								title = ((UnconnectedPing.UnconnectedPingResult)sp.getB()).getServerName();
							} else if (sp.getA() instanceof FullStat) {
								FullStat fs=(FullStat)sp.getA();
								Map<String,String> m=fs.getData();
								if (m.containsKey("hostname")) {
									title = deleteDecorations(m.get("hostname"));
								} else if (m.containsKey("motd")) {
									title = deleteDecorations(m.get("motd"));
								} else {
									title = s.ip + ":" + s.port;
								}
							} else {
								title = s.ip + ":" + s.port;
							}
						} else if (s.response instanceof UnconnectedPing.UnconnectedPingResult) {
							title = ((UnconnectedPing.UnconnectedPingResult)s.response).getServerName();
						} else {//Unreachable
							title = s.ip + ":" + s.port;
						}
						((TextView)sl.getViewQuick(i_).findViewById(R.id.serverName)).setText(deleteDecorations(title));
						((TextView)sl.getViewQuick(i_).findViewById(R.id.pingMillis)).setText(s.ping + " ms");
						list.set(i_, s);
						pinging.put(list.get(i_), false);
					}
				});
		}
	}
}
