package com.nao20010128nao.Wisecraft.widget;

import com.nao20010128nao.Wisecraft.*;

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
