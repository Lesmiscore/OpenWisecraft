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
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import com.google.gson.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.activity.*;
import com.nao20010128nao.Wisecraft.api.*;
import com.nao20010128nao.Wisecraft.misc.collector.*;
import com.nao20010128nao.Wisecraft.misc.json.*;
import com.nao20010128nao.Wisecraft.misc.pinger.*;
import com.nao20010128nao.Wisecraft.misc.serverList.*;
import java.io.*;
import java.lang.reflect.*;
import java.math.*;
import java.util.*;
import permissions.dispatcher.*;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import com.nao20010128nao.Wisecraft.R;
import permissions.dispatcher.PermissionRequest;

public class Utils extends PingerUtils{
	private static int[] HUE_COLORS;
	
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
	public static CharSequence parseMinecraftFormattingCode(String s){
		try {
			MinecraftFormattingCodeParser mfcp=new MinecraftFormattingCodeParser();
			mfcp.loadFlags(s);
			return mfcp.build();
		} catch (Throwable e) {
			return s;
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
			CompatUtils.safeClose(is,os);
		}
	}
	
	public static List<Server> convertServerObject(List<MslServer> from) {
		ArrayList<Server> result=new ArrayList<>();
		for (MslServer obj:from) {
			Server wcs=new Server();
			wcs.ip = obj.ip;
			wcs.port = obj.port;
			wcs.mode = obj.isPE?0:1;
			result.add(wcs);
		}
		return result;
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
		List<T> lst= new ArrayList<>();
		for (int i=0;i < balues.length;i++)
			if (balues[i])
				lst.add(all.get(i));
		return lst;
	}
	public static <T> T[] trueValues(T[] all, boolean[] balues) {
		List<T> lst= new ArrayList<>();
		for (int i=0;i < balues.length;i++)
			if (balues[i])
				lst.add(all[i]);
		return lst.toArray((T[])Array.newInstance(all.getClass().getComponentType(),lst.size()));
	}
	public static TextView getActionBarTextView(Toolbar mToolBar) {
		if(TextUtils.isEmpty(mToolBar.getTitle()))return null;
		try {
			Field f = mToolBar.getClass().getDeclaredField("mTitleTextView");
			f.setAccessible(true);
			return (TextView) f.get(mToolBar);
		} catch (NoSuchFieldException e) {
		} catch (IllegalAccessException e) {
		}
		try {
			Field f=Toolbar.LayoutParams.class.getDeclaredField("mViewType");
			f.setAccessible(true);
			for (int i=0;i < mToolBar.getChildCount();i++) {
				View v=mToolBar.getChildAt(i);
				if (v instanceof TextView) {
					ViewGroup.LayoutParams lp=v.getLayoutParams();
					int viewType=(int)f.get(lp);
					if (viewType == 1) {
						TextView tv=(TextView)v;
						if(tv.getText().equals(mToolBar.getTitle())||tv.getText()==mToolBar.getTitle()){
							return tv;
						}
					}
				}
			}
		} catch (NoSuchFieldException e) {
		} catch (IllegalAccessException e) {
		} catch (SecurityException e) {
		} catch (IllegalArgumentException e) {
		}
		try {
			return (TextView)mToolBar.getChildAt(1);
		}catch(Throwable e){
			
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
                CollectorMain.reportError("getRealSize",e);
            }
        }

