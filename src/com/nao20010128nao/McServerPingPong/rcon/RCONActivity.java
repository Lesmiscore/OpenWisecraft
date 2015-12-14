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
	int port;
	String ip;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		instance=new WeakReference(this);
		ip=getIntent().getStringExtra("ip");
		port=getIntent().getIntExtra("port",-1);
		if(rcon==null){
			pa.askPassword();
		}
		setContentView(R.layout.rconmain);
		fth = (FragmentTabHost)findViewById(android.R.id.tabhost);
		fth.setup(this, getSupportFragmentManager(), R.id.container);
		
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
			rcon = new RCon(ip, port, passChars);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (AuthenticationException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void appendIntoConsole(String s){
		consoleLogs.add(s);
		console.addView(newTextViewForConsole(s));
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
	}
	TextView newTextViewForConsole(String s){
		TextView tv=new TextView(this);
		tv.setTypeface(Typeface.MONOSPACE);
		tv.setText(s);
		return tv;
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
								if(!result){
									appendIntoConsole(getResources().getString(R.string.incorrectPassword));
									Toast.makeText(PasswordAsking.this,R.string.incorrectPassword,Toast.LENGTH_SHORT).show();
									askPassword();
								}else{
									appendIntoConsole(getResources().getString(R.string.connected));
								}
							}
						}.execute();
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
			return v;
		}
	}
}
