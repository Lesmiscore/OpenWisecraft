/*
 * Decompiled with CFR 0_108.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.preference.Preference
 *  android.util.AttributeSet
 */
package com.nao20010128nao.ToolBox;

import android.support.v7.preference.*;

public abstract class NormalButtonPreferenceCompat
extends Preference {
    public NormalButtonPreferenceCompat(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context,attrs,defStyleAttr,defStyleRes);
    }

    public NormalButtonPreferenceCompat(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
        super(context,attrs,defStyleAttr);
    }

    public NormalButtonPreferenceCompat(android.content.Context context, android.util.AttributeSet attrs) {
        super(context,attrs);
    }

    public NormalButtonPreferenceCompat(android.content.Context context) {
        super(context);
    }

    protected abstract void onClick();
}

