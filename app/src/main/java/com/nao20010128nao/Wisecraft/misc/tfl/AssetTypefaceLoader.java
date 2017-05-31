package com.nao20010128nao.Wisecraft.misc.tfl;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import java.lang.ref.*;

class AssetTypefaceLoader extends TypefaceLoader
{
	String filename;
	AssetManager am;
	WeakReference<Typeface> ref= new WeakReference<>(null);
	public AssetTypefaceLoader(String filename,AssetManager am){
		this.filename=filename;
		this.am=am;
	}
	public AssetTypefaceLoader(String filename,Context ctx){
		this(filename,ctx.getAssets());
	}

	@Override
	public boolean isLoaded() {
		return ref.get()!=null;
	}

	@Override
	public Typeface load() {
		if(!isLoaded()){
			ref= new WeakReference<>(Typeface.createFromAsset(am, filename));
		}
		return ref.get();
	}
}
