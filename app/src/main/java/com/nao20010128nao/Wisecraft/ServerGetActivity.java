package com.nao20010128nao.Wisecraft;
import android.content.*;
import android.os.*;
import android.support.design.widget.*;
import android.util.*;
import android.view.*;
import android.webkit.*;
import com.nao20010128nao.McServerList.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import java.net.*;
import java.util.*;

public class ServerGetActivity extends CompatWebViewActivity {
	public static List<String> addForServerList;
	String domain;
	String[] serverList;
	Snackbar downloading;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		ThemePatcher.applyThemeForActivity(this);
		super.onCreate(savedInstanceState);
		if(!Utils.isOnline(this)){
			new AppCompatAlertDialog.Builder(this,ThemePatcher.getDefaultDialogStyle(this))
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
		new AppCompatAlertDialog.Builder(this,ThemePatcher.getDefaultDialogStyle(this))
			.setSingleChoiceItems(serverList, -1, new DialogInterface.OnClickListener(){
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
		//Utils.getToolbar(this).setOverflowIcon(TheApplication.instance.getTintedDrawable(R.drawable.abc_ic_menu_moreoverflow_mtrl_alpha, ContextCompat.getColor(this, R.color.upd_2)));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO: Implement this method
		menu.add(Menu.NONE, 0, 0, R.string.findServers);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO: Implement this method
		switch (item.getItemId()) {
			case 0:
				//List<com.nao20010128nao.McServerList.Server>
				downloading.show();
				new AsyncTask<String,Void,Object>(){
					String url;
					boolean[] selections;
					public Object doInBackground(String... a) {
						try {
							return ServerAddressFetcher.findServersInWebpage(new URL(url = a[0]));
						} catch (Throwable e) {
							return e;
						}
					}
					public void onPostExecute(Object o) {
						downloading.dismiss();
						if (o instanceof List) {
							//Server list
							final List<com.nao20010128nao.McServerList.Server> serv=(List<com.nao20010128nao.McServerList.Server>)o;
							String[] servSel=new String[serv.size()];
							for (int i=0;i < servSel.length;i++) {
								servSel[i] = serv.get(i).toString();
							}
							new AppCompatAlertDialog.Builder(ServerGetActivity.this,ThemePatcher.getDefaultDialogStyle(ServerGetActivity.this))
								.setTitle(R.string.selectServers)
								.setMultiChoiceItems(servSel, selections = new boolean[servSel.length], new DialogInterface.OnMultiChoiceClickListener(){
									public void onClick(DialogInterface di, int w, boolean c) {
										selections[w] = c;
									}
								})
								.setPositiveButton(R.string.add, new DialogInterface.OnClickListener(){
									public void onClick(DialogInterface di, int w) {
										List<com.nao20010128nao.McServerList.Server> selected=getServers(serv, selections);
										for (com.nao20010128nao.Wisecraft.misc.Server s:Utils.convertServerObject(selected))
											((ServerListActivityInterface)ServerListActivity.instance.get()/*.getLocalActivityManager().getActivity("main")*/).addIntoList(s);
										di.dismiss();
									}
								})
								.show();
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

							new AppCompatAlertDialog.Builder(ServerGetActivity.this,ThemePatcher.getDefaultDialogStyle(ServerGetActivity.this))
								.setTitle(R.string.error)
								.setMessage(dialogMsg)
								.setPositiveButton(android.R.string.ok, Constant.BLANK_DIALOG_CLICK_LISTENER)
								.show();
						}
					}
					public List<com.nao20010128nao.McServerList.Server> getServers(List<com.nao20010128nao.McServerList.Server> all, boolean[] balues) {
						List<com.nao20010128nao.McServerList.Server> lst=new ArrayList<com.nao20010128nao.McServerList.Server>();
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
		// TODO: Implement this method
		if (getWebView().canGoBack()) {
			getWebView().goBack();
		} else {
			finish();
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
}
