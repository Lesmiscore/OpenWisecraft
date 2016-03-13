package com.nao20010128nao.Wisecraft;
import com.nao20010128nao.Wisecraft.misc.WebViewActivity;
import android.os.Bundle;
import android.view.Menu;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.MenuItem;
import android.os.AsyncTask;
import java.util.List;
import java.net.URL;
import com.nao20010128nao.McServerList.ServerAddressFetcher;

public class ServerGetActivity extends WebViewActivity
{
	String domain;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		new AlertDialog.Builder(this)
			.setSingleChoiceItems(R.array.serverListSites,-1,new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface di,int w){
					di.dismiss();
					loadUrl("http://"+(domain=getResources().getStringArray(R.array.serverListSites)[w])+"/");
				}
			})
			.setTitle(R.string.selectWebSite)
			.setOnCancelListener(new DialogInterface.OnCancelListener(){
				public void onCancel(DialogInterface di){
					finish();
					Log.d("SGA","cancel");
				}
			})
			.setOnDismissListener(new DialogInterface.OnDismissListener(){
				public void onDismiss(DialogInterface di){
					//finish();
					Log.d("SGA","dismiss");
				}
			})
			.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO: Implement this method
		menu.add(Menu.NONE,0,0,R.string.findServers);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO: Implement this method
		switch(item.getItemId()){
			case 0:
				//List<com.nao20010128nao.McServerList.Server>
				new AsyncTask<String,Void,Object>(){
					String url;
					public Object doInBackground(String... a){
						try{
							return ServerAddressFetcher.findServersInWebpage(new URL(url=a[0]));
						}catch(Throwable e){
							return e;
						}
					}
					public void onPostExecute(Object o){
						if(o instanceof List){
							//Server list
						}else{
							//Throwable
							String msg=((Throwable)o).getMessage();
							String dialogMsg=msg;
							if(msg.startsWith("This website is not supported")){
								dialogMsg=getResources().getString(R.string.msl_websiteNotSupported)+url;
							}
							if(msg.startsWith("Unsupported webpage")){
								dialogMsg=getResources().getString(R.string.msl_unsupportedWebpage)+url;
							}
							
							new AlertDialog.Builder(ServerGetActivity.this)
								.setTitle(R.string.error)
								.setMessage(dialogMsg)
								.setPositiveButton(android.R.string.ok,Constant.BLANK_DIALOG_CLICK_LISTENER)
								.show();
						}
					}
				}.execute(getWebView().getUrl());
				break;
		}
		return true;
	}
	
	@Override
	public void onBackPressed() {
		// TODO: Implement this method
		if(getWebView().canGoBack()){
			getWebView().goBack();
		}else{
			finish();
		}
	}
}
