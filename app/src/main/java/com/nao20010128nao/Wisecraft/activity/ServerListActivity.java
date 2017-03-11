package com.nao20010128nao.Wisecraft.activity;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.app.*;
import android.support.v4.content.*;
import android.support.v4.util.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.view.*;
import android.support.v7.widget.*;
import android.support.v7.widget.RecyclerView.*;
import android.support.v7.widget.helper.*;
import android.support.v7.widget.helper.ItemTouchHelper.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.google.gson.reflect.*;
import com.mikepenz.materialdrawer.*;
import com.mikepenz.materialdrawer.model.interfaces.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.collector.*;
import com.nao20010128nao.Wisecraft.misc.contextwrappers.extender.*;
import com.nao20010128nao.Wisecraft.misc.pinger.*;
import com.nao20010128nao.Wisecraft.misc.pinger.pc.*;
import com.nao20010128nao.Wisecraft.misc.pinger.pe.*;
import com.nao20010128nao.Wisecraft.misc.provider.*;
import com.nao20010128nao.Wisecraft.misc.serverList.*;
import com.nao20010128nao.Wisecraft.settings.*;
import java.io.*;
import java.lang.ref.*;
import java.util.*;
import permissions.dispatcher.*;

import android.support.v4.util.Pair;
import android.support.v7.view.ActionMode;
import com.nao20010128nao.Wisecraft.BuildConfig;
import com.nao20010128nao.Wisecraft.R;

import static com.nao20010128nao.Wisecraft.misc.Utils.*;

//Full implement for user interface (Some part is available at ServerListActivityBase4)
@RuntimePermissions
@ShowsServerList
abstract class ServerListActivityImpl extends ServerListActivityBase1 implements ServerListActivityInterface,ServerListProvider {
	public static WeakReference<ServerListActivityImpl> instance=new WeakReference(null);
	
    ServerList sl;
    List<Server> list;
	ServerListStyleLoader slsl;
	Set<Server> selected=new HashSet<>();
	Map<Server,Map.Entry<Boolean,Integer>> retrying=new HashMap<>();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ThemePatcher.applyThemeForActivity(this);
		super.onCreate(savedInstanceState);
		loadMenu();

