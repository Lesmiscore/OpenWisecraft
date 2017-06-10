package com.nao20010128nao.Wisecraft.rcon;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.os.*;
import android.support.v4.content.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.annimon.stream.Stream;
import com.astuetz.*;
import com.mikepenz.materialdrawer.*;
import com.mikepenz.materialdrawer.model.*;
import com.mikepenz.materialdrawer.model.interfaces.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.rcon.*;
import com.nao20010128nao.Wisecraft.rcon.*;
import com.nao20010128nao.Wisecraft.rcon.buttonActions.*;
import java.io.*;
import java.lang.ref.*;
import java.math.*;
import java.util.*;

import android.support.v7.widget.Toolbar;
import com.nao20010128nao.Wisecraft.rcon.R;

public abstract class RCONActivityBase extends AppCompatActivity {
	public static WeakReference<RCONActivityBase> instance=new WeakReference(null);
	static List<String> consoleLogs=new ArrayList<>();
	static RCon rcon;

	PasswordAsking pa;
	ViewPager pager;
	PagerSlidingTabStrip psts;
	LinearLayout console;
	EditText command;
	Button ok;
	Drawer drw;
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
	boolean wasSetHandlersCalled=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
		pager=(ViewPager)findViewById(R.id.pager);
		psts=(PagerSlidingTabStrip)findViewById(R.id.tabs);
		UsefulPagerAdapter upa=new UsefulPagerAdapter(this);

		playersList = new AppBaseArrayAdapter<>(this, android.R.layout.simple_list_item_1, playersListInternal = new ArrayList<>(10));

		upa.addTab(ConsoleFragment.class, getResources().getString(R.string.console));
		upa.addTab(PlayersFragment.class, getResources().getString(R.string.players));
		pager.setAdapter(upa);
		psts.setViewPager(pager);
		
		int[] appColors=resolveAppColors();
		int PALE_PRIMARY;
		{
			int palePrimary=appColors[1];
			int r=Color.red(palePrimary);
			int g=Color.green(palePrimary);
			int b=Color.blue(palePrimary);
			int a=new BigDecimal(0xff).multiply(new BigDecimal("0.3")).intValue();

			PALE_PRIMARY = Color.argb(a, r, g, b);
		}
		psts.setOnPageChangeListener(new PstsTabColorUpdater(appColors[1], PALE_PRIMARY, pager, psts));
		psts.setIndicatorColor(appColors[0]);
        
