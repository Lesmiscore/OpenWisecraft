package com.nao20010128nao.Wisecraft.misc.contextwrappers.extender;
import android.content.*;
import android.content.res.*;
import android.util.*;

public class ContextWrappingExtender extends ContextWrapper
{
	Resources modRes;
	private ContextWrappingExtender(Context ctx){
		super(ctx);
		
		Configuration cfg;
		AssetManager assets;
		DisplayMetrics dm;
		
		cfg=ctx.getResources().getConfiguration();
		assets=ctx.getResources().getAssets();
		dm=ctx.getResources().getDisplayMetrics();
		
		modRes=new ExtRes(assets,dm,cfg);
	}

	@Override
	public Resources getResources() {
		// TODO: Implement this method
		return modRes;
	}
	
	
	public static ContextWrappingExtender wrap(Context ctx){
		return new ContextWrappingExtender(ctx);
	}
	
	private class ExtRes extends Resources{
		public ExtRes(AssetManager a,DisplayMetrics d,Configuration c){
			super(a,d,c);
		}
	}
}
