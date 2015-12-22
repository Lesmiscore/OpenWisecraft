package com.nao20010128nao.McServerPingPong;
import android.app.*;
import android.content.*;
import android.graphics.drawable.*;
import android.os.*;
import android.preference.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.google.gson.*;
import java.io.*;
import java.util.*;
import query.*;
import static com.nao20010128nao.McServerPingPong.Utils.*;
import com.nao20010128nao.McServerPingPong.provider.*;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import com.nao20010128nao.McServerPingPong.rcon.*;

public class ServerListActivity extends ListActivity{
	ServerPingProvider spp=new MultiServerPingProvider(3);
	Gson gson=new Gson();
	SharedPreferences pref;
	ServerList sl;
	List<Server> list;
	int clicked=-1;
	ProgressDialog waitDialog;
	Map<Server,Boolean> pinging=new HashMap<Server,Boolean>(){
		@Override
		public Boolean get(Object key) {
			// TODO: Implement this method
			Boolean b= super.get(key);
			if(b==null){
				return false;
			}
			return b;
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setListAdapter(sl=new ServerList());
		getListView().setOnItemClickListener(sl);
		getListView().setOnItemLongClickListener(sl);
		getListView().setLongClickable(true);
		pref=PreferenceManager.getDefaultSharedPreferences(this);
		loadServers();
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
		switch(requestCode){
			case 0:
				switch(resultCode){
					case Consistant.ACTIVITY_RESULT_UPDATE:
						spp.putInQueue(ServerInfoActivity.stat,new ServerPingProvider.PingHandler(){
								public void onPingFailed(final Server s){
									runOnUiThread(new Runnable(){
											public void run(){
												sl.getViewQuick(clicked).findViewById(R.id.statColor).setBackground(new ColorDrawable(getResources().getColor(R.color.stat_error)));
												((TextView)sl.getViewQuick(clicked).findViewById(R.id.serverName)).setText(s.ip+":"+s.port);
												((TextView)sl.getViewQuick(clicked).findViewById(R.id.pingMillis)).setText(R.string.notResponding);
												Server sn=new Server();
												sn.ip=s.ip;
												sn.port=s.port;
												sn.isPC=s.isPC;
												list.set(clicked,sn);
												hideWorkingDialog();
												pinging.put(list.get(clicked),false);
											}
										});
								}
								public void onPingArrives(final ServerStatus s){
									runOnUiThread(new Runnable(){
											public void run(){
												sl.getViewQuick(clicked).findViewById(R.id.statColor).setBackground(new ColorDrawable(getResources().getColor(R.color.stat_ok)));
												final String title;
												Map<String,String> m=s.response.getData();
												if (m.containsKey("hostname")) {
													title = deleteDecorations(m.get("hostname"));
												} else if (m.containsKey("motd")) {
													title = deleteDecorations(m.get("motd"));
												} else if (m.containsKey("description")) {
													title = deleteDecorations(m.get("description"));
												} else {
													title = s.ip + ":" + s.port;
												}
												((TextView)sl.getViewQuick(clicked).findViewById(R.id.serverName)).setText(deleteDecorations(title));
												((TextView)sl.getViewQuick(clicked).findViewById(R.id.pingMillis)).setText(s.ping+" ms");
												ServerInfoActivity.stat=s;
												list.set(clicked,s);
												startActivityForResult(new Intent(ServerListActivity.this,ServerInfoActivity.class),0);
												hideWorkingDialog();
												pinging.put(list.get(clicked),false);
											}
										});
								}
							});
						((TextView)sl.getViewQuick(clicked).findViewById(R.id.pingMillis)).setText(R.string.working);
						sl.getViewQuick(clicked).findViewById(R.id.statColor).setBackground(new ColorDrawable(getResources().getColor(R.color.stat_pending)));
						showWorkingDialog();
						pinging.put(list.get(clicked),true);
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
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO: Implement this method
		switch(item.getItemId()){
			case 0:
				final Server data=new Server();
				data.ip="localhost";
				data.port=19132;
				data.isPC=false;
				View dialog=getLayoutInflater().inflate(R.layout.serveradddialog,null);
				final EditText ip=(EditText)dialog.findViewById(R.id.serverIp);
				final EditText port=(EditText)dialog.findViewById(R.id.serverPort);
				final CheckBox isPc=(CheckBox)dialog.findViewById(R.id.pc);
				
				ip.setText(data.ip);
				port.setText(data.port+"");

				new AlertDialog.Builder(this).
					setView(dialog).
					setPositiveButton(android.R.string.yes,new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface d,int sel){
							data.ip=ip.getText().toString();
							data.port=new Integer(port.getText().toString());
							data.isPC=isPc.isChecked();
							if(list.contains(data)){
								Toast.makeText(ServerListActivity.this,R.string.alreadyExists,Toast.LENGTH_LONG).show();
							}else{
								sl.add(data);
							}
							saveServers();
						}
					}).
					setNegativeButton(android.R.string.no,new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface d,int sel){

						}
					}).
					show();
				break;
			case 1:
				Toast.makeText(ServerListActivity.this,R.string.importing,Toast.LENGTH_LONG).show();
				new Thread(){
					public void run(){
						ArrayList<String[]> al=new ArrayList<String[]>();
						BufferedReader br=null;
						try{
							br=new BufferedReader(new InputStreamReader(new FileInputStream(new File(Environment.getExternalStorageDirectory(),"/games/com.mojang/minecraftpe/external_servers.txt"))));
							while(true){
								String s=br.readLine();
								if(s==null)break;
								Log.d("readLine",s);
								al.add(s.split("\\:"));
							}
						}catch(Throwable ex){
							ex.printStackTrace();
						}finally{
							try{
								if (br != null)
									br.close();
							}catch (IOException e){

							}
						}
						final ArrayList<Server> sv=new ArrayList<>();
						for(String[] s:al){
							Server svr=new Server();
							svr.ip=s[2];
							svr.port=new Integer(s[3]);
							svr.isPC=false;
							sv.add(svr);
						}
						runOnUiThread(new Runnable(){
							public void run(){
								sl.addAll(sv);
								saveServers();
							}
						});
					}
				}.start();
				break;
			case 2:
				for(int i=0;i<list.size();i++){
					if(pinging.get(list.get(i))){
						continue;
					}
					final int i_=i;
					spp.putInQueue(list.get(i),new ServerPingProvider.PingHandler(){
							public void onPingFailed(final Server s){
								runOnUiThread(new Runnable(){
										public void run(){
											sl.getViewQuick(i_).findViewById(R.id.statColor).setBackground(new ColorDrawable(getResources().getColor(R.color.stat_error)));
											((TextView)sl.getViewQuick(i_).findViewById(R.id.serverName)).setText(s.ip+":"+s.port);
											((TextView)sl.getViewQuick(i_).findViewById(R.id.pingMillis)).setText(R.string.notResponding);
											Server sn=new Server();
											sn.ip=s.ip;
											sn.port=s.port;
											sn.isPC=s.isPC;
											list.set(i_,sn);
											hideWorkingDialog();
											pinging.put(list.get(i_),false);
										}
									});
							}
							public void onPingArrives(final ServerStatus s){
								runOnUiThread(new Runnable(){
										public void run(){
											sl.getViewQuick(i_).findViewById(R.id.statColor).setBackground(new ColorDrawable(getResources().getColor(R.color.stat_ok)));
											final String title;
											Map<String,String> m=s.response.getData();
											if (m.containsKey("hostname")) {
												title = deleteDecorations(m.get("hostname"));
											} else if (m.containsKey("motd")) {
												title = deleteDecorations(m.get("motd"));
											} else if (m.containsKey("description")) {
												title = deleteDecorations(m.get("description"));
											} else {
												title = s.ip + ":" + s.port;
											}
											((TextView)sl.getViewQuick(i_).findViewById(R.id.serverName)).setText(deleteDecorations(title));
											((TextView)sl.getViewQuick(i_).findViewById(R.id.pingMillis)).setText(s.ping+" ms");
											list.set(i_,s);
											pinging.put(list.get(i_),false);
											hideWorkingDialog();
										}
									});
							}
						});
					((TextView)sl.getViewQuick(i).findViewById(R.id.pingMillis)).setText(R.string.working);
					sl.getViewQuick(i).findViewById(R.id.statColor).setBackground(new ColorDrawable(getResources().getColor(R.color.stat_pending)));
					pinging.put(list.get(i),true);
				}
				break;
		}
		return true;
	}
	
	public void loadServers(){
		Server[] sa=gson.fromJson(pref.getString("servers","[]"),Server[].class);
		sl.clear();
		sl.addAll(sa);
	}
	public void saveServers(){
		String json;
		pref.edit().putString("servers",json=gson.toJson(list.toArray(new Server[list.size()]),Server[].class)).commit();
		Log.d("json",json);
	}
	public void showWorkingDialog(){
		if(waitDialog!=null){
			hideWorkingDialog();
		}
		waitDialog= new ProgressDialog(this);
		waitDialog.setIndeterminate(true);
		waitDialog.setMessage(getResources().getString(R.string.working));
		waitDialog.setCancelable(false);
		waitDialog.show();
	}
	public void hideWorkingDialog(){
		if(waitDialog==null){
			return;
		}
		waitDialog.cancel();
		waitDialog=null;
	}
	class ServerList extends ArrayAdapter<Server> implements AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener{
		List<View> cached=new ArrayList();
		public ServerList(){
			super(ServerListActivity.this,0,list=new ArrayList<Server>());
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO: Implement this method
			if(cached.size()>position){
				View v=cached.get(position);
				if(v!=null){
					return v;
				}
			}
			//if(convertView!=null)return convertView;
			final View layout=getLayoutInflater().inflate(R.layout.quickstatus,null,false);
			Server s=getItem(position);
			layout.setTag(s);
			spp.putInQueue(s,new ServerPingProvider.PingHandler(){
				public void onPingFailed(final Server s){
					runOnUiThread(new Runnable(){
							public void run(){
								layout.findViewById(R.id.statColor).setBackground(new ColorDrawable(getResources().getColor(R.color.stat_error)));
								((TextView)layout.findViewById(R.id.serverName)).setText(s.ip+":"+s.port);
								((TextView)layout.findViewById(R.id.pingMillis)).setText(R.string.notResponding);
								pinging.put(s,false);
							}
						});
				}
				public void onPingArrives(final ServerStatus sv){
					runOnUiThread(new Runnable(){
						public void run(){
							int position=list.indexOf(sv);
							if(position==-1){
								return;
							}
							layout.findViewById(R.id.statColor).setBackground(new ColorDrawable(getResources().getColor(R.color.stat_ok)));
							final String title;
							Map<String,String> m=sv.response.getData();
							if (m.containsKey("hostname")) {
								title = deleteDecorations(m.get("hostname"));
							} else if (m.containsKey("motd")) {
								title = deleteDecorations(m.get("motd"));
							} else if (m.containsKey("description")) {
								title = deleteDecorations(m.get("description"));
							} else {
								title = sv.ip + ":" + sv.port;
							}
							((TextView)layout.findViewById(R.id.serverName)).setText(deleteDecorations(title));
							((TextView)layout.findViewById(R.id.pingMillis)).setText(sv.ping+" ms");
							list.set(position,sv);
							pinging.put(sv,false);
						}
					});
				}
			});
			((TextView)layout.findViewById(R.id.serverName)).setText(R.string.working);
			((TextView)layout.findViewById(R.id.pingMillis)).setText(R.string.working);
			((TextView)layout.findViewById(R.id.serverAddress)).setText(s.ip+":"+s.port);
			layout.findViewById(R.id.statColor).setBackground(new ColorDrawable(getResources().getColor(R.color.stat_pending)));
			if(cached.size()<=position){
				cached.addAll(Consistant.TEN_LENGTH_NULL_LIST);
			}
			cached.set(position,layout);
			pinging.put(s,true);
			return layout;
		}
		public View getCachedView(int position){
			return cached.get(position);
		}
		public View getViewQuick(int pos){
			return getView(pos,null,null);
		}
		@Override
		public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
			// TODO: Implement this method
			Server s=getItem(p3);
			clicked=p3;
			if(s instanceof ServerStatus){
				ServerInfoActivity.stat=(ServerStatus)s;
				startActivityForResult(new Intent(ServerListActivity.this,ServerInfoActivity.class),0);
			}else{
				if(pinging.get(s))return;
				spp.putInQueue(s,new ServerPingProvider.PingHandler(){
						public void onPingFailed(final Server s){
							runOnUiThread(new Runnable(){
									public void run(){
										sl.getViewQuick(clicked).findViewById(R.id.statColor).setBackground(new ColorDrawable(getResources().getColor(R.color.stat_error)));
										((TextView)sl.getViewQuick(clicked).findViewById(R.id.serverName)).setText(s.ip+":"+s.port);
										((TextView)sl.getViewQuick(clicked).findViewById(R.id.pingMillis)).setText(R.string.notResponding);
										Server sn=new Server();
										sn.ip=s.ip;
										sn.port=s.port;
										sn.isPC=s.isPC;
										list.set(clicked,sn);
										hideWorkingDialog();
										pinging.put(list.get(clicked),false);
									}
								});
						}
						public void onPingArrives(final ServerStatus s){
							runOnUiThread(new Runnable(){
									public void run(){
										sl.getViewQuick(clicked).findViewById(R.id.statColor).setBackground(new ColorDrawable(getResources().getColor(R.color.stat_ok)));
										final String title;
										Map<String,String> m=s.response.getData();
										if (m.containsKey("hostname")) {
											title = deleteDecorations(m.get("hostname"));
										} else if (m.containsKey("motd")) {
											title = deleteDecorations(m.get("motd"));
										} else if (m.containsKey("description")) {
											title = deleteDecorations(m.get("description"));
										} else {
											title = s.ip + ":" + s.port;
										}
										((TextView)sl.getViewQuick(clicked).findViewById(R.id.serverName)).setText(deleteDecorations(title));
										((TextView)sl.getViewQuick(clicked).findViewById(R.id.pingMillis)).setText(s.ping+" ms");
										list.set(clicked,s);
										ServerInfoActivity.stat=s;
										startActivityForResult(new Intent(ServerListActivity.this,ServerInfoActivity.class),0);
										hideWorkingDialog();
										pinging.put(list.get(clicked),false);
									}
								});
						}
					});
				((TextView)sl.getViewQuick(clicked).findViewById(R.id.pingMillis)).setText(R.string.working);
				sl.getViewQuick(clicked).findViewById(R.id.statColor).setBackground(new ColorDrawable(getResources().getColor(R.color.stat_pending)));
				showWorkingDialog();
				pinging.put(list.get(clicked),true);
			}
		}

		@Override
		public boolean onItemLongClick(AdapterView<?> p1, View p2, final int p3, long p4) {
			// TODO: Implement this method
			clicked=p3;
			Dialog d=new AlertDialog.Builder(ServerListActivity.this)
				.setItems(R.array.serverSubMenu,new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface di,int which){
						switch(which){
							case 0:
								new AlertDialog.Builder(ServerListActivity.this)
									.setMessage(R.string.auSure)
									.setNegativeButton(android.R.string.yes,new DialogInterface.OnClickListener(){
										public void onClick(DialogInterface di,int i){
											sl.remove(list.get(clicked));
											saveServers();
										}
									})
									.setPositiveButton(android.R.string.no,new DialogInterface.OnClickListener(){
										public void onClick(DialogInterface di,int i){
										}
									})
									.show();
								break;
							case 1:
								if(pinging.get(getItem(p3)))break;
								spp.putInQueue(getItem(p3),new ServerPingProvider.PingHandler(){
										public void onPingFailed(final Server s){
											runOnUiThread(new Runnable(){
													public void run(){
														sl.getViewQuick(clicked).findViewById(R.id.statColor).setBackground(new ColorDrawable(getResources().getColor(R.color.stat_error)));
														((TextView)sl.getViewQuick(clicked).findViewById(R.id.serverName)).setText(s.ip+":"+s.port);
														((TextView)sl.getViewQuick(clicked).findViewById(R.id.pingMillis)).setText(R.string.notResponding);
														Server sn=new Server();
														sn.ip=s.ip;
														sn.port=s.port;
														sn.isPC=s.isPC;
														list.set(clicked,sn);
														hideWorkingDialog();
														pinging.put(list.get(clicked),false);
													}
												});
										}
										public void onPingArrives(final ServerStatus s){
											runOnUiThread(new Runnable(){
													public void run(){
														sl.getViewQuick(clicked).findViewById(R.id.statColor).setBackground(new ColorDrawable(getResources().getColor(R.color.stat_ok)));
														final String title;
														Map<String,String> m=s.response.getData();
														if (m.containsKey("hostname")) {
															title = deleteDecorations(m.get("hostname"));
														} else if (m.containsKey("motd")) {
															title = deleteDecorations(m.get("motd"));
														} else if (m.containsKey("description")) {
															title = deleteDecorations(m.get("description"));
														} else {
															title = s.ip + ":" + s.port;
														}
														((TextView)sl.getViewQuick(clicked).findViewById(R.id.serverName)).setText(deleteDecorations(title));
														((TextView)sl.getViewQuick(clicked).findViewById(R.id.pingMillis)).setText(s.ping+" ms");
														list.set(clicked,s);
														hideWorkingDialog();
														pinging.put(list.get(clicked),false);
													}
												});
										}
									});
								((TextView)sl.getViewQuick(clicked).findViewById(R.id.pingMillis)).setText(R.string.working);
								sl.getViewQuick(clicked).findViewById(R.id.statColor).setBackground(new ColorDrawable(getResources().getColor(R.color.stat_pending)));
								showWorkingDialog();
								pinging.put(list.get(clicked),true);
								break;
							case 2:
								final Server data=new Server();
								data.ip=getItem(p3).ip;
								data.port=getItem(p3).port;
								data.isPC=getItem(p3).isPC;
								View dialog=getLayoutInflater().inflate(R.layout.serveradddialog,null);
								final EditText ip=(EditText)dialog.findViewById(R.id.serverIp);
								final EditText port=(EditText)dialog.findViewById(R.id.serverPort);
								final CheckBox isPc=(CheckBox)dialog.findViewById(R.id.pc);
								
								ip.setText(data.ip);
								port.setText(data.port+"");
								isPc.setChecked(data.isPC);

								new AlertDialog.Builder(getContext()).
									setView(dialog).
									setPositiveButton(android.R.string.yes,new DialogInterface.OnClickListener(){
										public void onClick(DialogInterface d,int sel){
											data.ip=ip.getText().toString();
											data.port=new Integer(port.getText().toString());
											data.isPC=isPc.isChecked();
											if(list.contains(data)){
												Toast.makeText(ServerListActivity.this,R.string.alreadyExists,Toast.LENGTH_LONG).show();
											}else{
												list.set(p3,data);
												spp.putInQueue(getItem(p3),new ServerPingProvider.PingHandler(){
														public void onPingFailed(final Server s){
															runOnUiThread(new Runnable(){
																	public void run(){
																		sl.getViewQuick(p3).findViewById(R.id.statColor).setBackground(new ColorDrawable(getResources().getColor(R.color.stat_error)));
																		((TextView)sl.getViewQuick(p3).findViewById(R.id.serverName)).setText(s.ip+":"+s.port);
																		((TextView)sl.getViewQuick(p3).findViewById(R.id.pingMillis)).setText(R.string.notResponding);
																		Server sn=new Server();
																		sn.ip=s.ip;
																		sn.port=s.port;
																		sn.isPC=s.isPC;
																		list.set(p3,sn);
																		hideWorkingDialog();
																		pinging.put(list.get(p3),false);
																	}
																});
														}
														public void onPingArrives(final ServerStatus s){
															runOnUiThread(new Runnable(){
																	public void run(){
																		sl.getViewQuick(clicked).findViewById(R.id.statColor).setBackground(new ColorDrawable(getResources().getColor(R.color.stat_ok)));
																		final String title;
																		Map<String,String> m=s.response.getData();
																		if (m.containsKey("hostname")) {
																			title = deleteDecorations(m.get("hostname"));
																		} else if (m.containsKey("motd")) {
																			title = deleteDecorations(m.get("motd"));
																		} else if (m.containsKey("description")) {
																			title = deleteDecorations(m.get("description"));
																		} else {
																			title = s.ip + ":" + s.port;
																		}
																		((TextView)sl.getViewQuick(p3).findViewById(R.id.serverName)).setText(deleteDecorations(title));
																		((TextView)sl.getViewQuick(p3).findViewById(R.id.pingMillis)).setText(s.ping+" ms");
																		list.set(p3,s);
																		hideWorkingDialog();
																		pinging.put(list.get(p3),false);
																	}
																});
														}
													});
												((TextView)sl.getViewQuick(p3).findViewById(R.id.serverAddress)).setText(data.ip + ":" + data.port);
											}
											saveServers();
										}
									}).
									setNegativeButton(android.R.string.no,new DialogInterface.OnClickListener(){
										public void onClick(DialogInterface d,int sel){

										}
									}).
									show();
								break;
							case 3:
								startActivity(new Intent(ServerListActivity.this,ServerTestActivity.class).putExtra("ip",getItem(p3).ip).putExtra("port",getItem(p3).port).putExtra("ispc",getItem(p3).isPC));
								break;
							case 4:
								startActivity(new Intent(ServerListActivity.this,RCONActivity.class).putExtra("ip",getItem(p3).ip).putExtra("port",getItem(p3).port));
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
			if(!list.contains(object))super.add(object);
		}

		@Override
		public void addAll(ServerListActivity.Server[] items) {
			// TODO: Implement this method
			for(Server s:items)add(s);
		}

		@Override
		public void addAll(Collection<? extends ServerListActivity.Server> collection) {
			// TODO: Implement this method
			for(Server s:collection)add(s);
		}

		@Override
		public void remove(ServerListActivity.Server object) {
			// TODO: Implement this method
			cached.remove(list.indexOf(object));
			super.remove(object);
		}
	}
	public static class Server{
		public String ip;
		public int port;
		public boolean isPC;

		@Override
		public int hashCode() {
			// TODO: Implement this method
			return ip.hashCode()^port;
		}

		@Override
		public boolean equals(Object o) {
			// TODO: Implement this method
			if(!(o instanceof Server)){
				return false;
			}
			Server os=(Server)o;
			return os.ip.equals(ip)&os.port==port;
		}
	}
	public static class ServerStatus extends Server{
		public QueryResponseUniverse response;
		public long ping;
	}
}
