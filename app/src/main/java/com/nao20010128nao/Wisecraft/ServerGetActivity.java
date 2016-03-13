package com.nao20010128nao.Wisecraft;
import com.nao20010128nao.Wisecraft.misc.WebViewActivity;
import android.os.Bundle;
import android.view.Menu;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;

public class ServerGetActivity extends WebViewActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		new AlertDialog.Builder(this)
			.setSingleChoiceItems(R.array.serverListSites,-1,new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface di,int w){
					di.cancel();
					loadUrl(getResources().getStringArray(R.array.serverListSites)[w]);
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
	public void onBackPressed() {
		// TODO: Implement this method
		if(getWebView().canGoBack()){
			getWebView().goBack();
		}else{
			finish();
		}
	}
}
