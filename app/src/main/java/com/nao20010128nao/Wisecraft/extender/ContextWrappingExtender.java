package com.nao20010128nao.Wisecraft.extender;
import android.content.ContextWrapper;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Configuration;
import android.content.res.AssetManager;
import android.util.DisplayMetrics;

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
		
		dm.density=dm.density/2;
		
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
