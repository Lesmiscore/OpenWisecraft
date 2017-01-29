package com.nao20010128nao.Wisecraft.activity;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.util.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import com.nao20010128nao.Wisecraft.misc.serverList.*;
import java.net.*;
import java.util.*;

import android.support.v7.widget.Toolbar;
import com.nao20010128nao.Wisecraft.R;
import android.support.v4.view.*;

abstract class ServerGetActivityImpl extends CompatWebViewActivity {
	public static List<String> addForServerList;
	String domain;
	String[] serverList;
	Snackbar downloading;
	BottomSheetBehavior bottomSheet;
	RecyclerView loadedServerListRv;
	Adapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ThemePatcher.applyThemeForActivity(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bottomsheet_base);
		getLayoutInflater().inflate(R.layout.only_toolbar_cood,(ViewGroup)findViewById(R.id.main));
		getLayoutInflater().inflate(R.layout.webview_activity_compat,(ViewGroup)findViewById(R.id.toolbarCoordinator).findViewById(R.id.frame));
		
		getLayoutInflater().inflate(R.layout.yes_no,(ViewGroup)findViewById(R.id.bottomSheet));
		getLayoutInflater().inflate(R.layout.recycler_view_content,(ViewGroup)findViewById(R.id.ynDecor).findViewById(R.id.frame));
		scanWebView();
		setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
		findViewById(R.id.bottomSheet).setVisibility(View.GONE);
		new Handler().post(new Runnable(){
					public void run(){
						Toolbar tb=Utils.getToolbar(ServerGetActivityImpl.this);
						TextView tv=Utils.getActionBarTextView(tb);
						if(tv!=null){
							tv.setGravity(Gravity.CENTER);
						}
					}
				});
		
