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

public class MainActivity extends AppCompatListActivity
{
	List<Server> list;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		ServerListAdapter sla=new ServerListAdapter();
		setListAdapter(sla);
		getListView().setLongClickable(true);
		getListView().setOnItemClickListener(sla);
		getListView().setOnItemLongClickListener(sla);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO: Implement this method
		SubMenu add=menu.addSubMenu(Menu.NONE,1,0,R.string.add);
		add.add(Menu.NONE,1,1,R.string.addSingle);
		add.add(Menu.NONE,1,2,R.string.importFromWc);
		return true;
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
			return super.getView(position, convertView, parent);
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
