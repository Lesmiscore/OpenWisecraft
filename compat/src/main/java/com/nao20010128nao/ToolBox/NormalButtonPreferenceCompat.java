/*
 * Decompiled with CFR 0_108.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.preference.Preference
 *  android.util.AttributeSet
 */
package com.nao20010128nao.ToolBox;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;

public abstract class NormalButtonPreferenceCompat
extends Preference {
    public NormalButtonPreferenceCompat(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    protected abstract void onClick();
}

