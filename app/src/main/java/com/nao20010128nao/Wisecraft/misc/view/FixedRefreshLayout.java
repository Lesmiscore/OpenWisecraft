package com.nao20010128nao.Wisecraft.misc.view;

import android.content.*;
import android.os.*;
import android.support.v4.widget.*;
import android.util.*;

public class FixedRefreshLayout extends SwipeRefreshLayout {

    private static final String TAG = "RefreshTag";
    private boolean selfCancelled = false;

    public FixedRefreshLayout(Context context) {
        super(context);
    }

    public FixedRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        if (isRefreshing()) {
            clearAnimation();
            setRefreshing(false);
            selfCancelled = true;
            Log.d(TAG, "For hide refreshing");
        }
        return super.onSaveInstanceState();
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        super.setRefreshing(refreshing);
        selfCancelled = false;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus && selfCancelled) {
            setRefreshing(true);
            Log.d(TAG, "Force show refreshing");
        }
    }
}
