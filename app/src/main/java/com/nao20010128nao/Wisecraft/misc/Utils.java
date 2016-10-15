package com.nao20010128nao.Wisecraft.misc;
import android.annotation.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.content.res.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.preference.*;
import android.support.design.widget.*;
import android.support.v7.widget.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.api.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import com.nao20010128nao.Wisecraft.misc.pinger.*;
import java.io.*;
import java.lang.reflect.*;
import java.security.*;
import java.util.*;

import android.support.v7.widget.Toolbar;
import com.nao20010128nao.Wisecraft.R;

public class Utils extends PingerUtils{
	public static String deleteDecorations(String decorated) {
		StringBuilder sb=new StringBuilder();
		char[] chars=decorated.toCharArray();
		int offset=0;
		while (chars.length > offset) {
			if (chars[offset] == '§') {
				offset += 2;
				continue;
			}
			sb.append(chars[offset]);
			offset++;
		}
		return sb.toString();
	}
	public static CharSequence parseMinecraftFormattingCode(String s,int defColor){
		try {
			MinecraftFormattingCodeParser mfcp=new MinecraftFormattingCodeParser();
			mfcp.loadFlags(s);
			mfcp.defaultColor=defColor;
			return mfcp.build();
		} catch (Throwable e) {
			return s;
		}
	}
	public static boolean isNullString(String s) {
		return TextUtils.isEmpty(s);
	}
	public static String[] lines(String s) {
		try {
			BufferedReader br=new BufferedReader(new StringReader(s));
			List<String> tmp=new ArrayList<>(4);
			String line=null;
			while (null != (line = br.readLine()))tmp.add(line);
			return tmp.toArray(new String[tmp.size()]);
		} catch (Throwable e) {
			return Constant.EMPTY_STRING_ARRAY;
		}
	}
	public static boolean writeToFile(File f, String content) {
		return writeToFileByBytes(f,content.getBytes(CompatCharsets.UTF_8));
	}
	public static String readWholeFile(File f) {
		return new String(readWholeFileInBytes(f),CompatCharsets.UTF_8);
	}
	public static boolean writeToFileByBytes(File f, byte[] content) {
		FileOutputStream fos=null;
		try {
			(fos = new FileOutputStream(f)).write(content);
			return true;
		} catch (Throwable e) {
			return false;
		} finally {
			try {
				if (fos != null)fos.close();
			} catch (IOException e) {}
		}
	}
	public static byte[] readWholeFileInBytes(File f) {
		FileInputStream fis=null;byte[] buf=new byte[8192];
		ByteArrayOutputStream baos=new ByteArrayOutputStream(8192);
		try {
			fis = new FileInputStream(f);
			while (true) {
				int r=fis.read(buf);
				if (r <= 0)
					break;
				baos.write(buf, 0, r);
			}
			return baos.toByteArray();
		} catch (Throwable e) {
			return null;
		} finally {
			try {
				if (fis != null)fis.close();
			} catch (IOException e) {}
		}
	}
	public static void copyAndClose(InputStream is, OutputStream os)throws IOException {
		byte[] buf=new byte[100];
		try {
			while (true) {
				int r=is.read(buf);
				if (r <= 0)
					break;
				os.write(buf, 0, r);
			}
		} finally {
			is.close();
			os.close();
		}
	}
	public static <T> T requireNonNull(T obj) {
		if (obj == null)
			throw new NullPointerException();
		return obj;
	}
	public static String randomText() {
		return randomText(16);
	}
	public static String randomText(int len) {
		StringBuilder sb=new StringBuilder(len*2);
		byte[] buf=new byte[len];
		new SecureRandom().nextBytes(buf);
		for (byte b:buf)
			sb.append(Character.forDigit(b >> 4 & 0xF, 16)).append(Character.forDigit(b & 0xF, 16));
		return sb.toString();
	}
	public static List<Server> convertServerObject(List<com.nao20010128nao.McServerList.Server> from) {
		ArrayList<Server> result=new ArrayList<>();
		for (com.nao20010128nao.McServerList.Server obj:from) {
			Server wcs=new Server();
			wcs.ip = obj.ip;
			wcs.port = obj.port;
			wcs.mode = obj.isPE?0:1;
			result.add(wcs);
		}
		return result;
	}

