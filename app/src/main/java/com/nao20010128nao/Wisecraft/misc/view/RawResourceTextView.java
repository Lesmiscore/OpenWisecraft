package com.nao20010128nao.Wisecraft.misc.view;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.os.*;
import android.support.v7.widget.*;
import android.util.*;
import com.nao20010128nao.Wisecraft.*;
import java.io.*;

public class RawResourceTextView extends AppCompatTextView
{
	public RawResourceTextView(android.content.Context context) {
		super(context);
	}

    public RawResourceTextView(android.content.Context context, AttributeSet attrs) {
		super(context,attrs);
		loadAttrs(context,attrs);
	}

    public RawResourceTextView(android.content.Context context, AttributeSet attrs, int defStyleAttr) {
		super(context,attrs,defStyleAttr);
		loadAttrs(context,attrs);
	}
	
	private void loadAttrs(final Context context,AttributeSet attrs){
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RawResourceTextView);
		final String rawRes = array.getString(R.styleable.RawResourceTextView_rawRes);
		final boolean async=array.getBoolean(R.styleable.RawResourceTextView_async,false);
		
		Thread t=new Thread(){
			public void run(){
				final int rawResId;
				try {
					rawResId = (int)R.raw.class.getField(rawRes).get(null);
				} catch (Throwable e) {
					WisecraftError.report("RawResourceTextView",e);
					return;
				}
				final String txt=readAllData(context.getResources().openRawResource(rawResId));
				post(new Runnable(){
					public void run(){
						setText(txt);
					}
				});
			}
		};
		if(async)t.start();else t.run();
		array.recycle();
	}
	private String readAllData(InputStream is){
		InputStreamReader isr=null;
		StringWriter sw=new StringWriter();
		char[] buf=new char[4096];
		try {
			isr = new InputStreamReader(is);
			while (true) {
				int r=isr.read(buf);
				if (r <= 0) {
					return sw.toString();
				}
				sw.write(buf, 0, r);
			}
		} catch (IOException e) {
			return "";
		}
	}

	@Override
	public void setTypeface(Typeface tf) {
		// TODO: Implement this method
		super.setTypeface(tf.MONOSPACE);
	}
}
