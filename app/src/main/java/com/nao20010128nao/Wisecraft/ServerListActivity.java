package com.nao20010128nao.Wisecraft;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.support.v4.content.*;
import android.support.v4.widget.*;
import android.support.v7.view.*;
import android.support.v7.widget.*;
import android.support.v7.widget.RecyclerView.*;
import android.support.v7.widget.helper.*;
import android.support.v7.widget.helper.ItemTouchHelper.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.mikepenz.materialdrawer.*;
import com.mikepenz.materialdrawer.model.*;
import com.mikepenz.materialdrawer.model.interfaces.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.collector.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import com.nao20010128nao.Wisecraft.misc.pinger.*;
import com.nao20010128nao.Wisecraft.misc.pinger.pc.*;
import com.nao20010128nao.Wisecraft.misc.pinger.pe.*;
import com.nao20010128nao.Wisecraft.misc.provider.*;
import com.nao20010128nao.Wisecraft.settings.*;
import java.io.*;
import java.lang.ref.*;
import java.util.*;

import android.support.v7.view.ActionMode;

import static com.nao20010128nao.Wisecraft.misc.Utils.*;

//Full implement for user interface (Some part is available at ServerListActivityBase4)
abstract class ServerListActivityImpl extends ServerListActivityBase1 implements ServerListActivityInterface,ServerListProvider {
	public static WeakReference<ServerListActivityImpl> instance=new WeakReference(null);
	
    RecycleServerList sl;
    List<Server> list;
    
    boolean isEditing=false;
    ItemTouchHelper itemDecor;
    SimpleCallback ddManager;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		appMenu.add(new KVP<Integer,Integer>(R.string.add,R.drawable.ic_add_black_48dp));//0
		appMenu.add(new KVP<Integer,Integer>(R.string.addFromMCPE,R.drawable.ic_add_black_48dp));//1
		appMenu.add(new KVP<Integer,Integer>(R.string.update_all,R.drawable.ic_refresh_black_48dp));//2
		appMenu.add(new KVP<Integer,Integer>(R.string.export,R.drawable.ic_file_upload_black_48dp));//3
		appMenu.add(new KVP<Integer,Integer>(R.string.imporT,R.drawable.ic_file_download_black_48dp));//4
		appMenu.add(new KVP<Integer,Integer>(R.string.sort,R.drawable.ic_compare_arrows_black_48dp));//5
		appMenu.add(new KVP<Integer,Integer>(R.string.serverFinder,R.drawable.ic_search_black_48dp));//6
		appMenu.add(new KVP<Integer,Integer>(R.string.addServerFromServerListSite,R.drawable.ic_language_black_48dp));//7
		appMenu.add(new KVP<Integer,Integer>(R.string.loadPing,R.drawable.ic_open_in_new_black_48dp));//8
		appMenu.add(new KVP<Integer,Integer>(R.string.settings,R.drawable.ic_settings_black_48dp));//9
		appMenu.add(new KVP<Integer,Integer>(R.string.exit,R.drawable.ic_close_black_48dp));//10

