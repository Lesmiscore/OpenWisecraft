package com.nao20010128nao.Wisecraft.widget;

import android.appwidget.*;
import android.content.*;
import android.content.res.*;
import android.os.*;
import android.preference.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.text.*;
import android.view.*;
import android.widget.*;
import com.google.gson.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;

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
		Intent values=getIntent();
		if(values.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)){
			wid = getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		}else if(values.hasExtra("wid")){
			wid=getIntent().getIntExtra("wid",0);
		}else{
			finish();
			return;
		}
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
			((TextView)parent.findViewById(android.R.id.text1)).setText(makeServerTitle(getItem(offset)));
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
		
		String makeServerTitle(Server sv){
			StringBuilder sb=new StringBuilder();
			if(TextUtils.isEmpty(sv.name)||sv.toString().equals(sv.name)){
				sb.append(sv).append(" ");
			}else{
				sb.append(sv.name).append(" (").append(sv).append(") ");
			}
			sb.append(sv.mode==0?"PE":"PC");
			return sb.toString();
		}
		
		class OnClickListener implements View.OnClickListener{
			int ofs;
			public OnClickListener(int i){ofs=i;}
			@Override
			public void onClick(View p1) {
				Server s=getItem(ofs).cloneAsServer();
				s.name=null;
				widgetPref.edit().putString(wid+"",gson.toJson(s)).commit();
				sendBroadcast(new Intent(WidgetServerSelectActivity.this,PingWidget.PingHandler.class).putExtra("wid",wid));
				setResult(RESULT_OK,new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, wid));
				finish();
			}
		}
	}
}
