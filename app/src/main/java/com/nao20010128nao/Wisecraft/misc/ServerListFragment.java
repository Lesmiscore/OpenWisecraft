package com.nao20010128nao.Wisecraft.misc;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v7.widget.*;
import android.util.*;
import android.view.*;
import com.nao20010128nao.Wisecraft.*;
import java.util.*;

import static com.nao20010128nao.Wisecraft.misc.Utils.*;

public class ServerListFragment<T extends FragmentActivity> extends BaseFragment<T>
{
	int rows=-1;
	
	
	RecyclerView rv;
	ServerListRecyclerAdapter slra;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		slra=new ServerListRecyclerAdapter(getActivity());
	}
	
	@Override
	public void onResume() {
		// TODO: Implement this method
		super.onResume();
		rv.setAdapter(slra);
		setLayoutModeInternal(pref.getInt("serverListStyle2",0));
		setDarkBackground(pref.getBoolean("colorFormattedText", false) & pref.getBoolean("darkBackgroundForServerName", false));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO: Implement this method
		View v= inflater.inflate(R.layout.recycler_view_content,container,false);
		rv=(RecyclerView)v.findViewById(android.R.id.list);
		return v;
	}
	
	public void setDarkBackground(boolean dark){
		if (dark) {
			BitmapDrawable bd=(BitmapDrawable)getResources().getDrawable(R.drawable.soil);
			bd.setTargetDensity(getResources().getDisplayMetrics());
			bd.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
			rv.setBackgroundDrawable(bd);
		}else{
			rv.setBackgroundDrawable(null);
		}
	}
	
	public RecyclerView getRecyclerView(){
		return rv;
	}
	
	public ServerListRecyclerAdapter getAdapter(){
		return slra;
	}
	
	public void setDarkness(boolean dark){
		setDarkBackground(dark);
		slra.setDarkness(dark);
		slra.setForceDarkness(true);
	}
	
	public void setLayoutMode(int mode){
		setLayoutModeInternal(mode);
		slra.setLayoutMode(mode);
	}
	
	public void addServer(Server s){
		slra.add(s);
	}
	public void addServers(Server[] s){
		slra.addAll(s);
	}
	public void addServers(Collection<Server> s){
		slra.addAll(s);
	}
	
	public void removeServer(Server s){
		slra.remove(s);
	}
	
	public void setRows(int row){
		rows=row;
		if(rv==null){
			Log.d("ServerListFragment","rv==null");
			return;
		}
		RecyclerView.LayoutManager lm=rv.getLayoutManager();
		if(lm instanceof GridLayoutManager){
			((GridLayoutManager)lm).setSpanCount(rows);
		}else if(lm instanceof StaggeredGridLayoutManager){
			((StaggeredGridLayoutManager)lm).setSpanCount(rows);
		}
		rv.setLayoutManager(lm);
		Log.d("ServerListFragment","set rows "+rows+" to "+lm);
	}
	
	private void setLayoutModeInternal(int mode){
		int rows=this.rows;
		if(rows==-1){
			rows=calculateRows(getActivity());
		}
		Log.d("ServerListFragment","setLayoutModeInternal:rows=="+rows);
		switch(mode){
			case 0:default:
				rv.setLayoutManager(new LinearLayoutManager(getActivity()));
				break;
			case 1:
				GridLayoutManager glm=new GridLayoutManager(getActivity(),rows);
				rv.setLayoutManager(glm);
				break;
			case 2:
				StaggeredGridLayoutManager sglm=new StaggeredGridLayoutManager(rows,StaggeredGridLayoutManager.VERTICAL);
				rv.setLayoutManager(sglm);
				break;
		}
	}
}
