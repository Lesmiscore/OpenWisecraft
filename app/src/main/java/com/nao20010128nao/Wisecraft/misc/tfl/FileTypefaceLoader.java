package com.nao20010128nao.Wisecraft.misc.tfl;

import android.content.*;
import android.graphics.*;

import java.io.*;
import java.lang.ref.*;

class FileTypefaceLoader extends TypefaceLoader
{
	File file;
	WeakReference<Typeface> ref= new WeakReference<>(null);
	public FileTypefaceLoader(File f){
		file=f;
	}
	public FileTypefaceLoader(String s){
		this(new File(s));
	}
	public FileTypefaceLoader(Context c,String s){
		this(new File(c.getFilesDir(),s));
	}

	@Override
	public boolean isLoaded() {
		return ref.get()!=null;
	}

	@Override
	public Typeface load() {
		if(!isLoaded()){
			ref= new WeakReference<>(Typeface.createFromFile(file));
		}
		return ref.get();
	}
}