		{
			for (Quintet<Integer,Integer,Treatment<ServerListActivity>,Treatment<ServerListActivity>,IDrawerItem> s:appMenu) {
				LineWrappingPrimaryDrawerItem pdi=new LineWrappingPrimaryDrawerItem();
				pdi.withName(s.getA()).withIcon(s.getB());
				pdi.withSetSelected(false);((IdContainer)pdi).setIntId(appMenu.indexOf(s));
				pdi.withIconColor(ThemePatcher.getMainColor(this)).withIconTinted(true);
                pdi.withIdentifier(appMenu.indexOf(s)).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener(){
						@Override
						public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
							appMenu.findByE(drawerItem).getC().process(ServerListActivity.instance.get());
							return false;
						}
					});
				drawer.addItem(pdi.withIconTintingEnabled(true));
				s.setE(pdi);
			}
            drawer.addItem(new InvisibleWebViewDrawerItem().withUrl(BuildConfig.HIDDEN_AD));
			drawer.deselect();
			drawer.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener(){
					@Override
					public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
						drawer.deselect();
						drawer.closeDrawer();
						return false;
					}
				});
			drawer.setOnDrawerItemLongClickListener(new Drawer.OnDrawerItemLongClickListener(){
					public boolean onItemLongClick(View p1, int p2, IDrawerItem p3) {
						Treatment<ServerListActivity> process=appMenu.findByE(p3).getD();
						if (process != null)process.process(ServerListActivity.instance.get());
						return false;
					}
				});
			setupDrawer();
		}

		srl = (SwipeRefreshLayout)findViewById(R.id.swipelayout);
		//srl.setColorSchemeResources(R.color.upd_1, R.color.upd_2, R.color.upd_3, R.color.upd_4);
		srl.setColorSchemeColors(Utils.getHueRotatedColors());
		srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
				public void onRefresh() {
					appMenu.findByA(R.string.update_all).getC().process(ServerListActivity.instance.get());
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
                                if (event.getX() == 0) {
                                    dest = 0;
                                } else {
                                    dest = (int)Math.floor(event.getX() * sl.getItemCount() / statLayout.getWidth());
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
            editMode = instance.get().editMode;
			selected = instance.get().selected;
            instance.get().editMode = EDIT_MODE_NULL;
			retrying = instance.get().retrying;
			usesOldInstance = true;

			sl.attachNewActivity(this);
			
			instance.clear();
		}
		instance = new WeakReference(this);
		slsl = (ServerListStyleLoader)getSystemService(ContextWrappingExtender.SERVER_LIST_STYLE_LOADER);
		if (usesOldInstance) {
			rv.setAdapter(sl);
		} else {
            if (!pref.getBoolean("useAltServer",false)) {
                spp = updater = new SinglePoolMultiServerPingProvider(Integer.valueOf(pref.getString("parallels", "6")));
                if (pref.getBoolean("updAnotherThread", false))
				    updater = new NormalServerPingProvider();
            } else {
                spp = updater = new TcpMultiServerPingProvider("160.16.103.57",15687, Integer.valueOf(pref.getString("parallels", "6")));
                if (pref.getBoolean("updAnotherThread", false))
				    updater = new TcpServerPingProvider("160.16.103.57",15687);
            }
			rv.setAdapter(sl = new ServerList(this));
		}
		rv.setLongClickable(true);
		wd = new WorkingDialog(this);
        if (fetchNetworkState2() == FetchNetworkStateResult.WIFI)
            spp.online();
        else if (pref.getBoolean("noCellular", false))
            spp.offline();
		if (!usesOldInstance) {
			loadServers();
			statLayout.initStatuses(list.size(), 1);
			for (int i=0;i < list.size();i++)
				dryUpdate(list.get(i), false);
		}
		findViewById(android.R.id.content).setBackgroundDrawable(slsl.load());
        ddManager = new SimpleCallback(0, 0) {
            @Override
            public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder target) {
                if (editMode == EDIT_MODE_NULL)return false;
                final int fromPos = viewHolder.getAdapterPosition();
                final int toPos = target.getAdapterPosition();
                sl.notifyItemMoved(fromPos, toPos);
                list.add(toPos, list.remove(fromPos));
                statLayout.moveStatus(fromPos, toPos);
                return true;
            }

            @Override
            public void onSwiped(ViewHolder viewHolder, int direction) {

            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return false;
            }
			@Override
            public boolean isLongPressDragEnabled() {
				return editMode != EDIT_MODE_NULL;
			}
        };
        handMoveAm = new ActionMode.Callback(){
            public boolean onCreateActionMode(ActionMode p1, Menu p2) {
                itemDecor.attachToRecyclerView(rv);
                srl.setEnabled(false);
                dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
				isInSelectMode=false;
                return true;
            }

            public boolean onPrepareActionMode(ActionMode p1, Menu p2) {
                editMode = EDIT_MODE_EDIT;
                return true;
            }

            public boolean onActionItemClicked(ActionMode p1, MenuItem p2) {
                return true;
            }

            public void onDestroyActionMode(ActionMode p1) {
                editMode = EDIT_MODE_NULL;
                itemDecor.attachToRecyclerView(null);
                srl.setEnabled(true);
                dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                saveServers();
				isInSelectMode=false;
            }
        };
		selectUpdateAm = new ActionMode.Callback(){
            public boolean onCreateActionMode(ActionMode p1, Menu p2) {
                srl.setEnabled(false);
                dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
				selected.clear();
				isInSelectMode=true;
                return true;
            }

            public boolean onPrepareActionMode(ActionMode p1, Menu p2) {
                editMode = EDIT_MODE_SELECT_UPDATE;
				MenuItem mi=p2.add(Menu.NONE,0,0,R.string.update).setIcon(TheApplication.instance.getTintedDrawable(R.drawable.ic_refresh_black_48dp,Utils.getMenuTintColor(ServerListActivityImpl.this)));
				MenuItemCompat.setShowAsAction(mi,MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
                return true;
            }

            public boolean onActionItemClicked(final ActionMode p1, MenuItem p2) {
				switch(p2.getItemId()){
					case 0:
					new AlertDialog.Builder(ServerListActivityImpl.this)
						.setMessage(R.string.auSure)
						.setNegativeButton(android.R.string.ok,new QuickDialogClickListener(){
							public void onClick(int which){
								for(Server s:selected){
									if(pinging.get(s))continue;
									dryUpdate(s,true);
									if(list.indexOf(s)!=-1){
										statLayout.setStatusAt(list.indexOf(s),1);
									}
								}
								p1.finish();
							}
						})
						.setPositiveButton(android.R.string.cancel,null);
						break;
				}
                return true;
            }

            public void onDestroyActionMode(ActionMode p1) {
                editMode = EDIT_MODE_NULL;
                srl.setEnabled(true);
                dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                saveServers();
				sl.unselectAll();
				isInSelectMode=false;
            }
        };
		multipleDeleteAm = new ActionMode.Callback(){
            public boolean onCreateActionMode(ActionMode p1, Menu p2) {
                srl.setEnabled(false);
                dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
				selected.clear();
				isInSelectMode=true;
                return true;
            }

            public boolean onPrepareActionMode(ActionMode p1, Menu p2) {
                editMode = EDIT_MODE_MULTIPLE_DELETE;
				MenuItem mi=p2.add(Menu.NONE,0,0,R.string.delete).setIcon(TheApplication.instance.getTintedDrawable(R.drawable.ic_delete_forever_black_48dp,Utils.getMenuTintColor(ServerListActivityImpl.this)));
				MenuItemCompat.setShowAsAction(mi,MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
                return true;
            }

            public boolean onActionItemClicked(ActionMode p1, MenuItem p2) {
				switch(p2.getItemId()){
					case 0:
						for(Server s:selected){
							sl.remove(s);
						}
						p1.finish();
						break;
				}
                return true;
            }

            public void onDestroyActionMode(ActionMode p1) {
                editMode = EDIT_MODE_NULL;
                srl.setEnabled(true);
                dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                saveServers();
				sl.unselectAll();
				isInSelectMode=false;
            }
        };
        switch (pref.getInt("serverListStyle2", 0)) {
            case 0:
                ddManager.setDefaultDragDirs(ItemTouchHelper.UP | ItemTouchHelper.DOWN);
                break;
            case 1:case 2:default:
                ddManager.setDefaultDragDirs(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT);
                break;
        }
        itemDecor = new ItemTouchHelper(ddManager);
		switch (editMode) {
			case EDIT_MODE_EDIT:startEditMode();break;
			case EDIT_MODE_SELECT_UPDATE:startSelectUpdateMode();break;
			case EDIT_MODE_MULTIPLE_DELETE:startMultipleDeleteMode();break;
		}

		addActivityResultReceiver(new DispatchActivityResult(){
				@Override
				public boolean dispatchActivityResult(int requestCode, int resultCode, Intent data, boolean consumed) {
					if (consumed)return true;
					switch (requestCode) {
						case 0:
							switch (resultCode) {
								case Constant.ACTIVITY_RESULT_UPDATE:
									Bundle obj=data.getBundleExtra("object");
									Server serv=list.get(clicked);
									updater.putInQueue(serv, new PingHandlerImpl(true, data, true));
									pinging.put(serv, true);
									statLayout.setStatusAt(clicked, 1);
									sl.notifyItemChanged(clicked);
									wd.showWorkingDialog(serv);
									break;
							}
							return true;
					}
					return false;
				}
			});
		if(savedInstanceState!=null)
			if(savedInstanceState.containsKey("selected"))
				selected=gson.fromJson(savedInstanceState.getString("selected"),new TypeToken<HashSet<Server>>(){}.getType());
	
		if(Build.VERSION.SDK_INT>=22){
			setTaskDescription(
				new CompatTaskDescription(
					getResources().getString(R.string.app_name),
					BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher),
					ThemePatcher.getMainColor(this)
				)
			);
		}
	}

	private void loadMenu() {
		appMenu.add(new Sextet<Integer,Integer,Treatment<ServerListActivity>,Treatment<ServerListActivity>,IDrawerItem,UUID>(R.string.add, R.drawable.ic_add_black_48dp, new Treatment<ServerListActivity>(){
							public void process(ServerListActivity a) {
								View dialog=getLayoutInflater().inflate(R.layout.server_add_dialog_new, null);
								final LinearLayout peFrame=(LinearLayout)dialog.findViewById(R.id.pe);
								final LinearLayout pcFrame=(LinearLayout)dialog.findViewById(R.id.pc);
								final EditText pe_ip=(EditText)dialog.findViewById(R.id.pe).findViewById(R.id.serverIp);
								final EditText pe_port=(EditText)dialog.findViewById(R.id.pe).findViewById(R.id.serverPort);
								final EditText pc_ip=(EditText)dialog.findViewById(R.id.pc).findViewById(R.id.serverIp);
								final CheckBox split=(CheckBox)dialog.findViewById(R.id.switchFirm);
								final EditText serverName=(EditText)dialog.findViewById(R.id.serverName);

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
												Server s=Utils.convertServerObject(Arrays.asList(MslServer.makeServerFromString(pc_ip.getText().toString(), false))).get(0);
												pe_ip.setText(s.ip);
												pe_port.setText(s.port + "");
											}
										}
									});

								new AlertDialog.Builder(a,ThemePatcher.getDefaultDialogStyle(a)).
									setView(dialog).
									setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){
										public void onClick(DialogInterface d, int sel) {
											Server s;
											if (split.isChecked()) {
												s = Utils.convertServerObject(Arrays.asList(MslServer.makeServerFromString(pc_ip.getText().toString(), false))).get(0);
											} else {
												s = new Server();
												s.ip = pe_ip.getText().toString();
												s.port = Integer.valueOf(pe_port.getText().toString());
												s.mode = split.isChecked() ?1: 0;
											}
											if(!TextUtils.isEmpty(serverName.getText()))
												s.name=serverName.getText().toString();

											if (list.contains(s)) {
												Utils.makeNonClickableSB(ServerListActivityImpl.this, R.string.alreadyExists, Snackbar.LENGTH_LONG).show();
											} else {
												sl.add(s);
												spp.putInQueue(s, new PingHandlerImpl(true, new Intent().putExtra("offset",-1),false));
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
							}
						},new DialogLauncherListener<ServerListActivity>(this)
							.setItems(R.array.serverAddSubMenu,new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface di,int w){
									switch(w){
										case 0:
											startMultipleDeleteMode();
											break;
									}
								}
							})
						,null,UUID.fromString("a077e0fe-1ae0-3e05-8ca1-08b587fb787d")));//0
		appMenu.add(new Sextet<Integer,Integer,Treatment<ServerListActivity>,Treatment<ServerListActivity>,IDrawerItem,UUID>(R.string.addFromMCPE, R.drawable.ic_add_black_48dp, new Treatment<ServerListActivity>(){
							public void process(final ServerListActivity a) {
								new AlertDialog.Builder(a,ThemePatcher.getDefaultDialogStyle(a))
									.setTitle(R.string.addFromMCPE)
									.setMessage(R.string.auSure)
									.setPositiveButton(android.R.string.yes,new DialogInterface.OnClickListener(){
										public void onClick(DialogInterface di,int w){
											ServerListActivityImplPermissionsDispatcher.addFromMCPEWithCheck(a);
										}
									})
									.setNegativeButton(android.R.string.no,null)
									.show();
							}
						},null,null,UUID.fromString("1041efc9-fe9f-32db-9729-9b9897f5eebd")));//1
		appMenu.add(new Sextet<Integer,Integer,Treatment<ServerListActivity>,Treatment<ServerListActivity>,IDrawerItem,UUID>(R.string.update_all, R.drawable.ic_refresh_black_48dp, new Treatment<ServerListActivity>(){
							public void process(ServerListActivity a) {
								updateAllWithConditions(new Predicate<Server>(){public boolean process(Server a){return true;}});
							}
						},
						new DialogLauncherListener<ServerListActivity>(this)
						.setTitle(R.string.update_all)
						.setItems(R.array.serverUpdateAllSubMenu,new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface di, int w) {
									switch(w){
										case 0://update all
											appMenu.findByA(R.string.update_all).getC().process(ServerListActivity.instance.get());
											break;
										case 1://update onlines
											updateAllWithConditions(new Predicate<Server>(){public boolean process(Server a){return a instanceof ServerStatus;}});
											break;
										case 2://update offlines
											updateAllWithConditions(new Predicate<Server>(){public boolean process(Server a){return !(a instanceof ServerStatus);}});
											break;
										case 3://select
											startSelectUpdateMode();
											break;
									}
								}
							})
						,null,UUID.fromString("7880ff52-b8c5-3c29-8b65-74fc30a57316")));//2
		appMenu.add(new Sextet<Integer,Integer,Treatment<ServerListActivity>,Treatment<ServerListActivity>,IDrawerItem,UUID>(R.string.export, R.drawable.ic_file_upload_black_48dp, new Treatment<ServerListActivity>(){
							public void process(final ServerListActivity a) {
								View dialogView_=getLayoutInflater().inflate(R.layout.server_list_imp_exp, null);
								final EditText et_=(EditText)dialogView_.findViewById(R.id.filePath);
								et_.setText(new File(Environment.getExternalStorageDirectory(), "/Wisecraft/servers.json").toString());
								dialogView_.findViewById(R.id.selectFile).setOnClickListener(new View.OnClickListener(){
										public void onClick(View v) {
											File f=new File(et_.getText().toString());
											if ((!f.exists())|f.isFile())f = f.getParentFile();
											ServerListActivityBase3PermissionsDispatcher.startChooseFileForOpenWithCheck(a,f, new FileChooserResult(){
													public void onSelected(File f) {
														et_.setText(f.toString());
													}
													public void onSelectCancelled() {/*No-op*/}
												});
										}
									});
								new AlertDialog.Builder(a,ThemePatcher.getDefaultDialogStyle(a))
									.setTitle(R.string.export_typepath)
									.setView(dialogView_)
									.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
										public void onClick(DialogInterface di, int w) {
											ServerListActivityImplPermissionsDispatcher.exportWisecraftListWithCheck(a,et_.getText().toString());
										}
									})
									.show();
							}
						},null,null,UUID.fromString("614d2246-80d1-37de-9f2e-fed1fa15c83d")));//3
		appMenu.add(new Sextet<Integer,Integer,Treatment<ServerListActivity>,Treatment<ServerListActivity>,IDrawerItem,UUID>(R.string.imporT, R.drawable.ic_file_download_black_48dp, new Treatment<ServerListActivity>(){
							public void process(final ServerListActivity a) {
								View dialogView=getLayoutInflater().inflate(R.layout.server_list_imp_exp, null);
								final EditText et=(EditText)dialogView.findViewById(R.id.filePath);
								et.setText(new File(Environment.getExternalStorageDirectory(), "/Wisecraft/servers.json").toString());
								dialogView.findViewById(R.id.selectFile).setOnClickListener(new View.OnClickListener(){
										public void onClick(View v) {
											File f=new File(et.getText().toString());
											if ((!f.exists())|f.isFile())f = f.getParentFile();
											ServerListActivityBase3PermissionsDispatcher.startChooseFileForOpenWithCheck(a,f, new FileChooserResult(){
													public void onSelected(File f) {
														et.setText(f.toString());
													}
													public void onSelectCancelled() {/*No-op*/}
												});
										}
									});
								new AlertDialog.Builder(a,ThemePatcher.getDefaultDialogStyle(a))
									.setTitle(R.string.import_typepath)
									.setView(dialogView)
									.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
										public void onClick(DialogInterface di, int w) {
											ServerListActivityImplPermissionsDispatcher.importWisecraftListWithCheck(a,et.getText().toString());
										}
									})
									.show();
							}
						},null,null,UUID.fromString("1b4cd9b1-662a-3e72-9ae4-eecd46858077")));//4
		if(pref.getBoolean("feature_bott", true)){
			appMenu.add(new Sextet<Integer,Integer,Treatment<ServerListActivity>,Treatment<ServerListActivity>,IDrawerItem,UUID>(R.string.sort, R.drawable.ic_compare_arrows_black_48dp, 
					new DialogLauncherListener<ServerListActivity>(this,false)
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
						,null,null,UUID.fromString("59084d0f-904a-3379-a0f2-285d6763016c")));//5
		}
		if(pref.getBoolean("feature_serverFinder", false)){
			appMenu.add(new Sextet<Integer,Integer,Treatment<ServerListActivity>,Treatment<ServerListActivity>,IDrawerItem,UUID>(R.string.serverFinder, R.drawable.ic_search_black_48dp, new Treatment<ServerListActivity>(){
								public void process(ServerListActivity a) {
									startActivity(new Intent(a, ServerFinderActivity.class));
								}
							},null,null,UUID.fromString("fce961a9-a6be-394d-8a16-96e598e7886d")));//6
		}
		if(pref.getBoolean("feature_asfsls", false)){
			appMenu.add(new Sextet<Integer,Integer,Treatment<ServerListActivity>,Treatment<ServerListActivity>,IDrawerItem,UUID>(R.string.addServerFromServerListSite, R.drawable.ic_language_black_48dp, new Treatment<ServerListActivity>(){
								public void process(ServerListActivity a) {
									startActivity(new Intent(a, ServerGetActivity.class));
								}
							},null,null,UUID.fromString("9e63592a-3b0b-33f6-a8e2-937f2aa85ce2")));//7
		}
		appMenu.add(new Sextet<Integer,Integer,Treatment<ServerListActivity>,Treatment<ServerListActivity>,IDrawerItem,UUID>(R.string.loadPing, R.drawable.ic_open_in_new_black_48dp, new Treatment<ServerListActivity>(){
							public void process(final ServerListActivity a) {
								View dialogView=getLayoutInflater().inflate(R.layout.server_list_imp_exp, null);
								final EditText et=(EditText)dialogView.findViewById(R.id.filePath);
								et.setText(new File(Environment.getExternalStorageDirectory(), "/Wisecraft/pingresult.wisecraft-ping").toString());
								dialogView.findViewById(R.id.selectFile).setOnClickListener(new View.OnClickListener(){
										public void onClick(View v) {
											File f=new File(et.getText().toString());
											if ((!f.exists())|f.isFile())f = f.getParentFile();
											ServerListActivityBase3PermissionsDispatcher.startChooseFileForOpenWithCheck(a,f, new FileChooserResult(){
													public void onSelected(File f) {
														et.setText(f.toString());
													}
													public void onSelectCancelled() {/*No-op*/}
												});
										}
									});
								new AlertDialog.Builder(a,ThemePatcher.getDefaultDialogStyle(a))
									.setTitle(R.string.load_typepath_simple)
									.setView(dialogView)
									.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
										public void onClick(DialogInterface di, int w) {
											ServerListActivityImplPermissionsDispatcher.loadWisecraftPingWithCheck(a,et.getText().toString());
										}
									})
									.show();
							}
						},null,null,UUID.fromString("162c19a0-271d-3671-80cd-cb95826e19d0")));//8
		appMenu.add(new Sextet<Integer,Integer,Treatment<ServerListActivity>,Treatment<ServerListActivity>,IDrawerItem,UUID>(R.string.settings, R.drawable.ic_settings_black_48dp, new Treatment<ServerListActivity>(){
							public void process(ServerListActivity a) {
								SettingsDelegate.openAppSettings(a);
							}
						},null,null,UUID.fromString("85df5fde-76f9-3d50-aeb3-cc5edb77ecfa")));//9
		appMenu.add(new Sextet<Integer,Integer,Treatment<ServerListActivity>,Treatment<ServerListActivity>,IDrawerItem,UUID>(R.string.exit, R.drawable.ic_close_black_48dp, new Treatment<ServerListActivity>(){
							public void process(ServerListActivity a) {
								finish();
								saveServers();
								instance = new WeakReference<>(null);
								if (pref.getBoolean("exitCompletely", false))
									if (ProxyActivity.cont != null)
										ProxyActivity.cont.stopService();
							}
						},null,null,UUID.fromString("5c0baf72-9a92-312d-ab33-062bdc3aa445")));//10
	}

	private void setupDrawer() {
		dl = drawer.getDrawerLayout();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (!skipSave)saveServers();
		unregisterReceiver(nsbr);
	}

	@Override
	public void onBackPressed() {
		if (drawer.isDrawerOpen())
			drawer.closeDrawer();
		else
			super.onBackPressed();
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_MENU){
			if(drawer.isDrawerOpen())
				drawer.closeDrawer();
			else
				drawer.openDrawer();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState=drawer.saveInstanceState(outState);
		super.onSaveInstanceState(outState);
		outState.putString("selected",gson.toJson(selected));
	}

	public void loadServers() {
		List<Server> sa=Utils.jsonToServers(pref.getString("servers", "[]"));
		int prevLen=list.size();
		list.clear();
		sl.notifyItemRangeRemoved(0, prevLen);
		int curLen=sa.size();
		list.addAll(sa);
		sl.notifyItemRangeInserted(0, curLen);
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
		if (isUpdate)updater.putInQueue(s, new PingHandlerImpl(true, new Intent().putExtra("offset",-1),false));
		else spp.putInQueue(s, new PingHandlerImpl(true, new Intent().putExtra("offset",-1),false));
		pinging.put(s, true);
		sl.notifyItemChanged(list.indexOf(s));
	}
	
	private void updateAllWithConditions(final Predicate<Server> pred) {
		for (int i=0;i < list.size();i++) {
			if (pinging.get(list.get(i)) || !pred.process(list.get(i)))
				continue;
			statLayout.setStatusAt(i, 1);
			sl.notifyItemChanged(i);
			if (!srl.isRefreshing())
				srl.setRefreshing(true);
		}
		new Thread(){
			public void run() {
				for (int i=0;i < list.size();i++) {
					if (pinging.get(list.get(i)) || !pred.process(list.get(i)))
						continue;
					spp.putInQueue(list.get(i), new PingHandlerImpl(false, new Intent().putExtra("offset",-1), false){
							public void onPingFailed(final Server s) {
								super.onPingFailed(s);
								runOnUiThread(new Runnable(){
										public void run() {			
											wd.hideWorkingDialog(s);
										}
									});
							}
							public void onPingArrives(final ServerStatus s) {
								super.onPingArrives(s);
								runOnUiThread(new Runnable(){
										public void run() {
											wd.hideWorkingDialog(s);
										}
									});
							}
						});
					pinging.put(list.get(i), true);
				}
			}
		}.start();
	}
	
	@NeedsPermission("android.permission.WRITE_EXTERNAL_STORAGE")
	public void addFromMCPE(){
		Utils.makeNonClickableSB(ServerListActivityImpl.this, R.string.importing, Snackbar.LENGTH_LONG).show();
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
						svr.name = s[1];
						sv.add(svr);
					} catch (NumberFormatException e) {}
				}
				sv.removeAll(list);
				runOnUiThread(new Runnable(){
						public void run() {
							if (sv.size() != 0) {
								for (Server s:sv) {
									if (!list.contains(s)) {
										spp.putInQueue(s, new PingHandlerImpl(true, new Intent().putExtra("offset",-1),false));
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
	
	@NeedsPermission("android.permission.WRITE_EXTERNAL_STORAGE")
	public void exportWisecraftList(String fn){
		Utils.makeNonClickableSB(ServerListActivityImpl.this, R.string.exporting, Snackbar.LENGTH_LONG).show();
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
					Utils.makeNonClickableSB(ServerListActivityImpl.this, getResources().getString(R.string.export_complete).replace("[PATH]", f + ""), Snackbar.LENGTH_LONG).show();
				} else {
					Utils.makeNonClickableSB(ServerListActivityImpl.this, getResources().getString(R.string.export_failed), Snackbar.LENGTH_LONG).show();
				}
			}
		}.execute(fn);
	}
	
	@NeedsPermission("android.permission.WRITE_EXTERNAL_STORAGE")
	public void importWisecraftList(final String fn){
		Utils.makeNonClickableSB(ServerListActivityImpl.this, R.string.importing, Snackbar.LENGTH_LONG).show();
		new Thread(){
			public void run() {
				File f=new File(fn);
				if(f.exists()){
					final List<Server> sv;
					String json=readWholeFile(f);
					sv=Utils.jsonToServers(json);
					runOnUiThread(new Runnable(){
							public void run() {
								sl.addAll(sv);
								saveServers();
								Utils.makeNonClickableSB(ServerListActivityImpl.this, getResources().getString(R.string.imported).replace("[PATH]", fn), Snackbar.LENGTH_LONG).show();
							}
						});
				}else{
					runOnUiThread(new Runnable(){
							public void run() {
								Utils.makeNonClickableSB(ServerListActivityImpl.this, R.string.fileNotExist, Snackbar.LENGTH_LONG).show();
							}
						});
				}
			}
		}.start();
	}
	
	@NeedsPermission("android.permission.WRITE_EXTERNAL_STORAGE")
	public void loadWisecraftPing(final String fn){
		wd.showWorkingDialog(fn);
		new Thread(){
			public void run() {
				ServerPingResult spr=null;
				try {
					try(InputStream rdr=new BufferedInputStream(new FileInputStream(new File(fn)))){
						spr = PingSerializeProvider.loadFromRawDumpFile(rdr);
					}
				} catch (Throwable e) {
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
				String _stat=null;
				try{
					_stat=Utils.encodeForServerInfo(sv);
				}catch(Throwable e){
					WisecraftError.report("ServerListActivity#execOption#8",e);
				}
				final String stat=_stat;
				runOnUiThread(new Runnable(){
						public void run() {
							wd.hideWorkingDialog(fn);
							if(sv.response==null|stat==null){
								Utils.makeNonClickableSB(ServerListActivityImpl.this,R.string.loadPing_loadError,Snackbar.LENGTH_SHORT).show();
							}else{
								startActivity(new Intent(ServerListActivityImpl.this, ServerInfoActivity.class).putExtra("stat", stat).putExtra("noExport",true).putExtra("nonUpd",true));
							}
						}
					});
			}
		}.start();
	}
	
	@NeedsPermission("android.permission.WRITE_EXTERNAL_STORAGE")
	public void exportSingleServer(String dest,final ServerStatus stat){
		Utils.makeSB(coordinator, R.string.exporting, Snackbar.LENGTH_LONG).show();
		new AsyncTask<String,Void,File>(){
			public File doInBackground(String... texts) {
				File f;
				byte[] data=PingSerializeProvider.doRawDumpForFile(stat.response);
				if (writeToFileByBytes(f = new File(texts[0]), data))
					return f;
				else
					return null;
			}
			public void onPostExecute(File f) {
				if (f != null) {
					Utils.makeSB(coordinator, getResources().getString(R.string.export_complete).replace("[PATH]", f + ""), Snackbar.LENGTH_LONG).show();
				} else {
					Utils.makeSB(coordinator, getResources().getString(R.string.export_failed), Snackbar.LENGTH_LONG).show();
				}
			}
		}.execute(dest);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		ServerListActivityImplPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
	}
	
	public List<Server> getServers() {
		return new ArrayList<Server>(list);
	}

	@Override
	public void addIntoList(Server s) {
		if (list.contains(s))return;
		sl.add(s);
		spp.putInQueue(s, new PingHandlerImpl(true, new Intent().putExtra("offset",-1),false));
		pinging.put(s, true);
	}

	@Override
	public boolean contains(Object s) {
		return list.contains(s);
	}
    
    private void startEditMode() {
        //start action mode here
        startSupportActionMode(handMoveAm);
    }
	
	private void startSelectUpdateMode() {
        //start action mode here
        startSupportActionMode(selectUpdateAm);
    }
	
	private void startMultipleDeleteMode() {
        //start action mode here
        startSupportActionMode(multipleDeleteAm);
    }

	public void startServerInfoActivity(Intent in,int objPos){
		in.setClass(this, ServerInfoActivity.class);
		boolean useBottomSheet=in.getBooleanExtra("bottomSheet",true)&!pref.getBoolean("noScrollServerInfo",false);
		if(!useBottomSheet&(objPos>=0|objPos<list.size())&rv.findViewHolderForAdapterPosition(objPos)!=null){
			ActivityOptionsCompat opt=ActivityOptionsCompat.makeSceneTransitionAnimation(this,Pair.<View,String>create(rv.findViewHolderForAdapterPosition(objPos).itemView,getResources().getString(R.string.serverInfoTrans1)));
			startActivityForResult(in, 0,opt.toBundle());
		}else{
			startActivityForResult(in, 0);
		}
	}
	
	
	static class ServerList extends RecyclerView.Adapter<ServerStatusWrapperViewHolder> implements AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener {
		ServerListActivityImpl sla;
		public ServerList(ServerListActivityImpl sla) {
			sla.list = new ServerListArrayList();
			this.sla = sla;
		}

		@Override
		public ServerStatusWrapperViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
			// 表示するレイアウトを設?��?
			switch(sla.pref.getInt("serverListStyle2",0)){
				case 0:default:
					return new ServerStatusWrapperViewHolder(sla,false,viewGroup);
				case 1:case 2:
					return new ServerStatusWrapperViewHolder(sla,true,viewGroup);
			}
		}

		@Override
		@ServerInfoParser
		public void onBindViewHolder(final ServerStatusWrapperViewHolder viewHolder, final int position) {
			if (sla.list != null && sla.list.size() > position && sla.list.get(position) != null) {
				Server sv=getItem(position);
				viewHolder.itemView.setTag(sv);
				try{
					if(TextUtils.isEmpty(sv.name)||sv.toString().equals(sv.name)){
						viewHolder.hideServerTitle();
						sv.name=null;
					}else{
						if (sla.pref.getBoolean("serverListColorFormattedText", false)) {
							viewHolder.setServerTitle(parseMinecraftFormattingCode(sv.name));
						} else {
							viewHolder.setServerTitle(deleteDecorations(sv.name));
						}
						viewHolder.showServerTitle();
					}
					sla.slsl.applyTextColorTo(viewHolder);
					if (sla.pinging.get(sv)) {
						viewHolder.pending(sv,sla);
					} else {
						if (sv instanceof ServerStatus) {
							ServerStatus s=(ServerStatus)sv;
							viewHolder.setStatColor(ContextCompat.getColor(sla, R.color.stat_ok));
							final CharSequence title;
							if (s.response instanceof FullStat) {//PE
								FullStat fs = (FullStat) s.response;
								Map<String, String> m = fs.getDataAsMap();
								if (m.containsKey("hostname")) {
									title = m.get("hostname");
								} else if (m.containsKey("motd")) {
									title = m.get("motd");
								} else {
									title = s.toString();
								}
								viewHolder.setServerPlayers(m.get("numplayers"), m.get("maxplayers"));
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
							} else if (s.response instanceof RawJsonReply) {//PC (Obfuscated)
								RawJsonReply rep = (RawJsonReply) s.response;
								if (!rep.json.has("description")) {
									title = s.toString();
								} else {
									title=Utils.parseMinecraftDescriptionJson(rep.json.get("description"));
								}
								viewHolder.setServerPlayers(rep.json.get("players").get("online").getAsInt(), rep.json.get("players").get("max").getAsInt());
							} else if (s.response instanceof SprPair) {//PE?
								SprPair sp = ((SprPair) s.response);
								if (sp.getB() instanceof UnconnectedPing.UnconnectedPingResult) {
									UnconnectedPing.UnconnectedPingResult res = (UnconnectedPing.UnconnectedPingResult) sp.getB();
									title = res.getServerName();
									viewHolder.setServerPlayers(res.getPlayersCount(), res.getMaxPlayers());
								} else if (sp.getA() instanceof FullStat) {
									FullStat fs = (FullStat) sp.getA();
									Map<String, String> m = fs.getDataAsMap();
									if (m.containsKey("hostname")) {
										title = m.get("hostname");
									} else if (m.containsKey("motd")) {
										title = m.get("motd");
									} else {
										title = s.toString();
									}
									viewHolder.setServerPlayers(m.get("numplayers"), m.get("maxplayers"));
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
							if (sla.pref.getBoolean("serverListColorFormattedText", false)) {
								if(title instanceof String){
									viewHolder.setServerName(parseMinecraftFormattingCode(title.toString()));
								}else{
									viewHolder.setServerName(title);
								}
							} else {
								if(title instanceof String){
									viewHolder.setServerName(deleteDecorations(title.toString()));
								}else{
									viewHolder.setServerName(title.toString());
								}
							}
							viewHolder
								.setPingMillis(s.ping)
								.setServer(s);
						} else {
							viewHolder.offline(sv,sla);
						}
					}
				}catch(Throwable e){
					RuntimeException rex=new RuntimeException("error: sv: "+sv+" Maybe the server sent incorrect data?",e);
					WisecraftError.report(sv+"",rex);
					viewHolder.unknown(sla,sv);
				}
				
				if(sla.isInSelectMode){
					viewHolder.setSelected(sla.selected.contains(sv));
				}else{
					viewHolder.setSelected(false);
				}

				applyHandlersForViewTree(viewHolder.itemView,
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							onItemClick(null, viewHolder.itemView, sla.list.indexOf(viewHolder.itemView.getTag()), Long.MIN_VALUE);
						}
					}
					,
					new View.OnLongClickListener() {
						@Override
						public boolean onLongClick(View v) {
							return onItemLongClick(null, viewHolder.itemView, sla.list.indexOf(viewHolder.itemView.getTag()), Long.MIN_VALUE);
						}
					}
				);
				travelViewTree(viewHolder.itemView,new Treatment<View>(){public void process(View v){sla.registerForContextMenu(v);}});
			}
		}

		@Override
		public int getItemCount() {
			return sla.list.size();
		}

		@Override
		public int getItemViewType(int position) {
			return sla.statLayout.getStatusAt(position);
		}

		@Override
		public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
			if(sla.isInSelectMode){
				if(sla.selected.contains(getItem(p3))){
					sla.selected.remove(getItem(p3));
				}else{
					sla.selected.add(getItem(p3));
				}
				notifyItemChanged(p3);
			}
            if(sla.editMode!=EDIT_MODE_NULL)return;
			Server s=getItem(p3);
			sla.clicked = p3;
			if (sla.pinging.get(s))return;
			if (s instanceof ServerStatus) {
				Bundle bnd=new Bundle();
				sla.startServerInfoActivity(new Intent().putExtra("stat", Utils.encodeForServerInfo((ServerStatus)s)).putExtra("object", bnd),p3);
			} else {
				sla.updater.putInQueue(s, new PingHandlerImpl(true, new Intent().putExtra("offset",0), true));
				sla.pinging.put(s, true);
				sla.statLayout.setStatusAt(p3, 1);
				sla.sl.notifyItemChanged(p3);
				sla.wd.showWorkingDialog(s);
			}
		}

		@Override
		public boolean onItemLongClick(AdapterView<?> p1, View p2, final int p3, long p4) {
            if(sla.editMode!=EDIT_MODE_NULL)return true;
			sla.clicked = p3;
			
			final List<Duo<Runnable,Integer>> executes,all;
			{
				executes=new ArrayList<>();
				executes.add(0, new Duo<Runnable,Integer>(new Runnable(){
									 public void run() {
										 new AlertDialog.Builder(sla,ThemePatcher.getDefaultDialogStyle(sla))
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
								 },R.string.remove));
				executes.add(1, new Duo<Runnable,Integer>(new Runnable(){
									 public void run() {
										 Server svr=getItem(p3);
										 if (sla.pinging.get(svr))return;
										 sla.updater.putInQueue(svr, new PingHandlerImpl(true, new Intent().putExtra("offset",-1),false));
										 sla.pinging.put(svr, true);
										 sla.statLayout.setStatusAt(p3, 1);
										 sla.sl.notifyItemChanged(p3);
										 sla.wd.showWorkingDialog(svr);
									 }
								 },R.string.update));
				executes.add(2, new Duo<Runnable,Integer>(new Runnable(){
									 public void run() {
										 final Server data=getItem(p3);
										 View dialog=sla.getLayoutInflater().inflate(R.layout.server_add_dialog_new, null);
										 final LinearLayout peFrame=(LinearLayout)dialog.findViewById(R.id.pe);
										 final LinearLayout pcFrame=(LinearLayout)dialog.findViewById(R.id.pc);
										 final EditText pe_ip=(EditText)dialog.findViewById(R.id.pe).findViewById(R.id.serverIp);
										 final EditText pe_port=(EditText)dialog.findViewById(R.id.pe).findViewById(R.id.serverPort);
										 final EditText pc_ip=(EditText)dialog.findViewById(R.id.pc).findViewById(R.id.serverIp);
										 final CheckBox split=(CheckBox)dialog.findViewById(R.id.switchFirm);
										 final EditText serverName=(EditText)dialog.findViewById(R.id.serverName);

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
										 if(!TextUtils.isEmpty(data.name))
											 serverName.setText(data.name);

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
														 Server s=Utils.convertServerObject(Arrays.<MslServer>asList(MslServer.makeServerFromString(pc_ip.getText().toString(), false))).get(0);
														 pe_ip.setText(s.ip);
														 pe_port.setText(s.port + "");
													 }
												 }
											 });

										 new AlertDialog.Builder(sla,ThemePatcher.getDefaultDialogStyle(sla)).
											 setView(dialog).
											 setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){
												 public void onClick(DialogInterface d, int sel) {
													 Server s;
													 if (split.isChecked()) {
														 s = Utils.convertServerObject(Arrays.<MslServer>asList(MslServer.makeServerFromString(pc_ip.getText().toString(), false))).get(0);
													 } else {
														 s = new Server();
														 s.ip = pe_ip.getText().toString();
														 s.port = Integer.valueOf(pe_port.getText().toString());
														 s.mode = 0;
													 }
													 if(!TextUtils.isEmpty(serverName.getText()))
														 s.name=serverName.getText().toString();

													 sla.list.set(p3, s);
													 sla.sl.notifyItemChanged(p3);
													 
													 //Never update when the server is only edited
													 /*sla.dryUpdate(s, true);
													 sla.statLayout.setStatusAt(p3, 1);*/
													 
													 sla.saveServers();
												 }
											 }).
											 setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener(){
												 public void onClick(DialogInterface d, int sel) {

												 }
											 }).
											 show();
									 }
								 },R.string.edit));
				executes.add(3, new Duo<Runnable,Integer>(new Runnable(){
									 public void run() {
										 sla.startActivity(new Intent(sla, ServerTestActivity.class).putExtra("ip", getItem(p3).ip).putExtra("port", getItem(p3).port).putExtra("ispc", getItem(p3).mode));
									 }
								 },R.string.testServer));
				executes.add(4, new Duo<Runnable,Integer>(new Runnable(){
									 public void run() {
										 sla.startActivity(new Intent(sla, RCONActivity.class).putExtra("ip", getItem(p3).ip).putExtra("port", getItem(p3).port));
									 }
								 },R.string.rcon));
				executes.add(5, new Duo<Runnable,Integer>(new Runnable(){
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
								 },R.string.addToMcpe));
				executes.add(6, new Duo<Runnable,Integer>(new Runnable(){
									 public void run() {
										 sla.startActivity(new Intent(sla, ProxyActivity.class).putExtra("ip", getItem(p3).ip).putExtra("port", getItem(p3).port).setAction("start"));
									 }
								 },R.string.launchMtl));
				executes.add(7, new Duo<Runnable,Integer>(new Runnable(){
									 public void run() {
										 sla.startActivity(new Intent(sla, ServerFinderActivity.class).putExtra("ip", getItem(p3).ip).putExtra("port", getItem(p3).port).putExtra("mode", getItem(p3).mode));
									 }
								 },R.string.serverFinder));
				executes.add(8, new Duo<Runnable,Integer>(new Runnable(){
									 public void run() {
										 sla.startActivity(new Intent(sla, GenerateWisecraftOpenLinkActivity.class).putExtra("ip", getItem(p3).ip).putExtra("port", getItem(p3).port).putExtra("mode", getItem(p3).mode));
									 }
								 },R.string.genLink));
				executes.add(9, new Duo<Runnable,Integer>(new Runnable(){
									 public void run() {
										 if(!(getItem(p3) instanceof ServerStatus))return;
										 View dialogView_=sla.getLayoutInflater().inflate(R.layout.server_list_imp_exp, null);
										 final EditText et_=(EditText)dialogView_.findViewById(R.id.filePath);
										 et_.setText(new File(Environment.getExternalStorageDirectory(), "/Wisecraft/pingresult.wisecraft-ping").toString());
										 dialogView_.findViewById(R.id.selectFile).setOnClickListener(new View.OnClickListener(){
												public void onClick(View v) {
													File f=new File(et_.getText().toString());
													if ((!f.exists())|f.isFile())f = f.getParentFile();
													ServerListActivityBase3PermissionsDispatcher.startChooseFileForOpenWithCheck(sla,f,new FileChooserResult(){
															public void onSelected(File f) {
																et_.setText(f.toString());
															}
															public void onSelectCancelled() {/*No-op*/}
														});
												}
											});
										new AlertDialog.Builder(sla,ThemePatcher.getDefaultDialogStyle(sla))
											.setTitle(R.string.export_typepath_simple)
											.setView(dialogView_)
											.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
												public void onClick(DialogInterface di, int w) {
													ServerListActivityImplPermissionsDispatcher.exportSingleServerWithCheck(sla,et_.getText().toString(),(ServerStatus)getItem(p3));
												}
											})
											.show();
										}
								 },R.string.exportPing));

				all=new ArrayList<>(executes);

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
				if(!(getItem(p3) instanceof ServerStatus)){
					executes.remove(all.get(9));
				}
			}
			
			if(false){
				new AlertDialog.Builder(sla,ThemePatcher.getDefaultDialogStyle(sla))
					.setTitle(getItem(p3).resolveVisibleTitle())
					.setItems(generateSubMenu(executes), new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface di, int which) {
							executes.get(which).getA().run();
						}
					})
					.setCancelable(true)
					.show();
			}else if(true){
				final BottomSheetListDialog bsld=new BottomSheetListDialog(sla);
				bsld.setTitle(getItem(p3).resolveVisibleTitle());
				bsld.setLayoutManager(new LinearLayoutManager(sla));
				
				class ServerExtSelect extends RecyclerView.Adapter<FindableViewHolder> {
					String[] strings=generateSubMenu(executes);
					
					@Override
					public void onBindViewHolder(FindableViewHolder holder, final int position) {
						((TextView)holder.findViewById(android.R.id.text1)).setText(strings[position]);
						TypedArray ta=sla.obtainStyledAttributes(new int[]{R.attr.selectableItemBackground});
						holder.itemView.setBackground(ta.getDrawable(0));
						ta.recycle();
						Utils.applyHandlersForViewTree(holder.itemView,new View.OnClickListener(){
							public void onClick(View v){
								executes.get(position).getA().run();
								bsld.cancel();
							}
						});
					}

					@Override
					public FindableViewHolder onCreateViewHolder(ViewGroup p1, int p2){
						return new FindableViewHolder(LayoutInflater.from(bsld.getContext()).inflate(R.layout.simple_list_item_1,p1,false));
					}

					@Override
					public int getItemCount() {
						return strings.length;
					}
				}
				bsld.setAdapter(new ServerExtSelect());
				bsld.show();
			}else{
				sla.openContextMenu(p2,sla.rv,new Treatment<Duo<View,ContextMenu>>(){
						public void process(Duo<View,ContextMenu> a){
							ContextMenu menu=a.getB();
							menu.setHeaderTitle(getItem(p3).toString());
							for(String s:generateSubMenu(executes)){
								menu.add(Menu.NONE,menu.size(),menu.size(),s);
							}
						}
					},new Predicate<Trio<View,ContextMenu,MenuItem>>(){
						public boolean process(Trio<View,ContextMenu,MenuItem> a){
							executes.get(a.getC().getItemId()).getA().run();
							return true;
						}
					});
			}
			
			
			return true;
		}
		
		private String[] generateSubMenu(List<Duo<Runnable,Integer>> executes) {
			List<String> result=new ArrayList<String>();
			for(Duo<Runnable,Integer> menus:executes)
				result.add(sla.getResources().getString(menus.getB()));
			return result.toArray(new String[result.size()]);
		}
		
		public void attachNewActivity(ServerListActivityImpl sla) {
			this.sla = sla;
		}

		public Server getItem(int ofs) {
			return sla.list.get(ofs);
		}


		public void add(Server object) {
			if (!sla.list.contains(object)) {
				sla.statLayout.addStatuses(1);
				sla.list.add(object);
				notifyItemInserted(getItemCount());
			}
		}

		public void addAll(Server[] items) {
			for (Server s:items)add(s);
		}

		public void addAll(Collection<? extends Server> collection) {
			for (Server s:collection)add(s);
		}

		public void remove(Server object) {
			int ofs=sla.list.indexOf(object);
			sla.list.remove(object);
			notifyItemRemoved(ofs);
		}
		
		public void unselectAll(){
			Set<Server> objects=new HashSet<>(sla.selected);
			sla.selected.clear();
			for(Server s:objects){
				int i=sla.list.indexOf(s);
				if(i>=0){
					notifyItemChanged(i);
				}
			}
		}
	}

	static class PingHandlerImpl implements ServerPingProvider.PingHandler {
		boolean closeDialog;
		Intent extras;
		Bundle obj;
		boolean isUpd;
		public PingHandlerImpl(boolean isUpdate) {
			this(false, new Intent(),isUpdate);
		}
		public PingHandlerImpl(boolean cd, Intent os,boolean isUpdate) {
			this(cd, os, true,isUpdate);
		}
		public PingHandlerImpl(boolean cd, Intent os, boolean updSrl,boolean isUpdate) {
			this(cd, os, updSrl, null,isUpdate);
		}
		public PingHandlerImpl(boolean cd, Intent os, boolean updSrl, Bundle receive,boolean isUpdate) {
			closeDialog = cd;
			extras = os;
			if (updSrl)act().srl.setRefreshing(true);
			obj = receive;
			isUpd=isUpdate;
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
							act().pinging.put(sn, false);
							act().statLayout.setStatusAt(i_, 0);
							act().sl.notifyItemChanged(i_);
							if (closeDialog)
								act().wd.hideWorkingDialog(sn);
							if (!act().pinging.containsValue(true))
								act().srl.setRefreshing(false);
							if(act().pref.getBoolean("letRetryPing",false)){
								if(act().retrying.containsKey(s)){
									final Map.Entry<Boolean,Integer> kvp=act().retrying.get(s);
									int remaining=kvp.getValue();
									if(remaining==0){
										act().retrying.remove(s);//We don't retry anymore
										if (extras.getIntExtra("offset",-1) != -1)
											Utils.makeNonClickableSB(act(), R.string.serverOffline, Snackbar.LENGTH_SHORT).show();
									}else{
										kvp.setValue(remaining-1);
										act().getWindow().getDecorView().getHandler().postDelayed(new Runnable(){
												public void run(){
													(kvp.getKey()?act().updater:act().spp).putInQueue(s,PingHandlerImpl.this);
													act().pinging.put(s,true);
													if(closeDialog)
														act().wd.showWorkingDialog(s);
												}
											},1000);
									}
								}else{
									act().retrying.put(s,new KVP<Boolean,Integer>(isUpd,Integer.valueOf(act().pref.getString("retryIteration","10"))));
									act().getWindow().getDecorView().getHandler().postDelayed(new Runnable(){
											public void run(){
												(isUpd?act().updater:act().spp).putInQueue(s,PingHandlerImpl.this);
												act().pinging.put(s,true);
												if(closeDialog)
													act().wd.showWorkingDialog(s);
											}
										},1000);
								}
							}else{
								if (extras.getIntExtra("offset",-1) != -1)
									Utils.makeNonClickableSB(act(), R.string.serverOffline, Snackbar.LENGTH_SHORT).show();
							}
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
							act().pinging.put(s, false);
							act().statLayout.setStatusAt(i_, 2);
							act().sl.notifyItemChanged(i_);
							if (extras.getIntExtra("offset",-1) != -1) {
								Intent caller = new Intent().putExtras(extras).putExtra("stat", Utils.encodeForServerInfo(s));
								if (obj != null) {
									caller.putExtra("object", obj);
								}
								act().startServerInfoActivity(caller,i_);
							}
							if (closeDialog) {
								act().wd.hideWorkingDialog(s);
							}

							if (!act().pinging.containsValue(true)) {
								act().srl.setRefreshing(false);
							}
							act().retrying.remove(s);
						} catch (final Throwable e) {
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
		super.onCreate(savedInstanceState);
		instance = new WeakReference(this);
		new Thread(){
			public void run(){
				List<Class> classes=new ArrayList<>();
				Class now=ServerListActivity.class;
				while(now!=Object.class){
					classes.add(now);
					now=now.getSuperclass();
				}
				List<StackTraceElement> ste=new ArrayList<>();
				for(Class c:classes){
					Log.i("Superclasses",c.getName());
					ste.add(new StackTraceElement(c.getName(),"<init>",c.getSimpleName()+".test",c.hashCode()));
				}
				Throwable t=new Throwable("Debug");
				t.setStackTrace(ste.toArray(new StackTraceElement[ste.size()]));
				CollectorMain.reportError("Debug",t);
			}
		}.start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		instance.clear();
	}

	@Override
	public void finish() {
		super.finish();
		DebugWriter.writeToI("hookFinishCall",new Throwable());
	}
}