        pager.setCurrentItem(1);
        pager.setCurrentItem(0);
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
		final ArrayList<String> lines= new ArrayList<>(Arrays.asList(RconModule_Utils.lines(s)));
		runOnUiThread(() -> {
            consoleLogs.addAll(lines);
            if(console!=null)
				Stream.of(lines).map(this::newTextViewForConsole).forEach(console::addView);
			if(scrollingConsole!=null)
                if(doAutoScroll())
                    scrollingConsole.fullScroll(ScrollView.FOCUS_DOWN);
        });
		for(String l:lines)
			Log.d("RCON_CONSOLE_LINE",l);
	}
	public void setConsoleLayout(LinearLayout lv) {
		console = lv;
		lv.removeAllViews();
		Stream.of(consoleLogs).map(this::newTextViewForConsole).forEach(lv::addView);
	}
	public void setCommandTextBox(EditText et) {
		command = et;
	}
	public void setCommandOk(Button bt) {
		ok = bt;
		ok.setOnClickListener(v -> {
            if (rcon != null) {
                performSend(command.getText().toString());
                runOnUiThread(() -> command.setText(""));
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
		(updatePlayers = tv).setOnClickListener(v -> refreshPlayers());
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
				runOnUiThread(() -> getPresenter().showSelfMessage(RCONActivityBase.this,R.string.rconListError,Presenter.MESSAGE_SHOW_LENGTH_LONG));
			}
		}.execute();
	}
	TextView newTextViewForConsole(String s) {
		TextView tv=(TextView)getLayoutInflater().inflate(R.layout.rcon_line_textview,console,false);
		tv.setText(s);
		return tv;
	}

	@Override
	public void onBackPressed() {
		if (drw.isDrawerOpen()) {
			drw.closeDrawer();
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
			new Thread(() -> {
                try {
                    String s=rcon.send(cmd);
                    if (TextUtils.isEmpty(s)) {
                        s = getResources().getString(R.string.emptyResponse);
                    }
                    appendIntoConsole(s);
                } catch (Throwable e) {
                    DebugWriter.writeToE("RCON",e);
                    appendIntoConsole(getResources().getString(R.string.rconSendError));
                }
            }).start();
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
	public boolean shouldCloseDrawer(){
		return false;
	}
	public PrimaryDrawerItem onCreatePrimaryDrawerItem(){
		return new PrimaryDrawerItem();
	}
	public SectionDrawerItem onCreateSectionDrawerItem(){
		return new SectionDrawerItem();
	}

	protected void onConnectionFailed(){}
	protected void onConnectionSuccess(String password){}
	
	private void applyHandlers() {
		if(wasSetHandlersCalled){
			return;
		}
		wasSetHandlersCalled=true;
		DrawerBuilder db=new DrawerBuilder(this);
		db.withToolbar(getToolbar());
		List<IDrawerItem> items=new ArrayList<>();
		
		/*serversystem*/
		items.add(onCreateSectionDrawerItem().withName(R.string.serversystem).withDivider(false));
		items.add(new Stop(this).newDrawerItem());
		items.add(new Op(this).newDrawerItem());
		items.add(new Deop(this).newDrawerItem());
		items.add(new Kick(this).newDrawerItem());
		items.add(new Ban(this).newDrawerItem());
		items.add(new BanIp(this).newDrawerItem());
		items.add(new Pardon(this).newDrawerItem());
		items.add(new PardonIp(this).newDrawerItem());
		items.add(new Time_Set(this).newDrawerItem());
		items.add(new Gamemode(this).newDrawerItem());
		items.add(new Save_All(this).newDrawerItem());
		items.add(new Save_On(this).newDrawerItem());
		items.add(new Save_Off(this).newDrawerItem());
		
		/*playertools*/
		items.add(onCreateSectionDrawerItem().withName(R.string.playertools));
		items.add(new Give(this).newDrawerItem());
		items.add(new Clear(this).newDrawerItem());
		items.add(new Kill(this).newDrawerItem());
		items.add(new Tell(this).newDrawerItem());
		items.add(new Tp(this).newDrawerItem());
		items.add(new Xp(this).newDrawerItem());
		items.add(new DefaultGamemode(this).newDrawerItem());
		items.add(new Weather(this).newDrawerItem());
		
		/*nocategory*/
		items.add(onCreateSectionDrawerItem().withName(R.string.nocategory));
		items.add(new Me(this).newDrawerItem());
		items.add(new Banlist(this).newDrawerItem());
		
		db.withDrawerItems(items).withDrawerWidthRes(R.dimen.drawer_width);
		db.withOnDrawerItemClickListener((v, i, di) -> {
            drw.deselect();
            if(shouldCloseDrawer())drw.closeDrawer();
            return true;
        });
		drw=db.build();
		drawer=drw.getDrawerLayout();
	}
	
	
	private android.support.v7.widget.Toolbar getToolbar(){
		int[] ids=new int[]{R.id.appbar,R.id.toolbar,R.id.toolbar_layout,R.id.action_bar};
		for(int id:ids){
			View v=getWindow().getDecorView().findViewById(id);
			if(v instanceof android.support.v7.widget.Toolbar){
				return (android.support.v7.widget.Toolbar)v;
			}
		}
		return null;
	}
	
	private int[] resolveAppColors(){
		int text,underline;
		text=underline=ContextCompat.getColor(this, R.color.mainColor);
		TypedArray ta=obtainStyledAttributes(R.styleable.rconActivityAttrs);
		if(ta.hasValue(R.styleable.rconActivityAttrs_rconTabColor)){
			text=underline=ta.getColor(R.styleable.rconActivityAttrs_rconTabColor,0);
		}
		if(ta.hasValue(R.styleable.rconActivityAttrs_rconTabTextColor)){
			text=ta.getColor(R.styleable.rconActivityAttrs_rconTabTextColor,0);
		}
		if(ta.hasValue(R.styleable.rconActivityAttrs_rconTabUnderlineColor)){
			underline=ta.getColor(R.styleable.rconActivityAttrs_rconTabUnderlineColor,0);
		}
		ta.recycle();
		return new int[]{text,underline};
	}
	
	class PasswordAsking extends ContextThemeWrapper {
		EditText password;
		public PasswordAsking() {
			super(RCONActivityBase.this,getPresenter().getDialogStyleId());
		}
		public void askPassword() {
			if(hasPasswordCoded())return;
			new AlertDialog.Builder(this,getPresenter().getDialogStyleId())
				.setView(inflateDialogView())
				.setCancelable(false)
				.setPositiveButton(android.R.string.ok, (di, whi) -> tryConnectWithDialog(password.getText().toString()))
				.setNegativeButton(android.R.string.cancel, (di, whi) -> exitActivity())
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
			View v=inflater.inflate(R.layout.rcon_players_tab, null, false);
			getParentActivity().setPlayersListView((ListView)v.findViewById(R.id.players));
			getParentActivity().setPlayersCountTextView((TextView)v.findViewById(R.id.playersCount));
			getParentActivity().setUpdatePlayersButton((ImageButton)v.findViewById(R.id.updatePlayers));
			return v;
		}
	}
}
