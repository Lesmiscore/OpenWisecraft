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

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.nao20010128nao.ToolBox.NormalButtonPreference;

public class HandledPreference
extends NormalButtonPreference {
	static final NullClickListener defHandler=new NullClickListener();
    OnClickListener clickListener=defHandler;

    public HandledPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
		try {
			Class.forName("com.nao20010128nao.Wisecraft.misc.pref.StartPref").getField("as").set(null,attributeSet);
		} catch (Throwable e) {
			
		}
    }

    public static OnClickListener createListenerFrom(final View.OnClickListener onClickListener) {
        return new OnClickListener(){
            @Override
            public void onClick(String string, String string2, String string3) {
                onClickListener.onClick(null);
            }
        };
    }

    private String getKeySafety() {
        try {
            return getKey();
        } catch (Throwable var1_2) {
            //var1_2.printStackTrace();
            return null;
        }
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

    public OnClickListener getOnClickListener() {
        return clickListener;
    }

    @Override
    protected void onClick() {
        clickListener.onClick(getKeySafety(), getTitleSafety(), getSummarySafety());
    }

    /*
     * Enabled aggressive block sorting
     */
    public void setOnClickListener(OnClickListener onClickListener) {
        clickListener = onClickListener == null ? defHandler : onClickListener;
    }

    public static interface OnClickListener {
        public void onClick(String var1, String var2, String var3);
    }

    private static class NullClickListener implements OnClickListener {
        @Override
        public void onClick(String string, String string2, String string3) {
        }
    }
}


