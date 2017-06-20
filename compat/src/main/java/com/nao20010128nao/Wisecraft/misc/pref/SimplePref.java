package com.nao20010128nao.Wisecraft.misc.pref;

import android.content.*;
import android.support.v7.preference.*;
import android.util.*;

public class SimplePref extends Preference {
    public SimplePref(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SimplePref(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SimplePref(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SimplePref(Context context) {
        super(context);
    }

    public SimplePref(Context context, CharSequence title) {
        this(context);
        setTitle(title);
    }

    public SimplePref(Context context, CharSequence title, CharSequence summary) {
        this(context, title);
        setSummary(summary);
    }
}
