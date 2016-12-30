package com.nao20010128nao.Wisecraft.widget;

import android.content.*;
import android.os.*;
import android.preference.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import com.google.gson.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import java.util.*;

import com.nao20010128nao.Wisecraft.R;

public class WidgetStateInspector extends AppCompatActivity 
{
	RecyclerView rv;
	KVRecyclerAdapter<String,Object> a;
	Gson gson=new Gson();
	SharedPreferences pref,widgetPref;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recycler_view_content);
		pref=PreferenceManager.getDefaultSharedPreferences(this);
		rv=(RecyclerView)findViewById(android.R.id.list);
		widgetPref=getSharedPreferences("widgets",Context.MODE_PRIVATE);
		
		rv.setLayoutManager(new LinearLayoutManager(this));
		a=new KVRecyclerAdapter<String,Object>(this);
		for(Map.Entry<String,?> ent:widgetPref.getAll().entrySet())
			a.add(new KVP<String,Object>(ent.getKey(),ent.getValue()));
		rv.setAdapter(a);
	}
}
