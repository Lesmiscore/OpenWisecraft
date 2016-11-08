package com.nao20010128nao.Wisecraft.misc;
import android.app.*;
import android.content.*;
import android.content.res.*;
import android.support.v4.content.*;
import android.support.v7.view.*;
import android.util.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.api.*;
import java.util.*;

public class ThemePatcher{
	private ThemePatcher(){}
	
	public static final int THEME_MODE_LIGHT=0;
	public static final int THEME_MODE_DARK=1;
	public static final int THEME_MODE_DAYNIGHT=2;
	public static final Map<Class<? extends Activity>,Themes> THEMES;
	static{
		Map<Class<? extends Activity>,Themes> themes=new HashMap<>();
		themes.put(
			Activity.class,
			new Themes(
				R.style.AppTheme,
				R.style.AppTheme_Dark,
				R.style.AppTheme_DayNight
			)
		);
		themes.put(
			ServerListActivity.class,
			new Themes(
				R.style.AppTheme_ServerList,
				R.style.AppTheme_ServerList_Dark,
				R.style.AppTheme_ServerList_DayNight
			)
		);
		themes.put(
			ServerInfoActivity.class,
			new Themes(
				R.style.AppTheme_ServerInfo,
				R.style.AppTheme_ServerInfo_Dark,
				R.style.AppTheme_ServerInfo_DayNight
			)
		);
		themes.put(
			RCONActivity.class,
			new Themes(
				R.style.AppTheme_NoActionBar,
				R.style.AppTheme_NoActionBar_Dark,
				R.style.AppTheme_NoActionBar_DayNight
			)
		);
		themes.put(
			ServerGetActivity.class,
			new Themes(
				R.style.AppTheme_NoActionBar,
				R.style.AppTheme_NoActionBar_Dark,
				R.style.AppTheme_NoActionBar_DayNight
			)
		);
		themes.put(
			RequestedServerInfoActivity.class,
			new Themes(
				R.style.AppTheme_Translucent,
				R.style.AppTheme_Translucent,
				R.style.AppTheme_Translucent
			)
		);
		themes.put(
			AddServerActivity.class,
			new Themes(
				R.style.AppDialog,
				R.style.AppDialog_Dark,
				R.style.AppDialog_DayNight
			)
		);
		themes.put(
			OpenSourceActivity.class,
			new Themes(
				R.style.AppTheme_OpenSource,
				R.style.AppTheme_OpenSource,
				R.style.AppTheme_OpenSource
			)
		);
		themes.put(
			AboutAppActivity.class,
			new Themes(
				R.style.AppTheme_OpenSource,
				R.style.AppTheme_OpenSource,
				R.style.AppTheme_OpenSource
			)
		);
		THEMES=Collections.unmodifiableMap(themes);
	}
	
	public static void applyThemeForActivity(Activity a){
		Class<? extends Activity> clazz=Utils.requireNonNull(a).getClass();
		Log.d("ThemePatcher","Applying theme for:"+clazz.getName());
		if(!THEMES.containsKey(clazz)){
			Log.d("ThemePatcher",clazz.getName()+" seems unregistered. Using Activity.class as key.");
			clazz=Activity.class;
		}
		int themeMode=Utils.getPreferences(a).getInt("4.0themeMode",THEME_MODE_LIGHT);
		Themes themes=THEMES.get(clazz);
		switch(themeMode){
			default:
				Log.d("ThemePatcher","Invalid themeMode detected. Using THEME_MODE_LIGHT instead.");
				themeMode=THEME_MODE_LIGHT;
				Utils.getPreferences(a).edit().putInt("4.0themeMode",THEME_MODE_LIGHT).commit();
			case THEME_MODE_LIGHT:
				Log.d("ThemePatcher","Using THEME_MODE_LIGHT.");
				setTheme(a, themes.light);
				break;
			case THEME_MODE_DARK:
				Log.d("ThemePatcher","Using THEME_MODE_DARK.");
				setTheme(a, themes.dark);
				break;
			case THEME_MODE_DAYNIGHT:
				Log.d("ThemePatcher","Using THEME_MODE_DAYNIGHT.");
				setTheme(a, themes.dayNight);
				break;
		}
		Log.d("ThemePatcher","Done. How is it?");
	}
	
	public static int getDefaultStyle(Context a){
		int themeMode=Utils.getPreferences(a).getInt("4.0themeMode",THEME_MODE_LIGHT);
		Themes themes=THEMES.get(Activity.class);
		switch(themeMode){
			default:
				Log.d("ThemePatcher","Invalid themeMode detected. Using THEME_MODE_LIGHT instead.");
				themeMode=THEME_MODE_LIGHT;
				Utils.getPreferences(a).edit().putInt("4.0themeMode",THEME_MODE_LIGHT).commit();
			case THEME_MODE_LIGHT:
				Log.d("ThemePatcher","Using THEME_MODE_LIGHT.");
				return themes.light;
			case THEME_MODE_DARK:
				Log.d("ThemePatcher","Using THEME_MODE_DARK.");
				return themes.dark;
			case THEME_MODE_DAYNIGHT:
				Log.d("ThemePatcher","Using THEME_MODE_DAYNIGHT.");
				return themes.dayNight;
		}
	}
	
	public static Context getStyledContext(Context c){
		return new ContextThemeWrapper(c,getDefaultStyle(c));
	}
	
	public static int getDefaultDialogStyle(Context c){
		TypedArray ta=ThemePatcher.getStyledContext(c).obtainStyledAttributes(new int[]{R.attr.dialogTheme});
		int color=ta.getResourceId(0,R.style.AppAlertDialog);
		ta.recycle();
		return color;
	}
	
	public static int getMainColor(Context c){
		TypedArray ta=ThemePatcher.getStyledContext(c).obtainStyledAttributes(new int[]{R.attr.colorAccent});
		int color=ta.getColor(0,ContextCompat.getColor(c,R.color.mainColor));
		ta.recycle();
		return color;
	}

	private static void setTheme(Activity a, int theme) {
		a.setTheme(theme);
		a.getTheme().applyStyle(theme, true);
	}
	
	
	public static class Themes{
		public final int light,dark,dayNight;
		public Themes(int light,int dark,int dayNight){
			this.light=light;
			this.dark=dark;
			this.dayNight=dayNight;
		}
	}
}
