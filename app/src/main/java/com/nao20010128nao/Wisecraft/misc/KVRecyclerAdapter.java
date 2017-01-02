package com.nao20010128nao.Wisecraft.misc;

import android.content.*;
import android.view.*;
import android.widget.*;
import java.util.*;

import com.nao20010128nao.Wisecraft.R;

public class KVRecyclerAdapter<K,V> extends ListRecyclerViewAdapter<KVRecyclerAdapter.KVVH,Map.Entry<K,V>> 
{
	Context ctx;
	public KVRecyclerAdapter(Context c){
		ctx=c;
	}
	@Override
	public void onBindViewHolder(KVVH parent, int offset) {
		parent.getK().setText(Utils.toString(getItem(offset).getKey()));
		parent.getV().setText(Utils.toString(getItem(offset).getValue()));
	}

	@Override
	public KVVH onCreateViewHolder(ViewGroup parent, int type) {
		return new KVVH(LayoutInflater.from(ctx).inflate(R.layout.data,parent,false));
	}


	
	public static class KVVH extends FindableViewHolder{
		public KVVH(View v){
			super(v);
		}
		public TextView getK(){
			return (TextView)findViewById(R.id.k);
		}
		public TextView getV(){
			return (TextView)findViewById(R.id.v);
		}
	}
}
