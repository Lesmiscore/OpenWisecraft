package com.nao20010128nao.WRcon;
import com.nao20010128nao.Wisecraft.misc.compat.AppCompatListActivity;
import android.os.Bundle;
import com.nao20010128nao.Wisecraft.misc.AppBaseArrayAdapter;
import java.util.ArrayList;

public class MainActivity extends AppCompatListActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setListAdapter(new ServerListAdapter());
	}
	class ServerListAdapter extends AppBaseArrayAdapter<Server>{
		public ServerListAdapter(){
			super(MainActivity.this,0,new ArrayList<Server>());
		}
	}
}
