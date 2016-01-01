package com.nao20010128nao.Wisecraft.api;
import android.os.*;
import android.content.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.provider.*;
import com.nao20010128nao.Wisecraft.misc.*;
import android.app.*;

public class RequestedServerInfoActivity extends ApiBaseActivity
{
	ServerPingProvider spp=new NormalServerPingProvider();
	ServerListActivity.Server reqested;
	WorkingDialog wd;
	Intent si=new Intent();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		wd=new WorkingDialog(this);
		si.setClass(this,ServerInfoActivity.class);
		si.putExtra("nonDetails",getIntent().getBooleanExtra(ApiActions.SERVER_INFO_HIDE_DETAILS,false));
		si.putExtra("nonPlayers",getIntent().getBooleanExtra(ApiActions.SERVER_INFO_HIDE_PLAYERS,false));
		si.putExtra("nonPlugins",getIntent().getBooleanExtra(ApiActions.SERVER_INFO_HIDE_PLUGINS,false));
		si.putExtra("nonUpd"    ,getIntent().getBooleanExtra(ApiActions.SERVER_INFO_DISABLE_UPDATE,false));
		
		ServerListActivity.Server s=new ServerListActivity.Server();
		s.ip=getIntent().getStringExtra(ApiActions.SERVER_INFO_IP);
		s.port=getIntent().getIntExtra(ApiActions.SERVER_INFO_PORT,19132);
		s.isPC=getIntent().getBooleanExtra(ApiActions.SERVER_INFO_IP,false);
		reqested=s.cloneAsServer();
		wd.showWorkingDialog();
		spp.putInQueue(reqested,new ServerPingProvider.PingHandler(){
			public void onPingArrives(ServerListActivity.ServerStatus s){
				ServerInfoActivity.stat=s;
				startActivityForResult(si,0);
				wd.hideWorkingDialog();
			}
			public void onPingFailed(ServerListActivity.Server s){
				wd.hideWorkingDialog();
				new AlertDialog.Builder(wd)
					.setMessage(R.string.serverOffline)
					.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface di,int t){
							finish();
						}
					})
					.show();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO: Implement this method
		switch(requestCode){
			case 0:
				switch(resultCode){
					case Constant.ACTIVITY_RESULT_UPDATE:
						wd.showWorkingDialog();
						spp.putInQueue(reqested,new ServerPingProvider.PingHandler(){
								public void onPingArrives(ServerListActivity.ServerStatus s){
									ServerInfoActivity.stat=s;
									startActivityForResult(si,0);
									wd.hideWorkingDialog();
								}
								public void onPingFailed(ServerListActivity.Server s){
									wd.hideWorkingDialog();
									new AlertDialog.Builder(wd)
										.setMessage(R.string.serverOffline)
										.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener(){
											public void onClick(DialogInterface di,int t){
												finish();
											}
										})
										.show();
								}
							});
						break;
					default:
						finish();
				}
				break;
			default:
				finish();
		}
	}
}
