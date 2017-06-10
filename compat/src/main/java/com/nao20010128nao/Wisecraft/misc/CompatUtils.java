package com.nao20010128nao.Wisecraft.misc;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.net.*;
import android.preference.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;

import java.io.*;
import java.lang.reflect.*;
import java.security.*;
import java.util.*;

import android.support.v7.widget.Toolbar;

public class CompatUtils {
	public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }
	public static int hash(Object... values) {
		return hashCode(values);
	}

	public static int hashCode(Object o) {
		return (o == null) ? 0 : o.hashCode();
	}
	
	public static int hashCode(boolean[] array) {
        if (array == null) {
            return 0;
        }
        int hashCode = 1;
        for (boolean element : array) {
            // 1231, 1237 are hash code values for boolean value
            hashCode = 31 * hashCode + (element ? 1231 : 1237);
        }
        return hashCode;
    }
	
    public static int hashCode(int[] array) {
        if (array == null) {
            return 0;
        }
        int hashCode = 1;
        for (int element : array) {
            // the hash code value for integer value is integer value itself
            hashCode = 31 * hashCode + element;
        }
        return hashCode;
    }
	
    public static int hashCode(short[] array) {
        if (array == null) {
            return 0;
        }
        int hashCode = 1;
        for (short element : array) {
            // the hash code value for short value is its integer value
            hashCode = 31 * hashCode + element;
        }
        return hashCode;
    }
	
    public static int hashCode(char[] array) {
        if (array == null) {
            return 0;
        }
        int hashCode = 1;
        for (char element : array) {
            // the hash code value for char value is its integer value
            hashCode = 31 * hashCode + element;
        }
        return hashCode;
    }

    public static int hashCode(byte[] array) {
        if (array == null) {
            return 0;
        }
        int hashCode = 1;
        for (byte element : array) {
            // the hash code value for byte value is its integer value
            hashCode = 31 * hashCode + element;
        }
        return hashCode;
    }

    public static int hashCode(long[] array) {
        if (array == null) {
            return 0;
        }
        int hashCode = 1;
        for (long elementValue : array) {
            /*
             * the hash code value for long value is (int) (value ^ (value >>>
             * 32))
             */
            hashCode = 31 * hashCode
				+ (int) (elementValue ^ (elementValue >>> 32));
        }
        return hashCode;
    }

    public static int hashCode(float[] array) {
        if (array == null) {
            return 0;
        }
        int hashCode = 1;
        for (float element : array) {
            /*
             * the hash code value for float value is
             * Float.floatToIntBits(value)
             */
            hashCode = 31 * hashCode + Float.floatToIntBits(element);
        }
        return hashCode;
    }

    public static int hashCode(double[] array) {
        if (array == null) {
            return 0;
        }
        int hashCode = 1;

        for (double element : array) {
            long v = Double.doubleToLongBits(element);
            /*
             * the hash code value for double value is (int) (v ^ (v >>> 32))
             * where v = Double.doubleToLongBits(value)
             */
            hashCode = 31 * hashCode + (int) (v ^ (v >>> 32));
        }
        return hashCode;
    }

    public static int hashCode(Object[] array) {
        if (array == null) {
            return 0;
        }
        int hashCode = 1;
        for (Object element : array) {
            int elementHashCode=hashCode(element);
            hashCode = 31 * hashCode + elementHashCode;
        }
        return hashCode;
    }
	public static void applyHandlersForViewTree(View v,View.OnClickListener click,View.OnLongClickListener longer){
		if(v!=null){
			v.setOnClickListener(click);
			v.setOnLongClickListener(longer);
			v.setLongClickable(longer!=null);
			if(v instanceof ViewGroup){
				ViewGroup vg=(ViewGroup)v;
				for(int i=0;i<vg.getChildCount();i++){
					applyHandlersForViewTree(vg.getChildAt(i),click,longer);
				}
			}
		}
	}
	public static void applyHandlersForViewTree(View v,View.OnClickListener click){
		if(v!=null){
			v.setOnClickListener(click);
			if(v instanceof ViewGroup){
				ViewGroup vg=(ViewGroup)v;
				for(int i=0;i<vg.getChildCount();i++){
					applyHandlersForViewTree(vg.getChildAt(i),click);
				}
			}
		}
	}
	public static void applyHandlersForViewTree(View v,View.OnLongClickListener longer){
		if(v!=null){
			v.setOnLongClickListener(longer);
			v.setLongClickable(longer!=null);
			if(v instanceof ViewGroup){
				ViewGroup vg=(ViewGroup)v;
				for(int i=0;i<vg.getChildCount();i++){
					applyHandlersForViewTree(vg.getChildAt(i),longer);
				}
			}
		}
	}
	public static void travelViewTree(View v,Treatment<View> a){
		if(v!=null){
			a.process(v);
			if(v instanceof ViewGroup){
				ViewGroup vg=(ViewGroup)v;
				for(int i=0;i<vg.getChildCount();i++){
					travelViewTree(vg.getChildAt(i),a);
				}
			}
		}
	}
	public static boolean isNullString(String s) {
		return TextUtils.isEmpty(s);
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
	public static String[] lines(String s) {
		try {
			BufferedReader br=new BufferedReader(new StringReader(s));
			List<String> tmp=new ArrayList<>(4);
			String line=null;
			while (null != (line = br.readLine()))tmp.add(line);
			return tmp.toArray(new String[tmp.size()]);
		} catch (Throwable e) {
			return new String[0];
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
			safeClose(fos);
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
			safeClose(fis);
		}
	}
	public static Context wrapContextForPreference(Context c){
		final TypedValue tv = new TypedValue();
		c.getTheme().resolveAttribute(R.attr.preferenceTheme, tv, true);
		final int theme = tv.resourceId;
		if (theme <= 0) {
			throw new IllegalStateException("Must specify preferenceTheme in theme");
		}
		return new ContextThemeWrapper(c, theme);
	}
	public static String toHex(byte[] buf) {
		StringBuilder sb=new StringBuilder();
		for (byte b:buf)
			sb.append(Character.forDigit(b >> 4 & 0xF, 16)).append(Character.forDigit(b & 0xF, 16));
		return sb.toString();
	}
	public static <T> T requireNonNull(T obj) {
		if (obj == null)
			throw new NullPointerException();
		return obj;
	}
	public static boolean isOnline(Context ctx){
		ConnectivityManager cm=(ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
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
	public static SharedPreferences getPreferences(Context c){
		return PreferenceManager.getDefaultSharedPreferences(c);
	}

	public static void safeClose(Object c){
		if(c==null)return;
		if(c instanceof Closeable){
			try {
				((Closeable)c).close();
			} catch (IOException e) {}
		}else{
			Method close;
			try {
				close=c.getClass().getMethod("close");
			} catch (Throwable e) {
				try {
					close=c.getClass().getMethod("dispose");
				} catch (Throwable e1) {
					return;
				}
			}
			try {
				close.invoke(c);
			} catch (Throwable e) {}
		}
	}
	public static void safeClose(Object... cs){
		if(cs==null)return;
		for(Object c:cs)safeClose(c);
	}
	public static void safeClose(Closeable[] cs){
		if(cs==null)return;
		for(Object c:cs)safeClose(c);
	}
}
