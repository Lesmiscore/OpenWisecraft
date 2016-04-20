package com.nao20010128nao.Wisecraft.misc.contextwrappers.colored_text;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.xmlpull.v1.XmlPullParser;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import android.widget.TextView;

/**
 * Created by chris on 19/12/2013
 * Project: Calligraphy
 */
class CalligraphyLayoutInflater extends LayoutInflater {

    private static final String[] sClassPrefixList = {
		"android.widget.",
		"android.webkit."
    };
    // Reflection Hax
    private boolean mSetPrivateFactory = false;
    private Field mConstructorArgs = null;
	private int textColor;

    protected CalligraphyLayoutInflater(Context context, int attributeId, int textColor) {
        super(context);
		this.textColor=textColor;
    }

    protected CalligraphyLayoutInflater(LayoutInflater original, Context newContext, int attributeId, final boolean cloned, int textColor) {
        super(original, newContext);
		this.textColor=textColor;
    }

    @Override
    public LayoutInflater cloneInContext(Context newContext) {
        return new CalligraphyLayoutInflater(this, newContext, 0, true, textColor);
    }

    // ===
    // Wrapping goodies
    // ===


    @Override
    public View inflate(XmlPullParser parser, ViewGroup root, boolean attachToRoot) {
        View v= super.inflate(parser, root, attachToRoot);
		processDeeply(v);
		return v;
    }
	
	private void processDeeply(View v){
		if(v==null){
			return;
		}else{
			ReflectionUtils.invokeMethod(v,ReflectionUtils.getMethod(TextView.class,"setTextColor"),textColor);
			if(v instanceof ViewGroup){
				ViewGroup vg=(ViewGroup)v;
				for(int i=0;i<vg.getChildCount();i++){
					processDeeply(vg.getChildAt(i));
				}
			}
		}
	}
}

