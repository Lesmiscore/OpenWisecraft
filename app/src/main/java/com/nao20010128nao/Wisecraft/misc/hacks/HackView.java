package com.nao20010128nao.Wisecraft.misc.hacks;
import android.content.*;
import android.util.*;
import android.view.*;
import com.nao20010128nao.Wisecraft.misc.pref.*;

public class HackView extends View
{
	public HackView(Context context) {
        super(context);
    }

    public HackView(Context context, AttributeSet attrs) {
        super(context, StartPref.as=attrs);
    }

    public HackView(Context context, AttributeSet attrs, int defStyle) {
        super(context, StartPref.as=attrs, defStyle);
    }
}
