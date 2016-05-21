package com.nao20010128nao.Wisecraft.misc;
import com.nao20010128nao.Wisecraft.misc.compat.AppCompatListActivity;
import android.os.Bundle;
import android.support.v4.widget.ViewDragHelper;
import android.view.View;
import android.support.v7.widget.LinearLayoutCompat;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

public class DraggingView extends LinearLayoutCompat{
	ViewDragHelper vdh;
	int ofs;
	public DraggingView(android.content.Context context) {
		super(context);
		setup(context);
	}
	public DraggingView(android.content.Context context, android.util.AttributeSet attrs) {
		super(context,attrs);
		setup(context);
	}
	public DraggingView(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
		super(context,attrs,defStyleAttr);
		setup(context);
	}
	private void setup(Context ctx){
		ServerListActivityBase3 act;
		if(ctx instanceof ServerListActivityBase3){
			act=(ServerListActivityBase3)ctx;
		}else{
			Log.d("ServerListActivity","DraggingView was inflated out of ServerListActivity! Is this correct?");
			return;
		}
		vdh=act.vdh=vdh=ViewDragHelper.create(this,new DraggingCallback());
	}

	public void setUnmoveableViewOffset(int value){
		ofs=value;
	}

	class DraggingCallback extends ViewDragHelper.Callback {
		@Override
		public boolean tryCaptureView(View p1, int p2) {
			// TODO: Implement this method
			return p1!=getChildAt(ofs);
		}
	}
}
