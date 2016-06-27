package com.nao20010128nao.Wisecraft.rcon;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.preference.*;
import android.support.v4.app.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import com.nao20010128nao.Wisecraft.misc.rcon.*;
import com.nao20010128nao.Wisecraft.rcon.buttonActions.*;
import java.io.*;
import java.lang.ref.*;
import java.util.*;

import com.nao20010128nao.Wisecraft.rcon.R;
import android.content.res.TypedArray;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public abstract class RCONActivityBase extends AppCompatActivity {
	public static WeakReference<RCONActivityBase> instance=new WeakReference(null);
	static List<String> consoleLogs=new ArrayList<>();
	static RCon rcon;

	PasswordAsking pa;
	FragmentTabHost fth;
	TabHost.TabSpec consoleF,playersF;
	LinearLayout console;
	EditText command;
	Button ok;
	DrawerLayout drawer;
	ListView players;
	TextView playersCount;
	ImageButton updatePlayers;
	ScrollView scrollingConsole;

	ArrayAdapter<String> playersList;
	ArrayList<String> playersListInternal;

	int port;
	String ip;
	boolean living=true;
	boolean drawerOpening=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		instance = new WeakReference(this);
		pa=new PasswordAsking();
		ip = getIntent().getStringExtra("ip");
		port = getIntent().getIntExtra("port", -1);
		if(hasActionBarOnTheme()){
			setContentView(R.layout.rconmain);
		}else{
			setContentView(R.layout.rconmain_noactionbar);
			setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
		}
		if(!hasPasswordCoded()){
			String password=getIntent().getStringExtra("password");
			if(password!=null){
				pa.tryConnectWithDialog(password);
			}else{
				if (rcon == null) {
					pa.askPassword();
				} else {
					applyHandlers();
				}
			}
		}
		fth = (FragmentTabHost)findViewById(android.R.id.tabhost);
		fth.setup(this, getSupportFragmentManager(), R.id.container);

		drawer = (DrawerLayout)findViewById(R.id.mainDrawer);
		drawer.setDrawerListener(new DrawerLayout.DrawerListener(){
				public void onDrawerClosed(View v) {
					drawerOpening = false;
				}
				public void onDrawerOpened(View v) {
					drawerOpening = true;
				}
				public void onDrawerStateChanged(int v) {

				}
				public void onDrawerSlide(View v, float f) {

				}
			});

		playersList = new AppBaseArrayAdapter<String>(this, android.R.layout.simple_list_item_1, playersListInternal = new ArrayList<>(10));

		consoleF = fth.newTabSpec("console");
		consoleF.setIndicator(getResources().getString(R.string.console));
		fth.addTab(consoleF, ConsoleFragment.class, null);

		playersF = fth.newTabSpec("players");
		playersF.setIndicator(getResources().getString(R.string.players));
		fth.addTab(playersF, PlayersFragment.class, null);
	}
	
	public boolean tryConnect(String pass) {
		if(rcon!=null)return true;
		char[] passChars=pass.toCharArray();
		try {
			rcon = new RConModified(ip, port, passChars);
			return true;
		} catch (Throwable e) {
			DebugWriter.writeToE("RCON",e);
			return false;
		}
	}

	public void appendIntoConsole(String s) {
		final ArrayList<String> lines=new ArrayList<String>(Arrays.asList(RconModule_Utils.lines(s)));
		runOnUiThread(new Runnable(){
				public void run() {
					consoleLogs.addAll(lines);
					if(console!=null)
						for(String s:lines)
							console.addView(newTextViewForConsole(s));
					if(scrollingConsole!=null)
						if(doAutoScroll())
							scrollingConsole.fullScroll(ScrollView.FOCUS_DOWN);
				}
			});
		for(String l:lines)
			Log.d("RCON_CONSOLE_LINE",l);
	}
	public void setConsoleLayout(LinearLayout lv) {
		console = lv;
		lv.removeAllViews();
		for (String s:consoleLogs)
			lv.addView(newTextViewForConsole(s));
	}
	public void setCommandTextBox(EditText et) {
		command = et;
	}
	public void setCommandOk(Button bt) {
		ok = bt;
		ok.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v) {
					if (rcon != null) {
						performSend(command.getText().toString());
						runOnUiThread(new Runnable(){
							public void run() {
								command.setText("");
							}
						});
					}
				}
			});
	}
	public void setPlayersListView(ListView lv) {
		(players = lv).setAdapter(playersList);
	}
	public void setPlayersCountTextView(TextView tv) {
		playersCount = tv;
	}
	public void setUpdatePlayersButton(ImageButton tv) {
		(updatePlayers = tv).setOnClickListener(new View.OnClickListener(){
				public void onClick(View v) {
					refreshPlayers();
				}
			});
	}
	public void refreshPlayers() {
		new AsyncTask<Void,Void,String[]>(){
			public String[] doInBackground(Void[] a) {
				try {
					return rcon.list();
				} catch (Throwable e) {
					DebugWriter.writeToE("RCON",e);
					notifyRetriveError();
				}
				return RconModule_Constant.EMPTY_STRING_ARRAY;
			}
			public void onPostExecute(String[] s) {
				playersListInternal.clear();
				playersListInternal.addAll(Arrays.asList(s));
				playersList.notifyDataSetChanged();
				if (playersCount != null)
					playersCount.setText(getResources().getString(R.string.indicatePlayers).replace("[PLAYERS]", s.length + ""));
			}
			public void notifyRetriveError(){
				runOnUiThread(new Runnable(){
					public void run(){
						getPresenter().showSelfMessage(RCONActivityBase.this,R.string.rconListError,Presenter.MESSAGE_SHOW_LENGTH_LONG);
					}
				});
			}
		}.execute();
	}
	TextView newTextViewForConsole(String s) {
		TextView tv=(TextView)getLayoutInflater().inflate(R.layout.rcon_line_textview,null);
		tv.setText(s);
		return tv;
	}

	@Override
	public void onBackPressed() {
		// TODO: Implement this method
		if (drawerOpening) {
			drawer.closeDrawers();
			drawerOpening = false;
		} else {
			exitActivity();
		}
	}
	public void exitActivity() {
		super.finish();
		consoleLogs.clear();
		try {
			if (rcon != null)
				rcon.close();
		} catch (IOException e) {}
		rcon = null;
		living = false;
	}
	public void cancelExitActivity(){
		if(rcon==null)pa.askPassword();
	}
	@Override
	public void finish() {
		exitActivity();
	}
	public void performSend(final String cmd) {
		if (rcon != null) {
			new Thread(){
				public void run() {
					try {
						String s=rcon.send(cmd);
						if (s.equals("")) {
							s = getResources().getString(R.string.emptyResponse);
						}
						appendIntoConsole(s);
					} catch (Throwable e) {
						DebugWriter.writeToE("RCON",e);
						appendIntoConsole(getResources().getString(R.string.rconSendError));
					}
				}
			}.start();
		}
	}
	private boolean hasActionBarOnTheme(){
		TypedArray ta=getTheme().obtainStyledAttributes(new int[]{R.attr.windowNoTitle});
		boolean result=ta.getBoolean(0,false);
		ta.recycle();
		return !result;
	}
	public RCon getRCon() {
		return rcon;
	}
	public Presenter getPresenter(){
		if(this instanceof Presenter){
			return (Presenter)this;
		}else if(getApplication() instanceof Presenter){
			return (Presenter)getApplication();
		}else{
			return null;
		}
	}
	public boolean hasPasswordCoded(){
		return false;
	}
	public boolean doAutoScroll(){
		return false;
	}

	protected void onConnectionFailed(){}
	protected void onConnectionSuccess(String password){}
	
	private void applyHandlers() {
		new Stop(this);
		new Op(this);
		new Deop(this);
		new Kick(this);
		new Ban(this);
		new BanIp(this);
		new Pardon(this);
		new PardonIp(this);
		new Time_Set(this);
		new Gamemode(this);
		new Save_All(this);
		new Save_On(this);
		new Save_Off(this);
		new Give(this);
		new Clear(this);
		new Kill(this);
		new Tell(this);
		new Tp(this);
		new Xp(this);
		new DefaultGamemode(this);
		new Weather(this);
		new Me(this);
		new Banlist(this);
	}
	class PasswordAsking extends ContextThemeWrapper {
		EditText password;
		public PasswordAsking() {
			super(RCONActivityBase.this,getPresenter().getDialogStyleId());
		}
		public void askPassword() {
			if(hasPasswordCoded())return;
			new AppCompatAlertDialog.Builder(this,getPresenter().getDialogStyleId())
				.setView(inflateDialogView())
				.setCancelable(false)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface di, int whi) {
						tryConnectWithDialog(password.getText().toString());
					}
				})
				.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface di, int whi) {
						exitActivity();
					}
				})
				.show();
		}
		public void tryConnectWithDialog(final String s){
			appendIntoConsole(getResources().getString(R.string.connecting));
			new AsyncTask<Void,Void,Boolean>(){
				public Boolean doInBackground(Void[] o) {
					return tryConnect(s);
				}
				public void onPostExecute(Boolean result) {
					if (!living)return;
					if (result) {
						appendIntoConsole(getResources().getString(R.string.connected));
						applyHandlers();
						refreshPlayers();
						onConnectionSuccess(s);
					} else {
						appendIntoConsole(getResources().getString(R.string.incorrectPassword));
						getPresenter().showSelfMessage(RCONActivityBase.this, R.string.incorrectPassword, Presenter.MESSAGE_SHOW_LENGTH_SHORT);
						askPassword();
						onConnectionFailed();
					}
				}
			}.execute();
		}
		View inflateDialogView() {
			View v=getLayoutInflater().inflate(R.layout.askpassword, null, false);
			password = (EditText)v.findViewById(R.id.password);
			return v;
		}
	}
	public static class ConsoleFragment extends BaseFragment<RCONActivityBase> {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO: Implement this method
			View v=inflater.inflate(R.layout.console, null, false);
			getParentActivity().setConsoleLayout((LinearLayout)v.findViewById(R.id.consoleText));
			getParentActivity().setCommandOk((Button)v.findViewById(R.id.send));
			getParentActivity().setCommandTextBox((EditText)v.findViewById(R.id.command));
			getParentActivity().scrollingConsole=(ScrollView)v.findViewById(R.id.consoleScroll);
			return v;
		}
	}
	public static class PlayersFragment extends BaseFragment<RCONActivityBase> {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO: Implement this method
			View v=inflater.inflate(R.layout.rcon_players_tab, null, false);
			getParentActivity().setPlayersListView((ListView)v.findViewById(R.id.players));
			getParentActivity().setPlayersCountTextView((TextView)v.findViewById(R.id.playersCount));
			getParentActivity().setUpdatePlayersButton((ImageButton)v.findViewById(R.id.updatePlayers));
			getParentActivity().refreshPlayers();
			return v;
		}
	}
}
