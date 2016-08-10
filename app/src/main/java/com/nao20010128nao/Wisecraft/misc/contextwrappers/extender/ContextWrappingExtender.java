package com.nao20010128nao.Wisecraft.misc.contextwrappers.extender;
import android.content.*;
import android.content.res.*;
import android.util.*;
import android.preference.*;
import java.math.*;
import android.content.res.Resources.*;
import com.nao20010128nao.Wisecraft.*;

public class ContextWrappingExtender extends ContextWrapper
{
	Resources modRes;
	SharedPreferences pref;
	private ContextWrappingExtender(Context ctx){
		super(ctx);
		pref=PreferenceManager.getDefaultSharedPreferences(this);
		
		Configuration cfg;
		AssetManager assets;
		DisplayMetrics dm;
		
		cfg=ctx.getResources().getConfiguration();
		assets=ctx.getResources().getAssets();
		dm=ctx.getResources().getDisplayMetrics();
		
		//density change
		pref.edit().putInt("densityDpi",dm.densityDpi).commit();
		if(pref.contains("changeDpi")){
			BigDecimal mul=new BigDecimal(pref.getString("changeDpi","1")).multiply(new BigDecimal(dm.densityDpi));
			cfg.densityDpi=mul.intValue();
		}
		
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

		@Override
		public int getColor(int id) throws Resources.NotFoundException {
			// TODO: Implement this method
			return getColor(id,getTheme());//Context#getColor(int)
		}

		@Override
		public int getColor(int id, Resources.Theme theme) throws Resources.NotFoundException {
			// color override
			if(id==R.color.stat_ok){
				return pref.getInt("colorStatOk",super.getColor(R.color.stat_ok));
			}
			if(id==R.color.stat_pending){
				return pref.getInt("colorStatPending",super.getColor(R.color.stat_pending));
			}
			if(id==R.color.stat_error){
				return pref.getInt("colorStatError",super.getColor(R.color.stat_error));
			}
			return super.getColor(id, theme);
		}
	}
}
