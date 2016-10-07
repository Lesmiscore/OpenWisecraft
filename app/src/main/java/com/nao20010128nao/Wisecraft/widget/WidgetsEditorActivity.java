package com.nao20010128nao.Wisecraft.widget;

import android.content.*;
import android.os.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import java.util.*;

public class WidgetsEditorActivity extends AppCompatActivity {
	SharedPreferences widgetPref;
	RecyclerView rv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		widgetPref=PingWidget.getWidgetPref(this);
		setContentView(R.layout.recycler_view_content);
		rv=(RecyclerView)findViewById(android.R.id.list);
	}
	
	List<String> listWidgets(){
		ArrayList<String> list=new ArrayList<>();
		for(String s:widgetPref.getAll().keySet())
			if(s.endsWith(".data"))continue;
			else if(s.startsWith("_version"))continue;
			else list.add(s);
		return list;
	}
	
	class Adapter extends ListRecyclerViewAdapter<PingWidgetEditorViewHolder,Duo<Server,PingWidget.WidgetData>> {

		@Override
		public PingWidgetEditorViewHolder onCreateViewHolder(ViewGroup p1, int p2) {
			return new PingWidgetEditorViewHolder(WidgetsEditorActivity.this,p1);
		}

		@Override
		public void onBindViewHolder(PingWidgetEditorViewHolder p1, int p2) {
			
		}
	}
}
