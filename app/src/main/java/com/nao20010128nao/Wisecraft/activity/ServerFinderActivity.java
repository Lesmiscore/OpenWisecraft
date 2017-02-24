package com.nao20010128nao.Wisecraft.activity;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.preference.*;
import android.support.design.widget.*;
import android.support.v4.content.*;
import android.support.v4.view.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.contextwrappers.extender.*;
import com.nao20010128nao.Wisecraft.misc.pinger.pc.*;
import com.nao20010128nao.Wisecraft.misc.pinger.pe.*;
import com.nao20010128nao.Wisecraft.misc.provider.*;
import java.lang.ref.*;
import java.util.*;

import com.nao20010128nao.Wisecraft.R;

import static com.nao20010128nao.Wisecraft.misc.Utils.*;
import com.nao20010128nao.Wisecraft.services.*;
import android.nfc.*;

@ShowsServerList
abstract class ServerFinderActivityImpl extends AppCompatActivity implements ServerListActivityInterface {
	ServerList sl;
	List<ServerStatus> list;
	String ip;
	int mode;
	View dialog,dialog2;
	SharedPreferences pref;
	RecyclerView rv;
	ServerListStyleLoader slsl;
	ServiceConnection lastConnection;
	String tag;
	ServerFinderService.InternalBinder bound;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		ThemePatcher.applyThemeForActivity(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recycler_view_content);
		sl = new ServerList(this);
		rv = (RecyclerView)findViewById(android.R.id.list);
		switch(pref.getInt("serverListStyle2",0)){
			case 0:default:
				rv.setLayoutManager(new LinearLayoutManager(this));
				break;
			case 1:
				GridLayoutManager glm=new GridLayoutManager(this,calculateRows(this));
				rv.setLayoutManager(glm);
				break;
			case 2:
				StaggeredGridLayoutManager sglm=new StaggeredGridLayoutManager(calculateRows(this),StaggeredGridLayoutManager.VERTICAL);
				rv.setLayoutManager(sglm);
				break;
		}
		rv.setAdapter(sl);
		