		bottomSheet=BottomSheetBehavior.from(findViewById(R.id.bottomSheet));
		bottomSheet.setHideable(true);
		bottomSheet.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback(){
				public void onStateChanged(android.view.View p1, int p2){
					switch(p2){
						case BottomSheetBehavior.STATE_HIDDEN:
							adapter.deleteAll();
							findViewById(R.id.bottomSheet).setVisibility(View.GONE);
							break;
					}
				}
				public void onSlide(android.view.View p1, float p2){
					
				}
			});
		
		loadedServerListRv=(RecyclerView)findViewById(android.R.id.list);
		loadedServerListRv.setLayoutManager(new LinearLayoutManager(this));
		loadedServerListRv.setAdapter(adapter=new Adapter());
		
		findViewById(R.id.yes).setOnClickListener(new View.OnClickListener(){
				public void onClick(View v){
					for(Server s:adapter.getSelection())
						ServerListActivity.instance.get().addIntoList(s);
					bottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
				}
			});
		findViewById(R.id.no).setOnClickListener(new View.OnClickListener(){
				public void onClick(View v){
					bottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
				}
			});
		
		if(!Utils.isOnline(this)){
			new AlertDialog.Builder(this,ThemePatcher.getDefaultDialogStyle(this))
				.setMessage(R.string.offline)
				.setTitle(R.string.error)
				.setOnCancelListener(new DialogInterface.OnCancelListener(){
					public void onCancel(DialogInterface di) {
						finish();
						Log.d("SGA", "cancel");
					}
				})
				.setOnDismissListener(new DialogInterface.OnDismissListener(){
					public void onDismiss(DialogInterface di) {
						//finish();
						Log.d("SGA", "dismiss");
					}
				})
				.show();
			return;
		}
		serverList = createServerListDomains();
		new AlertDialog.Builder(this,ThemePatcher.getDefaultDialogStyle(this))
			.setItems(serverList, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface di, int w) {
					di.dismiss();
					loadUrl("http://" + (domain = serverList[w]) + "/");
				}
			})
			.setTitle(R.string.selectWebSite)
			.setOnCancelListener(new DialogInterface.OnCancelListener(){
				public void onCancel(DialogInterface di) {
					finish();
					Log.d("SGA", "cancel");
				}
			})
			.setOnDismissListener(new DialogInterface.OnDismissListener(){
				public void onDismiss(DialogInterface di) {
					//finish();
					Log.d("SGA", "dismiss");
				}
			})
			.show();
		getWebView().setWebViewClient(new WebViewClient(){
				public void onPageFinished(WebView wv, String url) {
					setTitle(wv.getTitle());
					getSupportActionBar().setSubtitle(wv.getUrl());
				}
			});
		downloading = Snackbar.make(findViewById(android.R.id.content), R.string.serverGetFetch, Snackbar.LENGTH_INDEFINITE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem find=menu.add(Menu.NONE, 0, 0, R.string.findServers).setIcon(TheApplication.getTintedDrawable(R.drawable.ic_search_black_48dp,Utils.getMenuTintColor(this),this));
		MenuItemCompat.setShowAsAction(find,MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 0:
				//List<MslServer>
				downloading.show();
				new AsyncTask<String,Void,Object>(){
					String url;
					boolean[] selections;
					public Object doInBackground(String... a) {
						try {
							return ServerAddressFetcher.findServersInWebpage(new URL(url = a[0]));
						} catch (Throwable e) {
							DebugWriter.writeToD("ServerGetActivity.gettingServer#"+url,e);
							return e;
						}
					}
					public void onPostExecute(Object o) {
						downloading.dismiss();
						if (o instanceof List) {
							//Server list
							final List<MslServer> serv=(List<MslServer>)o;
							/*
							adapter.deleteAll();
							adapter.addAll(serv);
							bottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
							findViewById(R.id.bottomSheet).setVisibility(View.VISIBLE);
							*/
							final ModifiedBottomSheetDialog mbsd=new ModifiedBottomSheetDialog(ServerGetActivityImpl.this);
							final Adapter adapter=new Adapter();
							mbsd.setContentView(R.layout.server_get_recycler);
							final RecyclerView loadedServerListRv=(RecyclerView)mbsd.findViewById(R.id.servers);
							loadedServerListRv.setLayoutManager(new LinearLayoutManager(ServerGetActivityImpl.this));
							loadedServerListRv.setAdapter(adapter);
							adapter.addAll(serv);
							mbsd.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener(){
									public void onClick(View v){
										for(Server s:adapter.getSelection())
											ServerListActivity.instance.get().addIntoList(s);
										mbsd.dismiss();
									}
								});
							mbsd.findViewById(R.id.no).setOnClickListener(new View.OnClickListener(){
									public void onClick(View v){
										mbsd.dismiss();
									}
								});
							mbsd.show();
						} else {
							//Throwable
							String msg=((Throwable)o).getMessage();
							String dialogMsg=msg;
							if (msg.startsWith("This website is not supported")) {
								dialogMsg = getResources().getString(R.string.msl_websiteNotSupported) + url;
							}
							if (msg.startsWith("Unsupported webpage")) {
								dialogMsg = getResources().getString(R.string.msl_unsupportedWebpage) + url;
							}

							new AlertDialog.Builder(ServerGetActivityImpl.this,ThemePatcher.getDefaultDialogStyle(ServerGetActivityImpl.this))
								.setTitle(R.string.error)
								.setMessage(dialogMsg)
								.setPositiveButton(android.R.string.ok, Constant.BLANK_DIALOG_CLICK_LISTENER)
								.show();
						}
					}
				public List<MslServer> getServers(List<MslServer> all, boolean[] balues) {
					List<MslServer> lst=new ArrayList<MslServer>();
						for (int i=0;i < balues.length;i++) {
							if (balues[i]) {
								lst.add(all.get(i));
							}
						}
						return lst;
					}
				}.execute(getWebView().getUrl());
				break;
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		switch(bottomSheet.getState()){
			case BottomSheetBehavior.STATE_EXPANDED:
				bottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
				break;
			case BottomSheetBehavior.STATE_COLLAPSED:
				bottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
				break;
			default:
				if (getWebView().canGoBack()) {
					getWebView().goBack();
				} else {
					finish();
				}
				break;
		}
	}

	public String[] createServerListDomains() {
		List<String> result=new ArrayList<>();
		result.addAll(Arrays.asList(getResources().getStringArray(R.array.serverListSites)));
		if (addForServerList != null)result.addAll(addForServerList);
		return Factories.strArray(result);
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(TheApplication.injectContextSpecial(newBase));
	}
	
	public int getCheckColor(){
		TypedArray ta=obtainStyledAttributes(R.styleable.ServerGetActivity);
		int color=Color.BLACK;
		if(ta.hasValue(R.styleable.ServerGetActivity_wcMenuTintColor)){
			color=ta.getColor(R.styleable.ServerGetActivity_wcMenuTintColor,0);
		}
		if(ta.hasValue(R.styleable.ServerGetActivity_wcSgaCheckColor)){
			color=ta.getColor(R.styleable.ServerGetActivity_wcSgaCheckColor,0);
		}
		ta.recycle();
		return color;
	}
	
	class Adapter extends ListRecyclerViewAdapter<FindableViewHolder,MslServer> {
		Map<MslServer,Boolean> selected=new NonNullableMap<MslServer>();
		
		@Override
		public void onBindViewHolder(FindableViewHolder parent, int offset) {
			((TextView)parent.findViewById(android.R.id.text1)).setText(makeServerTitle(getItem(offset)));
			parent.itemView.setTag(getItem(offset));
			Utils.applyHandlersForViewTree(parent.itemView,new OnClickListener(offset));
			if(selected.get(getItem(offset))){
				parent.findViewById(R.id.check).setVisibility(View.VISIBLE);
			}else{
				parent.findViewById(R.id.check).setVisibility(View.GONE);
			}
			((ImageView)parent.findViewById(R.id.check)).setImageDrawable(TheApplication.instance.getTintedDrawable(R.drawable.ic_check_black_48dp,getCheckColor()));
		}

		@Override
		public FindableViewHolder onCreateViewHolder(ViewGroup parent, int type) {
			return new FindableViewHolder(getLayoutInflater().inflate(R.layout.checkable_list_item,parent,false));
		}
		
		public void clearSelectedState(){
			selected.clear();
			notifyItemRangeChanged(0,size());
		}
		
		public void deleteAll(){
			clear();
			selected.clear();
		}
		
		public List<Server> getSelection(){
			List<MslServer> result=new ArrayList<>();
			for(MslServer srv:new ArrayList<MslServer>(this))
				if(selected.get(srv))
					result.add(srv);
			return Utils.convertServerObject(result);
		}

		String makeServerTitle(MslServer sv){
			StringBuilder sb=new StringBuilder();
			sb.append(sv.ip).append(':').append(sv.port).append(" ");
			sb.append(sv.isPE?"PE":"PC");
			return sb.toString();
		}

		class OnClickListener implements View.OnClickListener{
			int ofs;
			public OnClickListener(int i){ofs=i;}
			@Override
			public void onClick(View p1) {
				MslServer s=getItem(ofs);
				selected.put(s,!selected.get(s));
				notifyItemChanged(ofs);
			}
		}
	}
}
public class ServerGetActivity extends ServerGetActivityImpl{
	
}
