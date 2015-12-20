package com.nao20010128nao.McServerPingPong.rcon;
import android.support.v4.app.*;
import android.os.*;
import com.nao20010128nao.McServerPingPong.*;
import android.widget.*;
import android.view.*;
import java.util.*;
import java.lang.ref.*;
import android.graphics.*;
import com.google.rconclient.rcon.*;
import android.app.*;
import android.content.*;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import java.io.*;
import android.support.v4.widget.*;
import com.nao20010128nao.McServerPingPong.rcon.buttonActions.*;

public class RCONActivity extends FragmentActivity
{
	public static WeakReference<RCONActivity> instance=new WeakReference(null);
	static List<String> consoleLogs=new ArrayList<>();
	static RCon rcon;
	
	PasswordAsking pa=new PasswordAsking();
	FragmentTabHost fth;
	TabHost.TabSpec consoleF;
	LinearLayout console;
	EditText command;
	Button ok;
	DrawerLayout drawer;
	
	int port;
	String ip;
	boolean living=true;
	boolean drawerOpening=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		instance=new WeakReference(this);
		ip=getIntent().getStringExtra("ip");
		port=getIntent().getIntExtra("port",-1);
		if(rcon==null){
			pa.askPassword();
		}else{
			applyHandlers();
		}
		setContentView(R.layout.rconmain);
		fth = (FragmentTabHost)findViewById(android.R.id.tabhost);
		fth.setup(this, getSupportFragmentManager(), R.id.container);
		
		drawer=(DrawerLayout)findViewById(R.id.mainDrawer);
		drawer.setDrawerListener(new DrawerLayout.DrawerListener(){
			public void onDrawerClosed(View v){
				drawerOpening=false;
			}
			public void onDrawerOpened(View v){
				drawerOpening=true;
			}
			public void onDrawerStateChanged(int v){

			}
			public void onDrawerSlide(View v,float f){

			}
		});
		
		consoleF=fth.newTabSpec("console");
		consoleF.setIndicator(getResources().getString(R.string.console));
		fth.addTab(consoleF,ConsoleFragment.class,null);
	}
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}
	
	public boolean tryConnect(String pass){
		char[] passChars=pass.toCharArray();
		try {
			rcon = new RConModified(ip, port, passChars);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (AuthenticationException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void appendIntoConsole(final String s){
		runOnUiThread(new Runnable(){
			public void run(){
				consoleLogs.add(s);
				console.addView(newTextViewForConsole(s));
			}
		});
	}
	public void setConsoleLayout(LinearLayout lv){
		console=lv;
		lv.removeAllViews();
		for(String s:consoleLogs)
			lv.addView(newTextViewForConsole(s));
	}
	public void setCommandTextBox(EditText et){
		command=et;
	}
	public void setCommandOk(Button bt){
		ok=bt;
		ok.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v){
					if(rcon!=null){
						new Thread(){
							public void run(){
								try {
									String s=rcon.send(command.getText().toString());
									if(s.equals("")){
										s=getResources().getString(R.string.emptyResponse);
									}
									appendIntoConsole(s);
									runOnUiThread(new Runnable(){
											public void run(){
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
	TextView newTextViewForConsole(String s){
		TextView tv=new TextView(this);
		tv.setTypeface(Typeface.MONOSPACE);
		tv.setText(s);
		return tv;
	}

	@Override
	public void onBackPressed() {
		// TODO: Implement this method
		if(drawerOpening){
			drawer.closeDrawers();
			drawerOpening=false;
		}else{
			exitActivity();
		}
	}
	public void exitActivity(){
		finish();
		consoleLogs.clear();
		try {
			if(rcon!=null)
				rcon.close();
		} catch (IOException e) {}
		rcon=null;
		living=false;
	}
	public void performSend(final String cmd){
		if(rcon!=null){
			new Thread(){
				public void run(){
					try {
						String s=rcon.send(cmd);
						if(s.equals("")){
							s=getResources().getString(R.string.emptyResponse);
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
	public RCon getRCon(){
		return rcon;
	}
	
	private void applyHandlers(){
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
	}
	class PasswordAsking extends ContextWrapper {
		EditText password;
		public PasswordAsking(){
			super(RCONActivity.this);
		}
		public void askPassword() {
			new AlertDialog.Builder(this)
				.setView(inflateDialogView())
				.setCancelable(false)
				.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface di,int whi){
						appendIntoConsole(getResources().getString(R.string.connecting));
						new AsyncTask<Void,Void,Boolean>(){
							public Boolean doInBackground(Void[] o){
								return tryConnect(password.getText()+"");
							}
							public void onPostExecute(Boolean result){
								if(!living)return;
								if(!result){
									appendIntoConsole(getResources().getString(R.string.incorrectPassword));
									Toast.makeText(PasswordAsking.this,R.string.incorrectPassword,Toast.LENGTH_SHORT).show();
									askPassword();
								}else{
									appendIntoConsole(getResources().getString(R.string.connected));
									applyHandlers();
								}
							}
						}.execute();
					}
				})
				.setNegativeButton(android.R.string.cancel,new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface di,int whi){
						exitActivity();
					}
				})
				.show();
		}
		View inflateDialogView(){
			View v=getLayoutInflater().inflate(R.layout.askpassword,null,false);
			password=(EditText)v.findViewById(R.id.password);
			return v;
		}
	}
	public static class ConsoleFragment extends android.support.v4.app.Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO: Implement this method
			View v=inflater.inflate(R.layout.console,null,false);
			instance.get().setConsoleLayout((LinearLayout)v.findViewById(R.id.consoleText));
			instance.get().setCommandOk((Button)v.findViewById(R.id.send));
			instance.get().setCommandTextBox((EditText)v.findViewById(R.id.command));
			return v;
		}
	}
}
