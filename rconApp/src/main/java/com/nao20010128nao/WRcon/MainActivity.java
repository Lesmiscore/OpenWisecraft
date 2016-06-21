package com.nao20010128nao.WRcon;
import com.nao20010128nao.Wisecraft.misc.compat.AppCompatListActivity;
import android.os.Bundle;
import com.nao20010128nao.Wisecraft.misc.AppBaseArrayAdapter;
import java.util.ArrayList;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.Adapter;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import java.util.Collection;
import java.util.Arrays;
import android.widget.TextView;
import android.view.Menu;
import android.view.SubMenu;
import android.support.v4.view.MenuItemCompat;
import android.view.MenuItem;
import com.nao20010128nao.Wisecraft.misc.compat.AppCompatAlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.Toast;
import android.support.design.widget.Snackbar;
import android.util.Log;
import com.google.gson.Gson;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.AppCompatEditText;
import java.io.File;
import android.os.Environment;
import android.os.AsyncTask;
import com.nao20010128nao.WRcon.misc.Utils;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

public class MainActivity extends AppCompatListActivity
{
	List<Server> list;
	ServerListAdapter sla;
	Gson gson=new Gson();
	SharedPreferences pref;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		pref=PreferenceManager.getDefaultSharedPreferences(this);
		sla=new ServerListAdapter();
		setListAdapter(sla);
		getListView().setLongClickable(true);
		getListView().setOnItemClickListener(sla);
		getListView().setOnItemLongClickListener(sla);
		loadServers();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO: Implement this method
		SubMenu add=menu.addSubMenu(Menu.NONE,0,1,R.string.add).setIcon(R.drawable.ic_action_new_dark);
		add.add(Menu.NONE,1,1,R.string.addSingle).setIcon(R.drawable.ic_action_new_light);
		add.add(Menu.NONE,2,1,R.string.imporT).setIcon(R.drawable.ic_action_import_export_light);
		add.add(Menu.NONE,3,1,R.string.export).setIcon(R.drawable.ic_action_import_export_light);
		MenuItemCompat.setShowAsAction(add.getItem(),MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
		
		SubMenu misc=menu.addSubMenu(Menu.NONE,4,1,R.string.other).setIcon(R.drawable.abc_ic_menu_moreoverflow_mtrl_alpha);
		misc.add(Menu.NONE,5,1,R.string.aboutApp).setIcon(R.drawable.ic_action_about_light);
		MenuItemCompat.setShowAsAction(misc.getItem(),MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO: Implement this method
		switch(item.getItemId()){
			case 1:
				final View dialogView=getLayoutInflater().inflate(R.layout.server_add_dialog_new,null);
				new AppCompatAlertDialog.Builder(this,R.style.AppAlertDialog)
					.setView(dialogView)
					.setTitle(R.string.add)
					.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface di,int w){
							String ip=((EditText)dialogView.findViewById(R.id.serverIp)).getText().toString();
							String portStr=((EditText)dialogView.findViewById(R.id.serverPort)).getText().toString();
							int port;
							try{
								port=Integer.valueOf(portStr);
							}catch(Throwable e){
								Snackbar.make(findViewById(android.R.id.content),R.string.numError,Snackbar.LENGTH_LONG).show();
								return;
							}
							Server sv=new Server();
							sv.ip=ip;
							sv.port=port;
							sla.add(sv);
							saveServers();
						}
					})
					.setNegativeButton(android.R.string.cancel,null)
					.show();
				break;
			case 2:
				final AppCompatEditText et=new AppCompatEditText(this);
				et.setText(new File(Environment.getExternalStorageDirectory(), "/Wisecraft/rcon_servers.json").toString());
				new AppCompatAlertDialog.Builder(this, R.style.AppAlertDialog)
					.setTitle(R.string.import_typepath)
					.setView(et)
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface di, int w) {
							Snackbar.make(findViewById(android.R.id.content), R.string.importing, Snackbar.LENGTH_LONG).show();
							new Thread(){
								public void run() {
									final Server[] sv;
									String json=Utils.readWholeFile(new File(et.getText().toString()));
									sv = gson.fromJson(json, Server[].class);
									runOnUiThread(new Runnable(){
											public void run() {
												sla.addAll(sv);
												saveServers();
												Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.imported).replace("[PATH]", et.getText().toString()), Snackbar.LENGTH_LONG).show();
											}
										});
								}
							}.start();
						}
					})
					.show();
				break;
			case 3:
				final AppCompatEditText et_=new AppCompatEditText(this);
				et_.setText(new File(Environment.getExternalStorageDirectory(), "/Wisecraft/rcon_servers.json").toString());
				new AppCompatAlertDialog.Builder(this, R.style.AppAlertDialog)
					.setTitle(R.string.export_typepath)
					.setView(et_)
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface di, int w) {
							Snackbar.make(findViewById(android.R.id.content), R.string.exporting, Snackbar.LENGTH_LONG).show();
							new AsyncTask<Void,Void,File>(){
								public File doInBackground(Void... a) {
									File f=new File(Environment.getExternalStorageDirectory(), "/Wisecraft");
									f.mkdirs();
									if (Utils.writeToFile(f = new File(et_.getText().toString()), gson.toJson(list, List.class)))
										return f;
									else
										return null;
								}
								public void onPostExecute(File f) {
									if (f != null) {
										Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.export_complete).replace("[PATH]", f + ""), Snackbar.LENGTH_LONG).show();
									} else {
										Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.export_failed), Snackbar.LENGTH_LONG).show();
									}
								}
							}.execute();
						}
					})
					.show();
				break;
			case 5:
				startActivity(new Intent(this,AboutAppActivity.class));
				break;
		}
		return super.onOptionsItemSelected(item)|true;
	}
	public void loadServers() {
		int version=pref.getInt("serversJsonVersion", 0);
		switch (version) {
			case 0:
				Server[] sa=gson.fromJson(pref.getString("servers", "[]"), Server[].class);
				sla.clear();
				sla.addAll(sa);
				break;
		}
	}
	public void saveServers() {
		new Thread(){
			public void run() {
				String json;
				pref.edit().putInt("serversJsonVersion",0).putString("servers", json = gson.toJson(list)).commit();
				Log.d("json", json);
			}
		}.start();
		sla.notifyDataSetChanged();
	}
	
	class ServerListAdapter extends AppBaseArrayAdapter<Server> implements ListView.OnItemClickListener,ListView.OnItemLongClickListener{
		public ServerListAdapter(){
			super(MainActivity.this,0,list=new ArrayList<Server>());
		}

		@Override
		public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
			// TODO: Implement this method
			startActivity(new Intent(MainActivity.this, RCONActivity.class).putExtra("ip", getItem(p3).ip).putExtra("port", getItem(p3).port));
		}

		@Override
		public boolean onItemLongClick(AdapterView<?> p1, View p2, final int position, long p4) {
			// TODO: Implement this method
			final Server server=getItem(position);
			new AlertDialog.Builder(MainActivity.this)
				.setItems(R.array.rconAppServerSubMenu,new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface di,int w){
							switch(w){
								case 0:
									new AlertDialog.Builder(MainActivity.this,R.style.AppAlertDialog)
										.setMessage(R.string.auSure)
										.setPositiveButton(android.R.string.no,null)
										.setNegativeButton(android.R.string.yes,new DialogInterface.OnClickListener(){
											public void onClick(DialogInterface di,int w){
												sla.remove(server);
												saveServers();
											}
										})
										.show();
									break;
								case 1:
									final View dialogView=getLayoutInflater().inflate(R.layout.server_add_dialog_new,null);
									((EditText)dialogView.findViewById(R.id.serverIp)).setText(server.ip);
									((EditText)dialogView.findViewById(R.id.serverPort)).setText(server.port+"");
									new AppCompatAlertDialog.Builder(MainActivity.this,R.style.AppAlertDialog)
										.setView(dialogView)
										.setTitle(R.string.edit)
										.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener(){
											public void onClick(DialogInterface di,int w){
												String ip=((EditText)dialogView.findViewById(R.id.serverIp)).getText().toString();
												String portStr=((EditText)dialogView.findViewById(R.id.serverPort)).getText().toString();
												int port;
												try{
													port=Integer.valueOf(portStr);
												}catch(Throwable e){
													Snackbar.make(findViewById(android.R.id.content),R.string.numError,Snackbar.LENGTH_LONG).show();
													return;
												}
												server.ip=ip;
												server.port=port;
												saveServers();
											}
										})
										.setNegativeButton(android.R.string.cancel,null)
										.show();
									break;
							}
						}
					})
				.show();
			return true;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO: Implement this method
			if(convertView==null)convertView=getLayoutInflater().inflate(R.layout.main_server_entry,parent,false);
			((TextView)convertView.findViewById(R.id.serverIp)).setText(getItem(position).toString());
			return convertView;
		}
		

		@Override
		public void add(Server object) {
			// TODO: Implement this method
			if (!list.contains(object)){
				super.add(object);
			}
		}

		@Override
		public void addAll(Server[] items) {
			// TODO: Implement this method
			addAll(Arrays.asList(items));
		}

		@Override
		public void addAll(Collection<? extends Server> collection) {
			// TODO: Implement this method
			for (Server s:collection)add(s);
		}
	}
}
