package com.nao20010128nao.Wisecraft.misc.contextwrappers.extender;
import android.content.*;
import android.content.res.*;
import android.util.*;
import android.preference.*;
import java.math.*;
import android.content.res.Resources.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;

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
		if(!pref.contains("densityDpi"))pref.edit().putInt("densityDpi",dm.densityDpi).commit();
		if(pref.contains("changeDpi")){
			BigDecimal mul=new BigDecimal(pref.getString("changeDpi","1")).multiply(new BigDecimal(pref.getInt("densityDpi",0)));
			cfg.densityDpi=mul.intValue();
		}
		
		modRes=new ExtRes(assets,dm,cfg);
	}

	@Override
	public Resources getResources() {
		// TODO: Implement this method
		return modRes;
	}

	@Override
	public Object getSystemService(String name) {
		// TODO: Implement this method
		if(LAYOUT_INFLATER_SERVICE.equals(name))
			return Utils.fixLayoutInflaterIfNeeded(this);
		else
			return super.getSystemService(name);
	}
	
	
	
	
	public static ContextWrappingExtender wrap(Context ctx){
		return new ContextWrappingExtender(ctx);
	}
	
	private class ExtRes extends Resources{
		public ExtRes(AssetManager a,DisplayMetrics d,Configuration c){
			super(a,d,c);
		}

		@Override
		public int getColor(int id, Resources.Theme theme) throws Resources.NotFoundException {
			// color override
			switch(id){
				case R.color.stat_ok:
					return pref.getInt("colorStatOk",getBaseContext().getColor(R.color.stat_ok));
				case R.color.stat_pending:
					return pref.getInt("colorStatPending",getBaseContext().getColor(R.color.stat_pending));
				case R.color.stat_error:
					return pref.getInt("colorStatError",getBaseContext().getColor(R.color.stat_error));
				case R.color.mainColor:
					//return pref.getInt("colorMainColor",getBaseContext().getColor(R.color.mainColor));
				default:
					return super.getColor(id, theme);
			}
		}

		@Override
		public int getDimensionPixelSize(int id) throws Resources.NotFoundException {
			// TODO: Implement this method
			{
				final int sysUiResA = getIdentifier("navigation_bar_height", "dimen", "android");
				final int sysUiResB = getIdentifier("navigation_bar_height_landscape", "dimen", "android");
				if(id==sysUiResA|id==sysUiResB){
					if(pref.contains("changeDpi")){
						return getDimensionPixelSizeFixed(id);
					}
				}
			}
			{
				int resourceId = getIdentifier("status_bar_height", "dimen", "android");
				if (resourceId > 0) {
					if(id==resourceId){
						return getDimensionPixelSizeFixed(id);
					}
				}
			}
			{
				if(id==R.dimen.tool_bar_top_padding){
					return getDimensionPixelSizeFixed(id);
				}
			}
			return super.getDimensionPixelSize(id);
		}
		private int getDimensionPixelSizeFixed(int id){
			BigDecimal div=new BigDecimal(super.getDimensionPixelSize(id)).divide(new BigDecimal(pref.getString("changeDpi","1")),4,RoundingMode.FLOOR);
			return div.intValue();
		}
	}
}
