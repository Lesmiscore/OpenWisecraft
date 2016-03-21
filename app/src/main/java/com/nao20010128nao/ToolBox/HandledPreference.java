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
    OnClickListener clicklis;

    public HandledPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.clicklis = new nullocl((HandledPreference)this, null);
    }

    public static OnClickListener createListenerFrom(final View.OnClickListener onClickListener) {
        OnClickListener onClickListener2 = new OnClickListener(){

            @Override
            public void onClick(String string, String string2, String string3) {
                View view = new View(null);
                onClickListener.onClick(view);
            }
        };
        return onClickListener2;
    }

    private String getKeySafety() {
        try {
            String string = this.getKey();
            return string;
        } catch (Throwable var1_2) {
            var1_2.printStackTrace();
            return null;
        }
    }

    private String getSummarySafety() {
        try {
            String string = this.getSummary().toString();
            return string;
        } catch (Throwable var1_2) {
            var1_2.printStackTrace();
            return null;
        }
    }

    private String getTitleSafety() {
        try {
            String string = this.getTitle().toString();
            return string;
        } catch (Throwable var1_2) {
            var1_2.printStackTrace();
            return null;
        }
    }

    public OnClickListener getOnClickListener() {
        return this.clicklis;
    }

    @Override
    protected void onClick() {
        this.clicklis.onClick(this.getKeySafety(), this.getTitleSafety(), this.getSummarySafety());
    }

    /*
     * Enabled aggressive block sorting
     */
    public void setOnClickListener(OnClickListener onClickListener) {
        OnClickListener onClickListener2 = onClickListener == null ? this.clicklis : onClickListener;
        this.clicklis = onClickListener2;
    }

    public static interface OnClickListener {
        public void onClick(String var1, String var2, String var3);
    }

    private class nullocl
    implements OnClickListener {
        final /* synthetic */ HandledPreference this0;

        private nullocl(HandledPreference handledPreference) {
            this.this0 = handledPreference;
        }

        /* synthetic */ nullocl(HandledPreference handledPreference, nullocl nullocl) {
            this(handledPreference);
        }

        @Override
        public void onClick(String string, String string2, String string3) {
        }
    }

}


