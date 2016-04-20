package com.nao20010128nao.Wisecraft.misc.contextwrappers.colored_text;
import android.content.ContextWrapper;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.xmlpull.v1.XmlPullParser;
import android.os.Build;
import android.util.AttributeSet;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ColoredTextViewsContextWrapper extends ContextWrapper
{
	CalligraphyFactory mCalligraphyFactory;
	
	int textColor;
	public static ColoredTextViewsContextWrapper wrap(Context ctz,int color){
		return new ColoredTextViewsContextWrapper(ctz,color);
	}
	private ColoredTextViewsContextWrapper(Context ctz,int color){
		super(ctz);
		textColor=color;
	}

	@Override
	public Object getSystemService(String name) {
		// TODO: Implement this method
		if(LAYOUT_INFLATER_SERVICE.equals(name)){
			return new CalligraphyLayoutInflater(this,0,textColor);
		}
		return super.getSystemService(name);
	}
}