		if(getIntent().hasExtra("tag")){
			tag=getIntent().getStringExtra("tag");
			launchService();
		}else{
			ip = getIntent().getStringExtra("ip");
			mode = getIntent().getIntExtra("mode", 0);
			new AlertDialog.Builder(this,ThemePatcher.getDefaultDialogStyle(this))
				.setTitle(R.string.serverFinder)
				.setView(dialog = getLayoutInflater().inflate(R.layout.server_finder_start, null, false))
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface di, int w) {
						String ip=((EditText)dialog.findViewById(R.id.ip)).getText().toString();
						int startPort=new Integer(((EditText)dialog.findViewById(R.id.startPort)).getText().toString());
						int endPort=new Integer(((EditText)dialog.findViewById(R.id.endPort)).getText().toString());
						int mode=((CheckBox)dialog.findViewById(R.id.pc)).isChecked()?1:0;
						launchService(ip, Math.min(startPort,endPort), Math.max(startPort,endPort), mode);
					}
				})
				.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface di, int w) {
						finish();
					}
				})
				.setOnCancelListener(new DialogInterface.OnCancelListener(){
					public void onCancel(DialogInterface di) {
						finish();
					}
				})
				.show();
			if (ip != null)((EditText)dialog.findViewById(R.id.ip)).setText(ip);
		}
		((CheckBox)dialog.findViewById(R.id.pc)).setChecked(mode == 0 ?false: true);
		slsl=(ServerListStyleLoader)getSystemService(ContextWrappingExtender.SERVER_LIST_STYLE_LOADER);
		
		findViewById(android.R.id.content).setBackgroundDrawable(slsl.load());
	}
	
	private void launchService(final String ip, final int startPort, final int endPort, final int mode) {
		bindService(new Intent(this,ServerFinderService.class),new ServiceConnection(){
				public void onServiceConnected(android.content.ComponentName p1, android.os.IBinder p2){
					lastConnection=this;
					tag=(bound=(ServerFinderService.InternalBinder)p2).startExploration(ip,startPort,endPort,mode);
					startIntervalUpdate();
				}

				public void onServiceDisconnected(android.content.ComponentName p1){
					finish();
				}
			},0);
	}
	
	private void launchService() {
		bindService(new Intent(this,ServerFinderService.class),new ServiceConnection(){
				public void onServiceConnected(android.content.ComponentName p1, android.os.IBinder p2){
					lastConnection=this;
					bound=(ServerFinderService.InternalBinder)p2;
					startIntervalUpdate();
				}

				public void onServiceDisconnected(android.content.ComponentName p1){
					finish();
				}
			},0);
	}
	
	private void startIntervalUpdate(){
		ServerFinderService.State state=bound.getState(tag);
		setTitle(state.ip+":("+state.start+"~"+state.end+")");
		updateList();
	}
	
	private void updateList(){
		new Handler().postDelayed(new Runnable(){
				public void run(){
					updateList();
				}
			},1000);
		ServerFinderService.State state=bound.getState(tag);
		sl.clear();
		sl.addAll(state.detected.values());
	}
	
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(TheApplication.injectContextSpecial(newBase));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(lastConnection!=null)unbindService(lastConnection);
	}

	@Override
	public void addIntoList(Server s) {
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	class ServerList extends ListRecyclerViewAdapter<ServerStatusWrapperViewHolder,ServerStatus> implements AdapterView.OnItemClickListener {
		ServerFinderActivityImpl sta;

		public ServerList(ServerFinderActivityImpl parent) {
			super(parent.list = new ArrayList<ServerStatus>());
			sta = parent;
		}

		@Override
		@ServerInfoParser
		public void onBindViewHolder(ServerStatusWrapperViewHolder viewHolder, final int offset) {
			sta.slsl.applyTextColorTo(viewHolder);
			ServerStatus s=getItem(offset);
			
			final String title;
			if (s.response instanceof Reply19) {//PC 1.9~
				Reply19 rep=(Reply19)s.response;
				if (rep.description == null) {
					title = s.toString();
				} else {
					title = rep.description.text;
				}
			} else if (s.response instanceof Reply) {//PC
				Reply rep=(Reply)s.response;
				if (rep.description == null) {
					title = s.toString();
				} else {
					title = rep.description;
				}
			} else if (s.response instanceof RawJsonReply) {//PC (Obfuscated)
				RawJsonReply rep = (RawJsonReply) s.response;
				if (!rep.json.has("description")) {
					title = s.toString();
				} else {
					if(rep.json.get("description").isJsonObject()){
						title = rep.json.get("description").getAsJsonObject().get("text").getAsString();
					}else{
						title = rep.json.get("description").getAsString();
					}
				}
			} else if (s.response instanceof UnconnectedPing.UnconnectedPingResult) {
				title = ((UnconnectedPing.UnconnectedPingResult)s.response).getServerName();
			} else {//Unreachable
				title = s.toString();
			}
			if (sta.pref.getBoolean("serverListColorFormattedText", false)) {
				viewHolder.setServerName(parseMinecraftFormattingCode(title,sta.slsl.getTextColor()));
			} else {
				viewHolder.setServerName(deleteDecorations(title));
			}
			viewHolder
				.setStatColor(ContextCompat.getColor(sta, R.color.stat_ok))
				.hideServerPlayers()
				.setTag(s)
				.setPingMillis(s.ping)
				.setServer(s)
				.setServerAddress(s.port + "")
				.hideServerTitle();
			applyHandlersForViewTree(viewHolder.itemView,
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						onItemClick(null, v, offset, Long.MIN_VALUE);
					}
				}
			);
		}

		@Override
		public ServerStatusWrapperViewHolder onCreateViewHolder(ViewGroup parent, int type) {
			switch(sta.pref.getInt("serverListStyle2",0)){
				case 0:default:
					return new ServerStatusWrapperViewHolder(sta,false,parent);
				case 1:case 2:
					return new ServerStatusWrapperViewHolder(sta,true,parent);
			}
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			final Server s=getItem(position);
			if (s instanceof ServerStatus) {
				new AlertDialog.Builder(ServerFinderActivityImpl.this,ThemePatcher.getDefaultDialogStyle(ServerFinderActivityImpl.this))
					.setTitle(s.toString())
					.setItems(R.array.serverFinderMenu, new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface di, int w) {
							switch (w) {
								case 0:
									if(!ServerListActivityImpl.instance.get().list.contains(s)){
										ServerListActivityImpl.instance.get().sl.add(s.cloneAsServer());
										ServerListActivityImpl.instance.get().dryUpdate(s,true);
									}
									break;
							}
						}
					})
					.show();
			}
		}
	}
}
public class ServerFinderActivity extends ServerFinderActivityImpl {
	public static WeakReference<ServerFinderActivity> instance=new WeakReference(null);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		instance = new WeakReference(this);
		super.onCreate(savedInstanceState);
	}
}
