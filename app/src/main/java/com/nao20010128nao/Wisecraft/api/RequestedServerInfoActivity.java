package com.nao20010128nao.Wisecraft.api;
import android.content.*;
import android.os.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import com.nao20010128nao.Wisecraft.provider.*;

import com.nao20010128nao.Wisecraft.R;

public class RequestedServerInfoActivity extends ApiBaseActivity {
	ServerPingProvider spp=new NormalServerPingProvider();
	Server reqested;
	WorkingDialog wd;
	Intent si=new Intent();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		wd = new WorkingDialog(this);
		si.setClass(this, ServerInfoActivity.class);
		si.putExtra("nonDetails", getIntent().getBooleanExtra(ApiActions.SERVER_INFO_HIDE_DETAILS, false));
		si.putExtra("nonPlayers", getIntent().getBooleanExtra(ApiActions.SERVER_INFO_HIDE_PLAYERS, false));
		si.putExtra("nonPlugins", getIntent().getBooleanExtra(ApiActions.SERVER_INFO_HIDE_PLUGINS, false));
		si.putExtra("nonUpd"    , getIntent().getBooleanExtra(ApiActions.SERVER_INFO_DISABLE_UPDATE, false));

		Server s=new Server();
		s.ip = getIntent().getStringExtra(ApiActions.SERVER_INFO_IP);
		s.port = getIntent().getIntExtra(ApiActions.SERVER_INFO_PORT, 19132);
		s.mode = getIntent().getBooleanExtra(ApiActions.SERVER_INFO_ISPC, false)?1:0;
		reqested = s.cloneAsServer();
		wd.showWorkingDialog();
		spp.putInQueue(reqested, new PingHandlingImpl());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO: Implement this method
		switch (requestCode) {
			case 0:
				switch (resultCode) {
					case Constant.ACTIVITY_RESULT_UPDATE:
						wd.showWorkingDialog();
						spp.putInQueue(reqested, new PingHandlingImpl(data.getIntExtra("offset",0)));
						break;
					default:
						finish();
				}
				break;
			default:
				finish();
		}
	}
	class PingHandlingImpl implements ServerPingProvider.PingHandler{
		int offset;
		public PingHandlingImpl(){
			this(0);
		}
		public PingHandlingImpl(int ofs){
			offset=ofs;
		}
		public void onPingArrives(ServerStatus s) {
			ServerInfoActivity.stat.add(s);
			int ofs=ServerInfoActivity.stat.indexOf(s);
			startActivityForResult(si.putExtra("offset",offset).putExtra("statListOffset",ofs), 0);
			wd.hideWorkingDialog();
		}
		public void onPingFailed(Server s) {
			wd.hideWorkingDialog();
			new AppCompatAlertDialog.Builder(wd)
				.setMessage(R.string.serverOffline)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface di, int t) {
						finish();
					}
				})
				.show();
		}
	}
}
