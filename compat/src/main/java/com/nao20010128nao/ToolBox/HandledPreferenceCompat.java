/*
 * Decompiled with CFR 0_108.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.util.AttributeSet
 *  android.view.View
 *  android.view.View$OnClickListener
 *  java.lang.CharSequence
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Throwable
 */
package com.nao20010128nao.ToolBox;

import android.view.*;
import com.annimon.stream.function.*;

public class HandledPreferenceCompat
        extends NormalButtonPreferenceCompat {
    static final NullClickListener defHandler = new NullClickListener();
    HandledPreference.OnClickListener clickListener = defHandler;

    public HandledPreferenceCompat(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public HandledPreferenceCompat(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public HandledPreferenceCompat(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    public HandledPreferenceCompat(android.content.Context context) {
        super(context);
    }

    public static OnClickListener createListenerFrom(final View.OnClickListener onClickListener) {
        return (string, string2, string3) -> onClickListener.onClick(null);
    }

    private String getKeySafety() {
        return getKey();
    }

    private String getSummarySafety() {
        if (getSummary() == null) {
            return null;
        } else {
            return getSummary().toString();
        }
    }

    private String getTitleSafety() {
        if (getTitle() == null) {
            return null;
        } else {
            return getTitle().toString();
        }
    }

    public HandledPreference.OnClickListener getOnClickListener() {
        return clickListener;
    }

    @Override
    protected void onClick() {
        clickListener.onClick(getKeySafety(), getTitleSafety(), getSummarySafety());
    }

    public HandledPreferenceCompat setOnClickListener(OnClickListener onClickListener) {
        clickListener = onClickListener == null ? defHandler : onClickListener;
        return this;
    }

    public HandledPreferenceCompat setOnClickListener(HandledPreference.OnClickListener onClickListener) {
        clickListener = onClickListener == null ? defHandler : onClickListener;
        return this;
    }

    public HandledPreferenceCompat onClick(OnClickListener onClickListener) {
        clickListener = onClickListener == null ? defHandler : onClickListener;
        return this;
    }
    
    public HandledPreferenceCompat onClick(Consumer<Void> onClickListener) {
        return onClick((a,b,c) -> onClickListener.accept(null));
    }

    public HandledPreferenceCompat title(CharSequence text) {
        setTitle(text);
        return this;
    }
    
    public HandledPreferenceCompat summary(CharSequence text) {
        setSummary(text);
        return this;
    }
    
    public HandledPreferenceCompat key(String text) {
        setKey(text);
        return this;
    }

    public interface OnClickListener extends HandledPreference.OnClickListener {
        void onClick(String key, String title, String summary);
    }

    private static class NullClickListener implements OnClickListener {
        @Override
        public void onClick(String string, String string2, String string3) {
        }
    }
}


