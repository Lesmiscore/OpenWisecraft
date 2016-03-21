package com.nao20010128nao.Wisecraft.rcon;
import android.content.*;
import android.view.*;
import android.widget.*;
import com.google.rconclient.rcon.*;
import com.nao20010128nao.Wisecraft.rcon.buttonActions.*;
import java.util.*;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.widget.DrawerLayout;
import com.nao20010128nao.Wisecraft.R;
import com.nao20010128nao.Wisecraft.misc.AppBaseArrayAdapter;
import com.nao20010128nao.Wisecraft.misc.BaseFragment;
import java.io.IOException;
import java.lang.ref.WeakReference;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RCONActivity extends FragmentActivity {
	public static WeakReference<RCONActivity> instance=new WeakReference(null);
	static List<String> consoleLogs=new ArrayList<>();
	static RCon rcon;

	PasswordAsking pa=new PasswordAsking();
	FragmentTabHost fth;
	TabHost.TabSpec consoleF,playersF;
	LinearLayout console;
	EditText command;
	Button ok;
	DrawerLayout drawer;
	ListView players;
	TextView playersCount;
	ImageButton updatePlayers;

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
		ip = getIntent().getStringExtra("ip");
		port = getIntent().getIntExtra("port", -1);
		setContentView(R.layout.rconmain);
		if (rcon == null) {
			pa.askPassword();
		} else {
			applyHandlers();
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

		playersList = new AppBaseArrayAdapter<>(this, android.R.layout.simple_list_item_1, playersListInternal = new ArrayList<>(10));

		consoleF = fth.newTabSpec("console");
		consoleF.setIndicator(getResources().getString(R.string.console));
		fth.addTab(consoleF, ConsoleFragment.class, null);

		playersF = fth.newTabSpec("players");
		playersF.setIndicator(getResources().getString(R.string.players));
		fth.addTab(playersF, PlayersFragment.class, null);
	}
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}

	public boolean tryConnect(String pass) {
		char[] passChars=pass.toCharArray();
		try {
			rcon = new RConModified(ip, port, passChars);
			return true;
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
	}

	public void appendIntoConsole(final String s) {
		runOnUiThread(new Runnable(){
				public void run() {
					consoleLogs.add(s);
					console.addView(newTextViewForConsole(s));
				}
			});
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
						new Thread(){
							public void run() {
								try {
									String s=rcon.send(command.getText().toString());
									if (s.equals("")) {
										s = getResources().getString(R.string.emptyResponse);
									}
									appendIntoConsole(s);
									runOnUiThread(new Runnable(){
											public void run() {
												command.setText("");
											}
										});
								} catch (IOException e) {
									e.printStackTrace();
								} catch (IncorrectRequestIdException e) {
									e.printStackTrace();
								}
							}
						}.start();
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
					e.printStackTrace();
				}
				return null;
			}
			public void onPostExecute(String[] s) {
				playersListInternal.clear();
				playersListInternal.addAll(Arrays.asList(s));
				playersList.notifyDataSetChanged();
				if (playersCount != null)
					playersCount.setText(getResources().getString(R.string.indicatePlayers).replace("[PLAYERS]", s.length + ""));
			}
		}.execute();
	}
	TextView newTextViewForConsole(String s) {
		TextView tv=new TextView(this);
		tv.setTypeface(Typeface.MONOSPACE);
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
		finish();
		consoleLogs.clear();
		try {
			if (rcon != null)
				rcon.close();
		} catch (IOException e) {}
		rcon = null;
		living = false;
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
					} catch (IOException e) {
						e.printStackTrace();
					} catch (IncorrectRequestIdException e) {
						e.printStackTrace();
					}
				}
			}.start();
		}
	}
	public RCon getRCon() {
		return rcon;
	}

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
	class PasswordAsking extends ContextWrapper {
		EditText password;
		public PasswordAsking() {
			super(RCONActivity.this);
		}
		public void askPassword() {
			new AlertDialog.Builder(this)
				.setView(inflateDialogView())
				.setCancelable(false)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface di, int whi) {
						appendIntoConsole(getResources().getString(R.string.connecting));
						new AsyncTask<Void,Void,Boolean>(){
							public Boolean doInBackground(Void[] o) {
								return tryConnect(password.getText() + "");
							}
							public void onPostExecute(Boolean result) {
								if (!living)return;
								if (!result) {
									appendIntoConsole(getResources().getString(R.string.incorrectPassword));
									Toast.makeText(PasswordAsking.this, R.string.incorrectPassword, Toast.LENGTH_SHORT).show();
									askPassword();
								} else {
									appendIntoConsole(getResources().getString(R.string.connected));
									applyHandlers();
									refreshPlayers();
								}
							}
						}.execute();
					}
				})
				.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface di, int whi) {
						exitActivity();
					}
				})
				.show();
		}
		View inflateDialogView() {
			View v=getLayoutInflater().inflate(R.layout.askpassword, null, false);
			password = (EditText)v.findViewById(R.id.password);
			return v;
		}
	}
	public static class ConsoleFragment extends BaseFragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO: Implement this method
			View v=inflater.inflate(R.layout.console, null, false);
			instance.get().setConsoleLayout((LinearLayout)v.findViewById(R.id.consoleText));
			instance.get().setCommandOk((Button)v.findViewById(R.id.send));
			instance.get().setCommandTextBox((EditText)v.findViewById(R.id.command));
			return v;
		}
	}
	public static class PlayersFragment extends BaseFragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO: Implement this method
			View v=inflater.inflate(R.layout.rcon_players_tab, null, false);
			instance.get().setPlayersListView((ListView)v.findViewById(R.id.players));
			instance.get().setPlayersCountTextView((TextView)v.findViewById(R.id.playersCount));
			instance.get().setUpdatePlayersButton((ImageButton)v.findViewById(R.id.updatePlayers));
			instance.get().refreshPlayers();
			return v;
		}
	}
}