	public static int getVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
			DebugWriter.writeToE("Utils",e);
			return 0;
        }
    }

    public static String getVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            DebugWriter.writeToE("Utils",e);
			return "";
        }
    }
	
	public static byte[] readAll(InputStream is)throws IOException{
		ByteArrayOutputStream os=new ByteArrayOutputStream(1000);
		byte[] buf=new byte[1000];
		try {
			while (true) {
				int r=is.read(buf);
				if (r <= 0)
					break;
				os.write(buf, 0, r);
			}
		} finally {
			is.close();
		}
		return os.toByteArray();
	}
	public static <T> List<T>  trueValues(List<T> all, boolean[] balues) {
		List<T> lst=new ArrayList<T>();
		for (int i=0;i < balues.length;i++)
			if (balues[i])
				lst.add(all.get(i));
		return lst;
	}
	public static <T> T[] trueValues(T[] all, boolean[] balues) {
		List<T> lst=new ArrayList<T>();
		for (int i=0;i < balues.length;i++)
			if (balues[i])
				lst.add(all[i]);
		return lst.toArray((T[])Array.newInstance(all.getClass().getComponentType(),lst.size()));
	}
	public static TextView getActionBarTextView(Toolbar mToolBar) {
		try {
			Field f = mToolBar.getClass().getDeclaredField("mTitleTextView");
			f.setAccessible(true);
			return (TextView) f.get(mToolBar);
		} catch (NoSuchFieldException e) {
		} catch (IllegalAccessException e) {
		}
		return null;
	}
	public static android.support.v7.widget.Toolbar getToolbar(Activity decor){
		int[] ids=new int[]{R.id.appbar,R.id.toolbar,R.id.toolbar_layout,R.id.action_bar};
		for(int id:ids){
			View v=decor.getWindow().getDecorView().findViewById(id);
			if(v instanceof android.support.v7.widget.Toolbar){
				return (android.support.v7.widget.Toolbar)v;
			}
		}
		return null;
	}
	public static Point getDisplaySize(Context activity){
        Display display = ((WindowManager)activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        return point;
    }
    @SuppressLint("NewApi")
    public static Point getRealSize(Context activity) {

        Display display = ((WindowManager)activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point point = new Point(0, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            // Android 4.2~
            display.getRealSize(point);
            return point;

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            // Android 3.2~
            try {
                Method getRawWidth = Display.class.getMethod("getRawWidth");
                Method getRawHeight = Display.class.getMethod("getRawHeight");
                int width = (Integer) getRawWidth.invoke(display);
                int height = (Integer) getRawHeight.invoke(display);
                point.set(width, height);
                return point;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return point;
    }
	public static Point getViewSize(View View){
        Point point = new Point(0, 0);
        point.set(View.getWidth(), View.getHeight());        

        return point;
    }
	public static int calculateRows(Context c,int value){
		int base=c.getResources().getDimensionPixelSize(R.dimen.panel_base_size);
		return Math.max(1,(int)Math.max(1,((double)value)/((double)base))+getPreferences(c).getInt("addLessRows",0));
	}
	public static int calculateRows(Context c){
		int base=c.getResources().getDimensionPixelSize(R.dimen.panel_base_size);
		return Math.max(1,(int)Math.max(1,((double)getScreenWidth(c,base))/((double)base))+getPreferences(c).getInt("addLessRows",0));
	}
	public static int calculateRows(Context c,View v){
		int base=c.getResources().getDimensionPixelSize(R.dimen.panel_base_size);
		int width;Configuration cfg=c.getResources().getConfiguration();
		Point point=getViewSize(v);
		switch(cfg.orientation){
			case Configuration.ORIENTATION_LANDSCAPE :width=Math.max(point.x,point.y);break;
			case Configuration.ORIENTATION_PORTRAIT  :width=Math.min(point.x,point.y);break;
			case Configuration.ORIENTATION_SQUARE    :width=point.x;                  break;
			case Configuration.ORIENTATION_UNDEFINED :width=base;                     break;
			default                                  :width=base;                     break;
		}
		return Math.max(1,(int)Math.max(1,((double)width)/((double)base))+getPreferences(c).getInt("addLessRows",0));
	}
	public static int calculateRowsNoAdjust(Context c,int value){
		int base=c.getResources().getDimensionPixelSize(R.dimen.panel_base_size);
		return (int)Math.max(1,((double)value)/((double)base));
	}
	public static int calculateRowsNoAdjust(Context c){
		int base=c.getResources().getDimensionPixelSize(R.dimen.panel_base_size);
		return (int)Math.max(1,((double)getScreenWidth(c,base))/((double)base));
	}
	public static int calculateRowsNoAdjust(Context c,View v){
		int base=c.getResources().getDimensionPixelSize(R.dimen.panel_base_size);
		int width;Configuration cfg=c.getResources().getConfiguration();
		Point point=getViewSize(v);
		switch(cfg.orientation){
			case Configuration.ORIENTATION_LANDSCAPE :width=Math.max(point.x,point.y);break;
			case Configuration.ORIENTATION_PORTRAIT  :width=Math.min(point.x,point.y);break;
			case Configuration.ORIENTATION_SQUARE    :width=point.x;                  break;
			case Configuration.ORIENTATION_UNDEFINED :width=base;                     break;
			default                                  :width=base;                     break;
		}
		return (int)Math.max(1,((double)width)/((double)base));
	}
	public static int getScreenWidth(Context c){
		return getScreenWidth(c,-1);
	}
	public static int getScreenHeight(Context c){
		return getScreenHeight(c,-1);
	}
	public static int getScreenWidth(Context c,int def){
		int width;Configuration cfg=c.getResources().getConfiguration();
		Point point=getRealSize(c);
		switch(cfg.orientation){
			case Configuration.ORIENTATION_LANDSCAPE :width=Math.max(point.x,point.y);break;
			case Configuration.ORIENTATION_PORTRAIT  :width=Math.min(point.x,point.y);break;
			case Configuration.ORIENTATION_SQUARE    :width=point.x;                  break;
			case Configuration.ORIENTATION_UNDEFINED :width=def;                      break;
			default                                  :width=def;                      break;
		}
		return width;
	}
	public static int getScreenHeight(Context c,int def){
		int width;Configuration cfg=c.getResources().getConfiguration();
		Point point=getRealSize(c);
		switch(cfg.orientation){
			case Configuration.ORIENTATION_LANDSCAPE :width=Math.min(point.x,point.y);break;
			case Configuration.ORIENTATION_PORTRAIT  :width=Math.max(point.x,point.y);break;
			case Configuration.ORIENTATION_SQUARE    :width=point.y;                  break;
			case Configuration.ORIENTATION_UNDEFINED :width=def;                      break;
			default                                  :width=def;                      break;
		}
		return width;
	}
	public static Object tryExecuteMethod(Object object,String methodName,Class[] signature,Object[] parameter){
		Class objClass;
		if(object instanceof Class){
			objClass=(Class)object;
		}else{
			objClass=object.getClass();
		}
		try {
			return objClass.getMethod(methodName, signature).invoke(object instanceof Class ?null: object, parameter);
		} catch (Throwable e) {
			return null;
		}
	}
	public static Object tryExecuteMethod(Object object,String methodName){
		return tryExecuteMethod(object,methodName);
	}
	public static boolean isOnline(Context ctx){
		ConnectivityManager cm=(ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
	}
	public static TextView getActionBarTextView(Activity a) {
		return getActionBarTextView(getToolbar(a));
	}
	public static Server makeServerFromBundle(Bundle bnd){
		String ip=bnd.getString(Server.class.getName()+".ip");
		int port=bnd.getInt(Server.class.getName()+".port");
		int mode=bnd.getInt(Server.class.getName()+".mode");
		Server s=new Server();
		s.ip=ip;
		s.port=port;
		s.mode=mode;
		return s;
	}
	public static Server[] makeServersFromBundle(Bundle bnd){
		Parcelable[] data=bnd.getParcelableArray(Server.class.getName()+"#servers");
		Server[] servers=new Server[data.length];
		for(int i=0;i<data.length;i++)servers[i]=makeServerFromBundle((Bundle)data[i]);
		return servers;
	}
	public static void putServerIntoBundle(Bundle bnd,Server s){
		bnd.putString(Server.class.getName()+".ip",s.ip);
		bnd.putInt(Server.class.getName()+".port",s.port);
		bnd.putInt(Server.class.getName()+".mode",s.mode);
	}
	public static Bundle putServerIntoBundle(Server s){
		Bundle bnd=new Bundle();
		putServerIntoBundle(bnd,s);
		return bnd;
	}
	public static void putServersIntoBundle(Bundle bnd,Server[] s){
		Bundle[] data=new Bundle[s.length];
		for(int i=0;i<s.length;i++)data[i]=putServerIntoBundle(s[i]);
		bnd.putParcelableArray(Server.class.getName()+"#servers",data);
	}
	public static Bundle putServersIntoBundle(Server[] s){
		Bundle bnd=new Bundle();
		putServersIntoBundle(bnd,s);
		return bnd;
	}
	public static LayoutInflater fixLayoutInflaterIfNeeded(Context c,Activity a){
		LayoutInflater li=LayoutInflater.from(c);
		if(li.getClass().getName().equals("uk.co.chrisjenx.calligraphy.CalligraphyLayoutInflater")){
			LayoutInflater ali=LayoutInflater.from(a);
			if(li.getFactory()==null){
				li.setFactory(ali.getFactory());
			}
			if(li.getFactory2()==null){
				li.setFactory2(ali.getFactory2());
			}
		}
		return li;
	}
	public static LayoutInflater fixLayoutInflaterIfNeeded(Context c){
		LayoutInflater li=LayoutInflater.from(c);
		if(li.getClass().getName().equals("uk.co.chrisjenx.calligraphy.CalligraphyLayoutInflater")){
			if(li.getFactory()==null){
				li.setFactory(li.getFactory());
			}
			if(li.getFactory2()==null){
				li.setFactory2(li.getFactory2());
			}
		}
		return li;
	}
	public static void applyTypefaceForViewTree(View v,Typeface tf){
		if(v!=null){
			applyTypeface(v,tf);
			if(v instanceof ViewGroup){
				ViewGroup vg=(ViewGroup)v;
				for(int i=0;i<vg.getChildCount();i++){
					applyTypefaceForViewTree(vg.getChildAt(i),tf);
				}
			}
		}
	}
	public static void applyTypeface(View v,Typeface tf){
		try {
			v.getClass().getMethod("setTypeface", Typeface.class).invoke(v, tf);
		} catch (Throwable e) {
			
		}
	}
	public static <T> Object getField(Class<T> clz,T instance,String name){
		try {
			return clz.getField(name).get(instance);
		} catch (Throwable e) {
			return null;
		}
	}
	public static int getModeFromIntent(Intent values){
		if(values.hasExtra(ApiActions.SERVER_INFO_MODE)){
			return values.getIntExtra(ApiActions.SERVER_INFO_MODE,0);
		}else if(values.hasExtra(ApiActions.SERVER_INFO_ISPC)){
			return values.getBooleanExtra(ApiActions.SERVER_INFO_ISPC, false)?1:0;
		}else{
			return 0;
		}
	}
	public static int parseModeName(String name){
		try{
			return Integer.valueOf(name);
		}catch(Throwable e){
			//PE
			if("pe".equalsIgnoreCase(name)){
				return 0;
			}else
			if("phone".equalsIgnoreCase(name)){
				return 0;
			}else
			if("android".equalsIgnoreCase(name)){
				return 0;
			}else
			if("ios".equalsIgnoreCase(name)){
				return 0;
			}else
			if("pocket".equalsIgnoreCase(name)){
				return 0;
			}else
			
			//PC
			if("pc".equalsIgnoreCase(name)){
				return 1;
			}else
			if("desktop".equalsIgnoreCase(name)){
				return 1;
			}else
			if("windows".equalsIgnoreCase(name)){
				return 1;
			}else
			if("mac".equalsIgnoreCase(name)){
				return 1;
			}else
			if("linux".equalsIgnoreCase(name)){
				return 1;
			}else
			if("java".equalsIgnoreCase(name)){
				return 1;
			}
		}
		return 0;
	}
	public static <T> List<T> emptyList(){
		return new ArrayList<T>();
	}
	public static SharedPreferences getPreferences(Context c){
		return PreferenceManager.getDefaultSharedPreferences(c);
	}
	public static String encodeForServerInfo(ServerStatus s){
		byte[] data=PingSerializeProvider.dumpServerForFile(s);
		return Base64.encodeToString(data,ServerInfoActivity.BASE64_FLAGS);
	}
	public static Snackbar makeSB(Activity a,int t,int l){
		return Snackbar.make(a.findViewById(android.R.id.content),t,l);
	}
	public static Snackbar makeSB(Activity a,String t,int l){
		return Snackbar.make(a.findViewById(android.R.id.content),t,l);
	}
	public static Snackbar makeNonClickableSB(Activity a,int t,int l){
		Snackbar sb=makeSB(a,t,l);
		sb.getView().setClickable(false);
		return sb;
	}
	public static Snackbar makeNonClickableSB(Activity a,String t,int l){
		Snackbar sb=makeSB(a,t,l);
		sb.getView().setClickable(false);
		return sb;
	}
	public static CoordinatorLayout.Behavior newBehavior(String clazz){
		try {
			return (CoordinatorLayout.Behavior)Class.forName(clazz).newInstance();
		} catch (Throwable e) {
			WisecraftError.report("Utils#newBehavior",e);
			return null;
		}
	}
	public static int getMenuTintColor(Context context){
		TypedArray ta=context.obtainStyledAttributes(new int[]{R.attr.wcMenuTintColor});
		int color=ta.getColor(0,Color.BLACK);
		ta.recycle();
		return color;
	}
}
