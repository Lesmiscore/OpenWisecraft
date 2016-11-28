package com.nao20010128nao.Wisecraft.api;
import android.content.*;
import android.net.*;
import android.os.*;
import android.support.v7.app.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.activity.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.provider.*;
import java.util.*;

public class RequestedServerInfoActivity extends ApiBaseActivity {
	ServerPingProvider spp=new NormalServerPingProvider();
	Server requested;
	WorkingDialog wd;
	Intent si=new Intent();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		wd = new WorkingDialog(this);
        Intent values=getIntent();
        
		si.setClass(this, ServerInfoActivity.class);
		
		final Server result=new Server();
        if(values.hasExtra(ApiActions.SERVER_INFO_IP)&(values.hasExtra(ApiActions.SERVER_INFO_ISPC)|values.hasExtra(ApiActions.SERVER_INFO_MODE))){
            si.putExtra("nonDetails", values.getBooleanExtra(ApiActions.SERVER_INFO_HIDE_DETAILS, false));
            si.putExtra("nonPlayers", values.getBooleanExtra(ApiActions.SERVER_INFO_HIDE_PLAYERS, false));
            si.putExtra("nonPlugins", values.getBooleanExtra(ApiActions.SERVER_INFO_HIDE_PLUGINS, false));
            si.putExtra("nonUpd"    , values.getBooleanExtra(ApiActions.SERVER_INFO_DISABLE_UPDATE, false));
            si.putExtra("noExport"  , values.getBooleanExtra(ApiActions.SERVER_INFO_DISABLE_EXPORT, false));
            
            result.mode=Utils.getModeFromIntent(values);
            int port=-1;
            if(values.hasExtra(ApiActions.SERVER_INFO_PORT)){
                port=values.getIntExtra(ApiActions.SERVER_INFO_PORT,-1);
            }else{
                switch(result.mode){
                    case 0:
                        port=19132;
                        break;
                    case 1:
                        port=25565;
                        break;
                }
            }
            if(port<1){
                finish();
                return;
            }
            result.port=port;
            result.ip=values.getStringExtra(ApiActions.SERVER_INFO_IP);
        }else if(values.getData()!=null){
            Uri data=values.getData();
            if("wisecraft".equalsIgnoreCase(data.getScheme())){
                List<String> path=data.getPathSegments();
                if(path.size()<3){
                    finish();
                    return;
                }
                result.ip=path.get(0);
                result.port=Integer.valueOf(path.get(1));
                result.mode=Utils.parseModeName(path.get(2));
            }else{
                finish();
                return;
            }
        }
        if(result.mode<0|result.mode>1){
            finish();
            return;
		}
		requested = result.cloneAsServer();
		wd.showWorkingDialog();
		spp.putInQueue(requested, new PingHandlingImpl());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case 0:
				switch (resultCode) {
					case Constant.ACTIVITY_RESULT_UPDATE:
						wd.showWorkingDialog();
						spp.putInQueue(requested, new PingHandlingImpl(data.getIntExtra("offset",0)));
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
		public void onPingArrives(final ServerStatus s) {
            runOnUiThread(new Runnable(){
                    public void run(){
                        startActivityForResult(((Intent)si.clone()).putExtra("offset",offset).putExtra("bottomSheet",false).putExtra("stat",Utils.encodeForServerInfo(s)), 0);
                        wd.hideWorkingDialog();
                    }
                });
		}
		public void onPingFailed(Server s) {
            runOnUiThread(new Runnable(){
                    public void run(){
                        wd.hideWorkingDialog();
                        new AlertDialog.Builder(RequestedServerInfoActivity.this,ThemePatcher.getDefaultDialogStyle(RequestedServerInfoActivity.this))
                            .setMessage(R.string.serverOffline)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface di, int t) {
                                    finish();
                                }
                            })
                            .show();
                    }
                });
		}
	}
}
