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

public class MainActivity extends AppCompatListActivity
{
	List<Server> list;
	ServerListAdapter sla;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		sla=new ServerListAdapter();
		setListAdapter(sla);
		getListView().setLongClickable(true);
		getListView().setOnItemClickListener(sla);
		getListView().setOnItemLongClickListener(sla);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO: Implement this method
		SubMenu add=menu.addSubMenu(Menu.NONE,0,1,R.string.add).setIcon(R.drawable.ic_action_new_light);
		add.add(Menu.NONE,1,1,R.string.addSingle).setIcon(R.drawable.ic_action_new_light);
		add.add(Menu.NONE,2,1,R.string.importFromWc).setIcon(R.drawable.ic_action_import_export_light);
		MenuItemCompat.setShowAsAction(add.getItem(),MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
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
						}
					})
					.setNegativeButton(android.R.string.cancel,null)
					.show();
				break;
			case 2:
				
				break;
		}
		return super.onOptionsItemSelected(item)|true;
	}
	
	
	class ServerListAdapter extends AppBaseArrayAdapter<Server> implements ListView.OnItemClickListener,ListView.OnItemLongClickListener{
		public ServerListAdapter(){
			super(MainActivity.this,0,list=new ArrayList<Server>());
		}

		@Override
		public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
			// TODO: Implement this method
		}

		@Override
		public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4) {
			// TODO: Implement this method
			return false;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO: Implement this method
			if(convertView==null)convertView=getLayoutInflater().inflate(R.layout.main_server_entry,null);
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
