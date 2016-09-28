package com.nao20010128nao.Wisecraft.widget;

import android.content.*;
import android.os.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import com.google.gson.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import android.preference.*;
import android.content.res.*;
import android.view.View.*;

public class WidgetServerSelectActivity extends AppCompatActivity 
{
	RecyclerView rv;
	Adapter a;
	Gson gson=new Gson();
	SharedPreferences pref,widgetPref;
	int wid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recycler_view_content);
		pref=PreferenceManager.getDefaultSharedPreferences(this);
		rv=(RecyclerView)findViewById(android.R.id.list);
		wid=getIntent().getIntExtra("wid",0);
		widgetPref=getSharedPreferences("widgets",Context.MODE_PRIVATE);
		//We won't apply any style here, because this is only to select.
		rv.setLayoutManager(new LinearLayoutManager(this));
		rv.setAdapter(a=new Adapter());
		Server[] servers=gson.fromJson(pref.getString("servers","[]"),Server[].class);
		a.addAll(servers);
	}
	
	class Adapter extends ListRecyclerViewAdapter<FindableViewHolder,Server> {

		@Override
		public void onBindViewHolder(FindableViewHolder parent, int offset) {
			((TextView)parent.findViewById(android.R.id.text1)).setText(getItem(offset).toString());
			TypedArray ta=obtainStyledAttributes(new int[]{R.attr.selectableItemBackground});
			parent.itemView.setBackground(ta.getDrawable(0));
			ta.recycle();
			parent.itemView.setTag(getItem(offset));
			Utils.applyHandlersForViewTree(parent.itemView,new OnClickListener(offset));
		}

		@Override
		public FindableViewHolder onCreateViewHolder(ViewGroup parent, int type) {
			return new FindableViewHolder(getLayoutInflater().inflate(android.R.layout.simple_list_item_1,parent,false));
		}
		
		class OnClickListener implements View.OnClickListener{
			int ofs;
			public OnClickListener(){}
			public OnClickListener(int i){ofs=i;}
			@Override
			public void onClick(View p1) {
				widgetPref.edit().putString(wid+"",gson.toJson(getItem(ofs))).commit();
				sendBroadcast(new Intent(WidgetServerSelectActivity.this,PingWidget.PingHandler.class).putExtra("wid",wid));
				finish();
			}
		}
	}
}
