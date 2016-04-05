package com.nao20010128nao.Wisecraft;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import com.nao20010128nao.McServerList.ServerAddressFetcher;
import com.nao20010128nao.Wisecraft.misc.WebViewActivity;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class ServerGetActivity extends WebViewActivity {
	public static List<String> addForServerList;
	String domain;
	String[] serverList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		serverList=createServerListDomains();
		new AlertDialog.Builder(this)
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO: Implement this method
		menu.add(Menu.NONE, 0, 0, R.string.findServers);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO: Implement this method
		switch (item.getItemId()) {
			case 0:
				//List<com.nao20010128nao.McServerList.Server>
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
						if (o instanceof List) {
							//Server list
							final List<com.nao20010128nao.McServerList.Server> serv=(List)o;
							String[] servSel=new String[serv.size()];
							for (int i=0;i < servSel.length;i++) {
								servSel[i] = serv.get(i).toString();
							}
							new AlertDialog.Builder(ServerGetActivity.this)
								.setTitle(R.string.selectServers)
								.setMultiChoiceItems(servSel, selections = new boolean[servSel.length], new DialogInterface.OnMultiChoiceClickListener(){
									public void onClick(DialogInterface di, int w, boolean c) {
										selections[w] = c;
									}
								})
								.setPositiveButton(R.string.add, new DialogInterface.OnClickListener(){
									public void onClick(DialogInterface di, int w) {
										List<com.nao20010128nao.McServerList.Server> selected=getServers(serv, selections);
										((ArrayAdapter)ServerListActivityImpl.instance.get().getListAdapter()).addAll(Utils.convertServerObject(selected));
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

							new AlertDialog.Builder(ServerGetActivity.this)
								.setTitle(R.string.error)
								.setMessage(dialogMsg)
								.setPositiveButton(android.R.string.ok, Constant.BLANK_DIALOG_CLICK_LISTENER)
								.show();
						}
					}
					public List<com.nao20010128nao.McServerList.Server> getServers(List<com.nao20010128nao.McServerList.Server> all, boolean[] balues) {
						List lst=new ArrayList();
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
	
	public String[] createServerListDomains(){
		List<String> result=new ArrayList<>();
		result.addAll(Arrays.asList(getResources().getStringArray(R.array.serverListSites)));
		if(addForServerList!=null)result.addAll(addForServerList);
		return Factories.strArray(result);
	}
}