        return point;
    }
	public static Point getViewSize(View view){
        Point point = new Point(0, 0);
        point.set(view.getWidth(), view.getHeight());        

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
			name=name.toLowerCase();
			//PE
			if(name.matches("^(pe|phone|android|ios|pocket)$")){
				return 0;
			}else
			
			//PC
			if(name.matches("^(pc|desktop|windows|mac|linux|java)$")){
				return 1;
			}
		}
		return 0;
	}
	public static <T> List<T> emptyList(){
		return Factories.arrayList();
	}
	public static String encodeForServerInfo(ServerStatus s){
		byte[] data=PingSerializeProvider.dumpServerForFile(s);
		return Base64.encodeToString(data, ServerInfoActivity.BASE64_FLAGS);
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
	
	public static Snackbar makeSB(View a,int t,int l){
		return Snackbar.make(a,t,l);
	}
	public static Snackbar makeSB(View a,String t,int l){
		return Snackbar.make(a,t,l);
	}
	public static Snackbar makeNonClickableSB(View a,int t,int l){
		Snackbar sb=makeSB(a,t,l);
		sb.getView().setClickable(false);
		return sb;
	}
	public static Snackbar makeNonClickableSB(View a,String t,int l){
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
		return ThemePatcher.getMenuTintColor(context);
	}
	public static boolean[] getBooleanArray(Context ctx,int resId){
		TypedArray ta=ctx.getResources().obtainTypedArray(resId);
		boolean[] values=new boolean[ta.length()];
		for(int i=0;i<values.length;i++)
			values[i]=ta.getBoolean(i,false);
		ta.recycle();
		return values;
	}
	public static String toString(Object o){
		return o==null?"null":o.toString();
	}
	public static int determineServerListJsonVersion(String json){
		WisecraftJsonObject ja=WJOUtils.parse(json);
		int maybe=-1;
		for(WisecraftJsonObject entry:ja){
			if(!(entry.has("ip")&entry.has("port"))){
				continue;
			}
			if(entry.has("isPC")){
				maybe=0;
			}
			if(entry.has("mode")&maybe<2){
				maybe=1;
			}
			if(entry.has("name")){
				maybe=2;
			}
		}
		if(maybe==-1){
			if(ja.size()==0){
				maybe=2;
			}else{
				throw new IllegalArgumentException("json is not for server list.");
			}
		}
		return maybe;
	}
	
	@Deprecated
	public static int[] getHueRotatedColors2(){
		if(HUE_COLORS!=null){
			return copyOf(HUE_COLORS,HUE_COLORS.length);
		}
		int[] colors=new int[24];
		BigDecimal _360=new BigDecimal("360");
		for(int i=0;i<24;i++){
			colors[i]=smallRgbTo32bitRgb(hsvToRgb(new BigDecimal(i).multiply(new BigDecimal("15")).divide(_360,10000,BigDecimal.ROUND_CEILING),BigDecimal.ONE,BigDecimal.ONE));
		}
		HUE_COLORS=Arrays.copyOf(colors,colors.length);
		return getHueRotatedColors();
	}
	
	public static int[] getHueRotatedColors(){
		if(HUE_COLORS!=null){
			return copyOf(HUE_COLORS,HUE_COLORS.length);
		}
		int[] colors=new int[24];
		for(int i=0;i<24;i++){
			colors[i]=smallRgbTo32bitRgb(hsvToRgb((float)15*(float)i/(float)360,1,1));
		}
		HUE_COLORS=Arrays.copyOf(colors,colors.length);
		return getHueRotatedColors();
	}
	
	public static float[] hsvToRgb(float h,float s,float v){
		if("".equals("")){
			BigDecimal[] rgb=hsvToRgb(BigDecimal.valueOf(h),BigDecimal.valueOf(s),BigDecimal.valueOf(v));
			return new float[]{rgb[0].floatValue(),rgb[1].floatValue(),rgb[2].floatValue()};
		}
		float r = v;
		float g = v;
		float b = v;
		if (s > 0.0f) {
			h *= 6.0f;
			final int i = (int) h;
			final float f = h - (float) i;
			switch (i) {
				default:
				case 0:
					g *= 1 - s * (1 - f);
					b *= 1 - s;
					break;
				case 1:
					r *= 1 - s * f;
					b *= 1 - s;
					break;
				case 2:
					r *= 1 - s;
					b *= 1 - s * (1 - f);
					break;
				case 3:
					r *= 1 - s;
					g *= 1 - s * f;
					break;
				case 4:
					r *= 1 - s * (1 - f);
					g *= 1 - s;
					break;
				case 5:
					g *= 1 - s;
					b *= 1 - s * f;
					break;
			}
		}
		return new float[]{r,g,b};
	}

	public static BigDecimal[] hsvToRgb(BigDecimal h, BigDecimal s, BigDecimal v) {
		// https://ja.wikipedia.org/wiki/HSV%E8%89%B2%E7%A9%BA%E9%96%93?wprov=sfla1
		BigDecimal r = v;
		BigDecimal g = v;
		BigDecimal b = v;
		if(s.compareTo(BigDecimal.ZERO)>0){
			h=h.multiply(new BigDecimal("6"));
			final int i = h.intValue();
			final BigDecimal f = h.subtract(BigDecimal.valueOf(i));
			switch (i) {
				default:
				case 0:
				g = g.multiply(BigDecimal.ONE.subtract(s.multiply(BigDecimal.ONE.subtract(f))));
				b = b.multiply(BigDecimal.ONE.subtract(s));
				break;
				case 1:
				r = r.multiply(BigDecimal.ONE.subtract(s.multiply(f)));
				b = b.multiply(BigDecimal.ONE.subtract(s));
				break;
				case 2:
				r = r.multiply(BigDecimal.ONE.subtract(s));
				b = b.multiply(BigDecimal.ONE.subtract(s.multiply(BigDecimal.ONE.subtract(f))));
				break;
				case 3:
				r = r.multiply(BigDecimal.ONE.subtract(s));
				g = g.multiply(BigDecimal.ONE.subtract(s.multiply(f)));
				break;
				case 4:
				r = r.multiply(BigDecimal.ONE.subtract(s.multiply(BigDecimal.ONE.subtract(f))));
				g = g.multiply(BigDecimal.ONE.subtract(s));
				break;
				case 5:
				g = g.multiply(BigDecimal.ONE.subtract(s));
				b = b.multiply(BigDecimal.ONE.subtract(s.multiply(f)));
				break;
			}
		}
		return new BigDecimal[]{r,g,b};
	}
	
	public static int smallRgbTo32bitRgb(BigDecimal[] bds){
		int r=bds[0].multiply(BigDecimal.valueOf(255)).intValue();
		int g=bds[1].multiply(BigDecimal.valueOf(255)).intValue();
		int b=bds[2].multiply(BigDecimal.valueOf(255)).intValue();
		return Color.rgb(r,g,b);
	}
	
	public static int smallRgbTo32bitRgb(float[] bds){
		int r=(int)(bds[0]*255);
		int g=(int)(bds[1]*255);
		int b=(int)(bds[2]*255);
		return Color.rgb(r,g,b);
	}
	
	public static void describeForPermissionRequired(Activity a,String[] permissions,final PermissionRequest req,int reasonId){
		Resources res=a.getResources();
		PackageManager pm=a.getPackageManager();
		TypedArray ta=a.obtainStyledAttributes(new int[]{android.R.attr.textAppearanceSmall});
		int smallTxtAppr=ta.getResourceId(0,R.style.TextAppearance_AppCompat_Small);
		ta.recycle();
		SpannableStringBuilder ssb=new SpannableStringBuilder();
		ssb.append(res.getString(R.string.permissionsRequiredMessage).replace("[REASON]",res.getString(reasonId)));
		ssb.append('\n');
		for(String p:permissions){
			PermissionInfo pi;
			try {
				pi = pm.getPermissionInfo(p, PackageManager.GET_META_DATA);
			} catch (PackageManager.NameNotFoundException e) {
				continue;
			}
			SpannableStringBuilder ssb2=new SpannableStringBuilder();
			ssb2.append(pi.loadLabel(pm));
			ssb2.append('\n');
			ssb2.setSpan(new StyleSpan(Typeface.BOLD),0,ssb2.length()-1,SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
			
			SpannableStringBuilder ssb3=new SpannableStringBuilder();
			ssb3.append(pi.loadDescription(pm));
			ssb3.append('\n');
			ssb3.setSpan(new TextAppearanceSpan(a,smallTxtAppr),0,ssb3.length()-1,SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
			
			ssb.append(ssb2);
			ssb.append(ssb3);
			ssb.append('\n');
		}
		new AlertDialog.Builder(a)
			.setTitle(R.string.permissionsRequired)
			.setMessage(ssb)
			.setPositiveButton(R.string.continue_,new QuickDialogClickListener(){
				public void onClick(int w){
					req.proceed();
				}
			})
			.setNegativeButton(android.R.string.cancel,new QuickDialogClickListener(){
				public void onClick(int w){
					req.cancel();
				}
			})
			.setOnDismissListener(a1 -> req.cancel())
			.show();
	}
	
	public static void showPermissionError(Activity a,String[] permissions,int reasonId){
		Resources res=a.getResources();
		PackageManager pm=a.getPackageManager();
		StringBuilder sb=new StringBuilder();
		sb.append(res.getString(R.string.permissionsRequiredError).replace("[REASON]",res.getString(reasonId)));
		sb.append('\n');
		for(String p:permissions){
			PermissionInfo pi;
			try {
				pi = pm.getPermissionInfo(p, PackageManager.GET_META_DATA);
			} catch (PackageManager.NameNotFoundException e) {
				continue;
			}
			sb.append(pi.loadLabel(pm));
			sb.append('\n');
		}
		Toast.makeText(a,sb.toString(),Toast.LENGTH_LONG).show();
	}
	public static <T> Collection<T> iterableToCollection(Iterable<T> input){
		List<T> result=emptyList();
		for(T t:input){
			result.add(t);
		}
		return result;
	}
	public static void prepareLooper(){
		try{
			Looper.prepare();
		}catch(Throwable a){
			
		}
	}
	public static String getMimeType(String filePath){
		int index;
		if((index=filePath.lastIndexOf(".")) > 0){
			return MimeTypeMap.getSingleton().getMimeTypeFromExtension(filePath.substring(index + 1));
		}
		return null;
	}
	
	public static List<Server> jsonToServers(String json){
		WisecraftJsonObject ja=WJOUtils.parse(json);
		List<Server> servers=new ArrayList<>();
		for(WisecraftJsonObject entry:ja){
			if(!(entry.has("ip")&entry.has("port"))){
				continue;
			}
			Server s=new Server();
			if(entry.has("isPC")){
				// mode 19
				s.ip=entry.get("ip").getAsString();
				s.port=entry.get("port").getAsInt();
				s.mode=entry.get("isPC").getAsBoolean()?1:0;
			}else if(entry.has("mode")){
				// mode 35
				s.ip=entry.get("ip").getAsString();
				s.port=entry.get("port").getAsInt();
				s.mode=entry.get("mode").getAsInt();
				if(entry.has("name")){
					// current structure
					s.name=entry.get("name").getAsString();
				}
			}else{
				// so old!
				s.ip=entry.get("ip").getAsString();
				s.port=entry.get("port").getAsInt();
				s.mode=0;// forces PE to use
			}
			servers.add(s);
		}
		return servers;
	}
	
	public static CharSequence parseMinecraftDescriptionJson(WisecraftJsonObject description){
		if(!description.isJsonObject()){
			return parseMinecraftFormattingCode(description.getAsString());
		}
		if(description.has("extra")&&description.get("extra").isJsonArray()){
			SpannableStringBuilder ssb=new SpannableStringBuilder();
			Map<String,Integer> nameToColor=MinecraftFormattingCodeParser.NAME_TO_COLOR;
			for(WisecraftJsonObject part:description.get("extra")){
				if(part.isJsonObject()){
					// styled
					SpannableStringBuilder partSsb=new SpannableStringBuilder();
					String base=part.get("text").getAsString();
					int bend=base.length();
					partSsb.append(base);
					if(part.has("bold")&&part.get("bold").getAsBoolean()){
						partSsb.setSpan(new StyleSpan(Typeface.BOLD),0,bend,SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
					if(part.has("italic")&&part.get("italic").getAsBoolean()){
						partSsb.setSpan(new StyleSpan(Typeface.ITALIC),0,bend,SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
					if(part.has("strikethrough")&&part.get("strikethrough").getAsBoolean()){
						partSsb.setSpan(new StrikethroughSpan(),0,bend,SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
					if(part.has("underlined")&&part.get("underlined").getAsBoolean()){
						partSsb.setSpan(new UnderlineSpan(),0,bend,SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
					// ignore obfuscated: not supported on Android
					if(part.has("color")&&nameToColor.containsKey(part.get("color").getAsString())){
						partSsb.setSpan(new ForegroundColorSpan(nameToColor.get(part.get("color").getAsString())),0,bend,SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
					ssb.append(partSsb);
				}else{
					// non-styled
					ssb.append(parseMinecraftFormattingCode(part.getAsString()));
				}
			}
			return ssb;
		}else{
			return parseMinecraftFormattingCode(description.get("text").getAsString());
		}
	}
}
