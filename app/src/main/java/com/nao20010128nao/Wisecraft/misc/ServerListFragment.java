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
	ServerListStyleLoader slsl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		slra=new ServerListRecyclerAdapter(getActivity());
		slsl=new ServerListStyleLoader(getActivity());
	}
	
	@Override
	public void onResume() {
		// TODO: Implement this method
		super.onResume();
		rv.setAdapter(slra);
		setLayoutModeInternal(pref.getInt("serverListStyle2",0));
		rv.setBackgroundDrawable(slsl.load());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO: Implement this method
		View v= inflater.inflate(R.layout.recycler_view_content,container,false);
		rv=(RecyclerView)v.findViewById(android.R.id.list);
		return v;
	}
	
	public RecyclerView getRecyclerView(){
		return rv;
	}
	
	public ServerListRecyclerAdapter getAdapter(){
		return slra;
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
		if(rows==-1){
			rows=onCalculateRows();
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
	
	protected int onCalculateRows(){
		return calculateRows(getActivity());
	}
}
