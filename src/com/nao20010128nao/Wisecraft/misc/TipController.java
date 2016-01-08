package com.nao20010128nao.Wisecraft.misc;
import android.app.*;
import android.content.*;
import android.view.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.*;

public class TipController
{
	Context ctx;
	TextView tipView;
	public TipController(Activity act){
		ctx=act;
		tipView=(TextView)act.findViewById(R.id.tip);
	}
	public TipController(Context ctx,TextView tip){
		this.ctx=ctx;
		tipView=tip;
	}
	public TipController(TextView tip){
		ctx=tip.getContext();
		tipView=tip;
	}
	public TipController visible(boolean v){
		tipView.setVisibility(v?View.VISIBLE:View.GONE);
		return this;
	}
	public TipController text(String s){
		tipView.setText(s);
		return this;
	}
	public TipController text(int id){
		return text(ctx.getResources().getString(id));
	}
}
