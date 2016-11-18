package com.nao20010128nao.Wisecraft.misc.tfl;

import android.content.*;
import android.content.res.*;
import android.graphics.*;
import java.io.*;

public abstract class TypefaceLoader {
	public abstract boolean isLoaded();
	public abstract Typeface load();
	
	
	public static TypefaceLoader newInstance(String filename,AssetManager am){
		return new AssetTypefaceLoader(filename,am);
	}
	public static TypefaceLoader newInstance(String filename,Context ctx){
		return new AssetTypefaceLoader(filename,ctx);
	}
	public static TypefaceLoader newInstance(Typeface tf){
		return new ConstantTypefaceLoader(tf);
	}
	public static TypefaceLoader newInstance(File f){
		return new FileTypefaceLoader(f);
	}
	public static TypefaceLoader newInstance(String s){
		return new FileTypefaceLoader(s);
	}
	public static TypefaceLoader newInstance(Context c,String s){
		return new FileTypefaceLoader(c,s);
	}
}