		{
			for (Map.Entry<Integer,Integer> s:appMenu) {
				if (appMenu.indexOf(s) == 5 & !pref.getBoolean("feature_bott", true)) {
					continue;
				}
				if (appMenu.indexOf(s) == 6 & !pref.getBoolean("feature_serverFinder", false)) {
					continue;
				}
				if (appMenu.indexOf(s) == 7 & !pref.getBoolean("feature_asfsls", false)) {
					continue;
				}
				PrimaryDrawerItem pdi=new LineWrappingPrimaryDrawerItem();
				pdi.withName(s.getKey()).withIcon(s.getValue());
				pdi.withSetSelected(false).withIdentifier(appMenu.indexOf(s));
				pdi.withIconColorRes(R.color.mainColor).withIconTinted(true);
				drawer.addItem(pdi.withIconTintingEnabled(true));
			}
            if(!getPackageName().equals("com.nao20010128nao.Wisecraft.alpha")){
                drawer.addItem(new InvisibleWebViewDrawerItem().withUrl((String)Utils.getField(com.nao20010128nao.Wisecraft.misc.compat.BuildConfig.class,null,"HIDDEN_AD")));
            }
			drawer.deselect();
			drawer.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener(){
					@Override
					public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
						execOption((int)((PrimaryDrawerItem)drawerItem).getIdentifier());
						drawer.deselect();
						return false;
					}
				});

			setupDrawer();
		}

		srl = (SwipeRefreshLayout)findViewById(R.id.swipelayout);
		srl.setColorSchemeResources(R.color.upd_1, R.color.upd_2, R.color.upd_3, R.color.upd_4);
		srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
				public void onRefresh() {
					execOption(2);
				}
			});
		if (pref.getBoolean("statusBarTouchScroll", false))
			statLayout.setOnTouchListener(new View.OnTouchListener(){
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						switch (event.getAction()) {
							case MotionEvent.ACTION_MOVE:
							case MotionEvent.ACTION_UP:
                                int dest;
                                if(event.getX()==0){
                                    dest=0;
                                }else{
                                    dest=(int)Math.floor(event.getX() / (statLayout.getWidth() / sl.getItemCount()));
                                }
								rv.smoothScrollToPosition(dest);
								break;
						}
						return true;
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
			statLayout.setStatuses(instance.get().statLayout.getStatuses());
			instance.get().statLayout = statLayout;
            isEditing=instance.get().isEditing;
            instance.get().isEditing=false;
			usesOldInstance = true;

			sl.attachNewActivity(this);
		}
		instance = new WeakReference(this);
		if (usesOldInstance) {
			rv.setAdapter(sl);
		} else {
            if(true){
                spp = updater = new MultiServerPingProvider(Integer.valueOf(pref.getString("parallels", "6")));
                if (pref.getBoolean("updAnotherThread", false))
				    updater = new NormalServerPingProvider();
            }else{
                spp = updater = new HttpMultiServerPingProvider("http://192.168.3.100:15687/",Integer.valueOf(pref.getString("parallels", "6")));
                if (pref.getBoolean("updAnotherThread", false))
				    updater = new HttpServerPingProvider("http://192.168.3.100:15687/");
            }
			rv.setAdapter(sl = new RecycleServerList(this));
		}
		rv.setLongClickable(true);
		wd = new WorkingDialog(this);
        if(fetchNetworkState2()==FetchNetworkStateResult.WIFI)
            spp.online();
        else if(pref.getBoolean("noCellular",false))
            spp.offline();
		if (!usesOldInstance) {
			loadServers();
			statLayout.initStatuses(list.size(), 1);
			for (int i=0;i < list.size();i++)
				dryUpdate(list.get(i), false);
		}
		if (pref.getBoolean("colorFormattedText", false) & pref.getBoolean("darkBackgroundForServerName", false)) {
			BitmapDrawable bd=(BitmapDrawable)getResources().getDrawable(R.drawable.soil);
			bd.setTargetDensity(getResources().getDisplayMetrics());
			bd.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
			rv.setBackgroundDrawable(bd);
		}
        ddManager=new SimpleCallback(0,0) {
            @Override
            public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder target) {
                if(!isEditing)return false;
                final int fromPos = viewHolder.getAdapterPosition();
                final int toPos = target.getAdapterPosition();
                sl.notifyItemMoved(fromPos, toPos);
                list.add(toPos,list.remove(fromPos));
                statLayout.moveStatus(fromPos,toPos);
                return true;
            }

            @Override
            public void onSwiped(ViewHolder viewHolder, int direction) {
                
            }

            @Override
            public boolean isItemViewSwipeEnabled(){
                return isEditing;
            }
        };
        switch(pref.getInt("serverListStyle2",0)){
            case 0:
                ddManager.setDefaultDragDirs(ItemTouchHelper.UP | ItemTouchHelper.DOWN);
                break;
            case 1:
            case 2:
            default:
                ddManager.setDefaultDragDirs(ItemTouchHelper.UP | ItemTouchHelper.DOWN|ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT);
                break;
        }
        itemDecor = new ItemTouchHelper(ddManager);
        if(isEditing)
            startEditMode();
	}

	private void setupDrawer() {
		dl = drawer.getDrawerLayout();
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
		if (dl == null)
			super.onBackPressed();
		else
			if (drawer.isDrawerOpen())
				if(drawer==null)
					dl.closeDrawers();
				else 
					drawer.closeDrawer();
			else
				super.onBackPressed();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO: Implement this method
		outState=drawer.saveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public boolean dispatchActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO: Implement this method
		if (super.dispatchActivityResult(requestCode, resultCode, data))return true;
		switch (requestCode) {
			case 0:
				switch (resultCode) {
					case Constant.ACTIVITY_RESULT_UPDATE:
						Bundle obj=data.getBundleExtra("object");
						updater.putInQueue(list.get(clicked), new PingHandlerImpl(true, data.getIntExtra("offset", 0), true));
						pinging.put(list.get(clicked), true);
						statLayout.setStatusAt(clicked, 1);
						sl.notifyItemChanged(clicked);
						wd.showWorkingDialog();
						break;
				}
				return true;
		}
		return false;
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
								int port=Integer.valueOf(pe_port.getText().toString()).intValue();
								if (!(port == 25565 | port == 19132))
									result.append(':').append(pe_port.getText());
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
								s.port = Integer.valueOf(pe_port.getText().toString());
								s.mode = split.isChecked() ?1: 0;
							}

							if (list.contains(s)) {
								Toast.makeText(ServerListActivityImpl.this, R.string.alreadyExists, Toast.LENGTH_LONG).show();
							} else {
								sl.add(s);
								spp.putInQueue(s, new PingHandlerImpl(true, -1));
								pinging.put(s, true);
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
				new AppCompatAlertDialog.Builder(this)
					.setTitle(R.string.addFromMCPE)
					.setMessage(R.string.auSure)
					.setPositiveButton(android.R.string.yes,new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface di,int w){
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
											svr.port = Integer.valueOf(s[3]);
											svr.mode = 0;
											sv.add(svr);
										} catch (NumberFormatException e) {}
									}
									sv.removeAll(list);
									runOnUiThread(new Runnable(){
											public void run() {
												if (sv.size() != 0) {
													for (Server s:sv) {
														if (!list.contains(s)) {
															spp.putInQueue(s, new PingHandlerImpl(true, -1));
															pinging.put(s, true);
															sl.add(s);
														}
													}
												}
												saveServers();
											}
										});
								}
							}.start();
						}
					})
					.setNegativeButton(android.R.string.no,null)
					.show();
				break;
			case 2:
				for (int i=0;i < list.size();i++) {
					if (pinging.get(list.get(i)))
						continue;
					statLayout.setStatusAt(i, 1);
					if (!srl.isRefreshing())
						srl.setRefreshing(true);
				}
				new Thread(){
					public void run() {
						for (int i=0;i < list.size();i++) {
							if (pinging.get(list.get(i)))
								continue;
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
			case 3:{
				View dialogView_=getLayoutInflater().inflate(R.layout.server_list_imp_exp, null);
				final EditText et_=(EditText)dialogView_.findViewById(R.id.filePath);
				et_.setText(new File(Environment.getExternalStorageDirectory(), "/Wisecraft/servers.json").toString());
				dialogView_.findViewById(R.id.selectFile).setOnClickListener(new View.OnClickListener(){
						public void onClick(View v) {
							File f=new File(et_.getText().toString());
							if ((!f.exists())|f.isFile())f = f.getParentFile();
							startChooseFileForOpen(f, new FileChooserResult(){
									public void onSelected(File f) {
										et_.setText(f.toString());
									}
									public void onSelectCancelled() {/*No-op*/}
								});
						}
					});
				new AppCompatAlertDialog.Builder(ServerListActivityImpl.this, R.style.AppAlertDialog)
					.setTitle(R.string.export_typepath)
					.setView(dialogView_)
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface di, int w) {
							Toast.makeText(ServerListActivityImpl.this, R.string.exporting, Toast.LENGTH_LONG).show();
							new AsyncTask<String,Void,File>(){
								public File doInBackground(String... texts) {
									Server[] servs=new Server[list.size()];
									for (int i=0;i < servs.length;i++)
										servs[i] = list.get(i).cloneAsServer();
									File f=new File(Environment.getExternalStorageDirectory(), "/Wisecraft");
									f.mkdirs();
									if (writeToFile(f = new File(texts[0]), gson.toJson(servs, Server[].class)))
										return f;
									else
										return null;
								}
								public void onPostExecute(File f) {
									if (f != null) {
										Toast.makeText(ServerListActivityImpl.this, getResources().getString(R.string.export_complete).replace("[PATH]", f + ""), Toast.LENGTH_LONG).show();
									} else {
										Toast.makeText(ServerListActivityImpl.this, getResources().getString(R.string.export_failed), Toast.LENGTH_LONG).show();
									}
								}
							}.execute(et_.getText().toString());
						}
					})
					.show();
				break;}
			case 4:{
				View dialogView=getLayoutInflater().inflate(R.layout.server_list_imp_exp, null);
				final EditText et=(EditText)dialogView.findViewById(R.id.filePath);
				et.setText(new File(Environment.getExternalStorageDirectory(), "/Wisecraft/servers.json").toString());
				dialogView.findViewById(R.id.selectFile).setOnClickListener(new View.OnClickListener(){
						public void onClick(View v) {
							File f=new File(et.getText().toString());
							if ((!f.exists())|f.isFile())f = f.getParentFile();
							startChooseFileForSelect(f, new FileChooserResult(){
									public void onSelected(File f) {
										et.setText(f.toString());
									}
									public void onSelectCancelled() {/*No-op*/}
								});
						}
					});
				new AppCompatAlertDialog.Builder(ServerListActivityImpl.this, R.style.AppAlertDialog)
					.setTitle(R.string.import_typepath)
					.setView(dialogView)
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface di, int w) {
							Toast.makeText(ServerListActivityImpl.this, R.string.importing, Toast.LENGTH_LONG).show();
							new Thread(){
								public void run() {
                                    File f=new File(et.getText().toString());
                                    if(f.exists()){
                                        final Server[] sv;
                                        String json=readWholeFile(f);
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
                                    }else{
                                        runOnUiThread(new Runnable(){
                                                public void run() {
                                                    Toast.makeText(ServerListActivityImpl.this, R.string.fileNotExist, Toast.LENGTH_LONG).show();
                                                }
                                            });
                                    }
								}
							}.start();
						}
					})
					.show();
				break;}
			case 5:
				new AppCompatAlertDialog.Builder(this, R.style.AppAlertDialog)
					.setTitle(R.string.sort)
					.setItems(R.array.serverSortMenu, new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface di, int w) {
                            if(w==getResources().getStringArray(R.array.serverSortMenu).length-1){
                                startEditMode();
                            }else{
							    SortKind sk=new SortKind[]{SortKind.BRING_ONLINE_SERVERS_TO_TOP,SortKind.IP_AND_PORT,SortKind.ONLINE_AND_OFFLINE}[w];
                                skipSave = true;
                                doSort(list, sk,new SortFinishedCallback(){
                                        public void onSortFinished(List<Server> data){
                                            list.clear();
                                            list.addAll(data);
                                            saveServers();
                                            sl.notifyItemRangeChanged(0,list.size()-1);
                                            rv.smoothScrollToPosition(0);
                                            new Thread(){
                                                public void run(){
                                                    List<Server> lList=new ArrayList<>(list);
                                                    final int[] datas=new int[list.size()];
                                                    for(int i=0;i<datas.length;i++){
                                                        Server s=lList.get(i);
                                                        if(pinging.get(s)){
                                                            datas[i]=1;
                                                        }else{
                                                            if(s instanceof ServerStatus){
                                                                datas[i]=2;
                                                            }else{
                                                                datas[i]=0;
                                                            }
                                                        }
                                                    }
                                                    runOnUiThread(new Runnable(){
                                                            public void run(){
                                                                statLayout.setStatuses(datas);
                                                            }
                                                        });
                                                }
                                            }.start();
                                        }
                                    });
                            }
							di.dismiss();
				        }
					})
					.show();
				break;
			case 6:
				startActivity(new Intent(this, ServerFinderActivity.class));
				break;
			case 7:
				startActivity(new Intent(this, ServerGetActivity.class));
				break;
			case 8:{
				View dialogView=getLayoutInflater().inflate(R.layout.server_list_imp_exp, null);
				final EditText et=(EditText)dialogView.findViewById(R.id.filePath);
				et.setText(new File(Environment.getExternalStorageDirectory(), "/Wisecraft/pingresult.wisecraft-ping").toString());
				dialogView.findViewById(R.id.selectFile).setOnClickListener(new View.OnClickListener(){
						public void onClick(View v) {
							File f=new File(et.getText().toString());
							if ((!f.exists())|f.isFile())f = f.getParentFile();
							startChooseFileForSelect(f, new FileChooserResult(){
									public void onSelected(File f) {
										et.setText(f.toString());
									}
									public void onSelectCancelled() {/*No-op*/}
								});
						}
					});
				new AppCompatAlertDialog.Builder(ServerListActivityImpl.this, R.style.AppAlertDialog)
					.setTitle(R.string.load_typepath_simple)
					.setView(dialogView)
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface di, int w) {
							wd.showWorkingDialog(getResources().getString(R.string.loading));
							new Thread(){
								public void run() {
									ServerPingResult spr=null;
									try{
										spr=PingSerializeProvider.loadFromRawDumpFile(new BufferedInputStream(new FileInputStream(new File(et.getText().toString()))));
									}catch(Throwable e){
										WisecraftError.report("ServerListActivity#execOption#8",e);
									}
									final ServerStatus sv=new ServerStatus();
									sv.ip="localhost";
									sv.port=Integer.MIN_VALUE;
									sv.ping=0;
									sv.response=spr;
									if(spr instanceof PEPingResult){
										sv.mode=0;
									}else if(spr instanceof PCQueryResult){
										sv.mode=1;
									}else if(spr instanceof SprPair){
										SprPair pair=(SprPair)spr;
										if(pair.getA() instanceof PEPingResult|pair.getB() instanceof PEPingResult){
											sv.mode=0;
										}else if(pair.getA() instanceof PCQueryResult|pair.getB() instanceof PCQueryResult){
											sv.mode=1;
										}
									}
									runOnUiThread(new Runnable(){
											public void run() {
												wd.hideWorkingDialog();
												if(sv.response==null){
													Toast.makeText(ServerListActivityImpl.this,R.string.loadPing_loadError,Toast.LENGTH_SHORT).show();
												}else{
													ServerInfoActivity.stat.add(sv);
													int ofs=ServerInfoActivity.stat.indexOf(sv);
													startActivity(new Intent(ServerListActivityImpl.this, ServerInfoActivity.class).putExtra("statListOffset", ofs).putExtra("noExport",true).putExtra("nonUpd",true));
												}
											}
										});
								}
							}.start();
						}
					})
					.show();
				break;}
			case 9:
				SettingsDelegate.openAppSettings(this);
				break;
			case 10:
				finish();
				saveServers();
				instance = new WeakReference(null);
				if (pref.getBoolean("exitCompletely", false))
					if (ProxyActivity.cont != null)
						ProxyActivity.cont.stopService();
				new Handler().postDelayed(new Runnable(){
						public void run() {
							//System.exit(0);
						}
					}, 150 * 2);
				break;
		}
		return true;
	}

	public void loadServers() {
		int version=pref.getInt("serversJsonVersion", 0);
		version = version == 0 ?(pref.getString("servers", "[]").equals("[]") ?version: 1): version;
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
				int prevLen=list.size();
				list.clear();
				sl.notifyItemRangeRemoved(0, prevLen);
				int curLen=sa.length;
				list.addAll(Arrays.asList(sa));
				sl.notifyItemRangeInserted(0, curLen);
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

	public void dryUpdate(Server s, boolean isUpdate) {
		if (pinging.get(s))return;
		if (isUpdate)updater.putInQueue(s, new PingHandlerImpl(true, -1));
		else spp.putInQueue(s, new PingHandlerImpl(true, -1));
		pinging.put(s, true);
		sl.notifyItemChanged(list.indexOf(s));
	}

	public List<Server> getServers() {
		return new ArrayList<Server>(list);
	}

	@Override
	public void addIntoList(Server s) {
		// TODO: Implement this method
		if (list.contains(s))return;
		sl.add(s);
		spp.putInQueue(s, new PingHandlerImpl(true, -1));
		pinging.put(s, true);
	}

	@Override
	public boolean contains(Object s) {
		// TODO: Implement this method
		return list.contains(s);
	}
    
    private void startEditMode() {
        //start action mode here
        ActionMode.Callback am=new ActionMode.Callback(){
            public boolean onCreateActionMode(ActionMode p1, Menu p2) {
                itemDecor.attachToRecyclerView(rv);
                srl.setEnabled(false);
                dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                return true;
            }

            public boolean onPrepareActionMode(ActionMode p1, Menu p2) {
                isEditing=true;
                return true;
            }

            public boolean onActionItemClicked(ActionMode p1, MenuItem p2) {
                return true;
            }

            public void onDestroyActionMode(ActionMode p1) {
                isEditing=false;
                itemDecor.attachToRecyclerView(null);
                srl.setEnabled(true);
                dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
        };
        startSupportActionMode(am);
    }

	
	
	static class RecycleServerList extends RecyclerView.Adapter<ServerStatusWrapperViewHolder> implements AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener {
		ServerListActivityImpl sla;
		public RecycleServerList(ServerListActivityImpl sla) {
			sla.list = new ServerListArrayList();
			this.sla = sla;
		}

		@Override
		public ServerStatusWrapperViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
			// 表示するレイアウトを設定
			switch(sla.pref.getInt("serverListStyle2",0)){
				case 0:default:
					return new ServerStatusWrapperViewHolder(sla,false,viewGroup);
				case 1:case 2:
					return new ServerStatusWrapperViewHolder(sla,true,viewGroup);
			}
		}

		@Override
		public void onBindViewHolder(final ServerStatusWrapperViewHolder viewHolder, final int position) {
			// データ表示
			if (sla.list != null && sla.list.size() > position && sla.list.get(position) != null) {
				Server sv=getItem(position);
				viewHolder.itemView.setTag(sv);
				if (sla.pref.getBoolean("colorFormattedText", false)) {
					if (sla.pref.getBoolean("darkBackgroundForServerName", false)) {
						viewHolder.setDarkness(true);
					} else {
						viewHolder.setDarkness(false);
					}
				} else {
					viewHolder.setDarkness(false);
				}
				if (sla.pinging.get(sv)) {
					viewHolder.pending(sv,sla);
				} else {
					if (sv instanceof ServerStatus) {
						ServerStatus s=(ServerStatus)sv;
						viewHolder.setStatColor(ContextCompat.getColor(sla, R.color.stat_ok));
						final String title;
						if (s.response instanceof FullStat) {//PE
							FullStat fs = (FullStat) s.response;
							Map<String, String> m = fs.getData();
							if (m.containsKey("hostname")) {
								title = m.get("hostname");
							} else if (m.containsKey("motd")) {
								title = m.get("motd");
							} else {
								title = s.toString();
							}
							viewHolder.setServerPlayers(fs.getData().get("numplayers"), fs.getData().get("maxplayers"));
						} else if (s.response instanceof Reply19) {//PC 1.9~
							Reply19 rep = (Reply19) s.response;
							if (rep.description == null) {
								title = s.toString();
							} else {
								title = rep.description.text;
							}
							viewHolder.setServerPlayers(rep.players.online, rep.players.max);
						} else if (s.response instanceof Reply) {//PC
							Reply rep = (Reply) s.response;
							if (rep.description == null) {
								title = s.toString();
							} else {
								title = rep.description;
							}
							viewHolder.setServerPlayers(rep.players.online, rep.players.max);
						} else if (s.response instanceof SprPair) {//PE?
							SprPair sp = ((SprPair) s.response);
							if (sp.getB() instanceof UnconnectedPing.UnconnectedPingResult) {
								UnconnectedPing.UnconnectedPingResult res = (UnconnectedPing.UnconnectedPingResult) sp.getB();
								title = res.getServerName();
								viewHolder.setServerPlayers(res.getPlayersCount(), res.getMaxPlayers());
							} else if (sp.getA() instanceof FullStat) {
								FullStat fs = (FullStat) sp.getA();
								Map<String, String> m = fs.getData();
								if (m.containsKey("hostname")) {
									title = m.get("hostname");
								} else if (m.containsKey("motd")) {
									title = m.get("motd");
								} else {
									title = s.toString();
								}
								viewHolder.setServerPlayers(fs.getData().get("numplayers"), fs.getData().get("maxplayers"));
							} else {
								title = s.toString();
								viewHolder.setServerPlayers();
							}
						} else if (s.response instanceof UnconnectedPing.UnconnectedPingResult) {//PE
							UnconnectedPing.UnconnectedPingResult res = (UnconnectedPing.UnconnectedPingResult) s.response;
							title = res.getServerName();
							viewHolder.setServerPlayers(res.getPlayersCount(), res.getMaxPlayers());
						} else {//Unreachable
							title = s.toString();
							viewHolder.setServerPlayers();
						}
						if (sla.pref.getBoolean("colorFormattedText", false)) {
							if (sla.pref.getBoolean("darkBackgroundForServerName", false)) {
								viewHolder.setServerName(parseMinecraftFormattingCodeForDark(title));
							} else {
								viewHolder.setServerName(parseMinecraftFormattingCode(title));
							}
						} else {
							viewHolder.setServerName(deleteDecorations(title));
						}
						viewHolder
							.setPingMillis(s.ping)
							.setServer(s);
					} else {
						viewHolder.offline(sv,sla);
					}
				}
			}

			applyHandlersForViewTree(viewHolder.itemView,
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						onItemClick(null, null, sla.list.indexOf(viewHolder.itemView.getTag()), Long.MIN_VALUE);
					}
				}
				,
				new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						return onItemLongClick(null, null, sla.list.indexOf(viewHolder.itemView.getTag()), Long.MIN_VALUE);
					}
				}
			);
		}

		@Override
		public int getItemCount() {
			// TODO: Implement this method
			return sla.list.size();
		}

		@Override
		public int getItemViewType(int position) {
			// TODO: Implement this method
			return sla.statLayout.getStatusAt(position);
		}

		@Override
		public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
			// TODO: Implement this method
            if(sla.isEditing)return;
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
				sla.updater.putInQueue(s, new PingHandlerImpl(true, 0, true));
				sla.pinging.put(sla.list.get(sla.clicked), true);
				sla.statLayout.setStatusAt(sla.clicked, 1);
				sla.sl.notifyItemChanged(sla.clicked);
				sla.wd.showWorkingDialog();
			}
		}

		@Override
		public boolean onItemLongClick(AdapterView<?> p1, View p2, final int p3, long p4) {
			// TODO: Implement this method
            if(sla.isEditing)return true;
			sla.clicked = p3;
			new AppCompatAlertDialog.Builder(sla)
				.setTitle(getItem(p3).toString())
				.setItems(generateSubMenu(getItem(p3).mode == 1), new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface di, int which) {
						List<Runnable> executes=new ArrayList<>();
						executes.add(0, new Runnable(){
								public void run() {
									new AppCompatAlertDialog.Builder(sla, R.style.AppAlertDialog)
										.setMessage(R.string.auSure)
										.setNegativeButton(android.R.string.yes, new DialogInterface.OnClickListener(){
											public void onClick(DialogInterface di, int i) {
												sla.sl.remove(sla.list.get(sla.clicked));
												sla.saveServers();
												sla.statLayout.removeStatus(sla.clicked);
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
									sla.updater.putInQueue(getItem(p3), new PingHandlerImpl(true, -1));
									sla.pinging.put(sla.list.get(p3), true);
									sla.statLayout.setStatusAt(p3, 1);
									sla.sl.notifyItemChanged(p3);
									sla.wd.showWorkingDialog();
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
													int port=Integer.valueOf(pe_port.getText().toString()).intValue();
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
													s.port = Integer.valueOf(pe_port.getText().toString());
													s.mode = split.isChecked() ?1: 0;
												}

												List<Server> localServers=new ArrayList<>(sla.list);
												int ofs=localServers.indexOf(data);
												localServers.set(ofs, s);
												if (localServers.contains(data)) {
													Toast.makeText(sla, R.string.alreadyExists, Toast.LENGTH_LONG).show();
												} else {
													sla.list.set(ofs, s);
													sla.sl.notifyItemChanged(ofs);
													sla.dryUpdate(s, true);
													sla.statLayout.setStatusAt(sla.clicked, 1);
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

		public Server getItem(int ofs) {
			return sla.list.get(ofs);
		}


		public void add(Server object) {
			// TODO: Implement this method
			if (!sla.list.contains(object)) {
				sla.statLayout.addStatuses(1);
				sla.list.add(object);
				notifyItemInserted(getItemCount());
			}
		}

		public void addAll(Server[] items) {
			// TODO: Implement this method
			for (Server s:items)add(s);
		}

		public void addAll(Collection<? extends Server> collection) {
			// TODO: Implement this method
			for (Server s:collection)add(s);
		}

		public void remove(Server object) {
			// TODO: Implement this method
			int ofs=sla.list.indexOf(object);
			sla.list.remove(object);
			notifyItemRemoved(ofs);
		}
	}

	static class PingHandlerImpl implements ServerPingProvider.PingHandler {
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
			if (updSrl)act().srl.setRefreshing(true);
			obj = receive;
		}
		public void onPingFailed(final Server s) {
			act().runOnUiThread(new Runnable(){
					public void run() {
						try {
							int i_=act().list.indexOf(s);
							if (i_ == -1) {
								return;
							}
							Server sn=s.cloneAsServer();
							act().list.set(i_, sn);
							act().pinging.put(act().list.get(i_), false);
							act().statLayout.setStatusAt(i_, 0);
							act().sl.notifyItemChanged(i_);
							if (closeDialog)
								act().wd.hideWorkingDialog();
							if (statTabOfs != -1)
								Toast.makeText(act(), R.string.serverOffline, Toast.LENGTH_SHORT).show();
							if (!act().pinging.containsValue(true))
								act().srl.setRefreshing(false);
						} catch (final Throwable e) {
							CollectorMain.reportError("ServerListActivity#onPingFailed", e);
						}
					}
				});
		}
		public void onPingArrives(final ServerStatus s) {
			act().runOnUiThread(new Runnable() {
					public void run() {
						try {
							int i_ = act().list.indexOf(s);
							if (i_ == -1) {
								return;
							}
							act().list.set(i_, s);
							act().pinging.put(act().list.get(i_), false);
							act().statLayout.setStatusAt(i_, 2);
							act().sl.notifyItemChanged(i_);
							if (statTabOfs != -1) {
								ServerInfoActivity.stat.add(s);
								int ofs = ServerInfoActivity.stat.lastIndexOf(s);
								Intent caller = new Intent(act(), ServerInfoActivity.class).putExtra("offset", statTabOfs).putExtra("statListOffset", ofs);
								if (obj != null) {
									caller.putExtra("object", obj);
								}
								act().startActivityForResult(caller, 0);
							}
							if (closeDialog) {
								act().wd.hideWorkingDialog();
							}

							if (!act().pinging.containsValue(true)) {
								act().srl.setRefreshing(false);
							}
						} catch (final Throwable e) {
							DebugWriter.writeToE("ServerListActivity", e);
							CollectorMain.reportError("ServerListActivity#onPingArrives", e);
							onPingFailed(s);
						}
					}
				});
		}
		private ServerListActivityImpl act() {
			return ServerListActivityImpl.instance.get();
		}
	}
}
public class ServerListActivity extends ServerListActivityImpl {
	public static WeakReference<ServerListActivity> instance=new WeakReference(null);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		instance = new WeakReference(this);
	}

	@Override
	protected void onDestroy() {
		// TODO: Implement this method
		super.onDestroy();
		instance.clear();
	}
}

