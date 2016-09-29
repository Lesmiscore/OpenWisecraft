package com.nao20010128nao.Wisecraft.widget;

import android.content.*;
import android.os.*;
import android.preference.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import com.google.gson.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;

public class WidgetStateInspector extends AppCompatActivity 
{
	RecyclerView rv;
	KVRecyclerAdapter<String,?> a;
	Gson gson=new Gson();
	SharedPreferences pref,widgetPref;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recycler_view_content);
		pref=PreferenceManager.getDefaultSharedPreferences(this);
		rv=(RecyclerView)findViewById(android.R.id.list);
		widgetPref=getSharedPreferences("widgets",Context.MODE_PRIVATE);
		
		rv.setLayoutManager(new LinearLayoutManager(this));
		a=new KVRecyclerAdapter<String,Object>(this);
		a.addAll(widgetPref.getAll().entrySet());
		rv.setAdapter(a);
	}
}
