package com.nao20010128nao.Wisecraft.misc.tfl;
import android.graphics.*;

class ConstantTypefaceLoader extends TypefaceLoader
{
	Typeface tf;
	public ConstantTypefaceLoader(Typeface tf){
		this.tf=tf;
	}

	@Override
	public boolean isLoaded() {
		return true;
	}

	@Override
	public Typeface load() {
		return tf;
	}
}
