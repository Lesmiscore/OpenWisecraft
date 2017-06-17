package com.nao20010128nao.Wisecraft.misc;

import android.content.*;
import android.support.design.widget.*;
import android.util.*;
import android.view.*;
import biz.laenger.android.vpbs.*;

public class LockableViewPagerBottomSheetBehavior<V extends View> extends ViewPagerBottomSheetBehavior<V> {
    private boolean mAllowUserDragging = true;

    /**
     * Default constructor for instantiating BottomSheetBehaviors.
     */
    public LockableViewPagerBottomSheetBehavior() {
        super();
    }

    /**
     * Default constructor for inflating BottomSheetBehaviors from layout.
     *
     * @param context The {@link Context}.
     * @param attrs   The {@link AttributeSet}.
     */
    public LockableViewPagerBottomSheetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAllowUserDragging(boolean allowUserDragging) {
        mAllowUserDragging = allowUserDragging;
    }

    public boolean getAllowUserDragging() {
        return mAllowUserDragging;
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        if (!mAllowUserDragging) {
            return false;
        }
        return super.onInterceptTouchEvent(parent, child, event);
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        if (!mAllowUserDragging) {
            return false;
        }
        return super.onTouchEvent(parent, child, event);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, V child, View directTargetChild, View target, int nestedScrollAxes) {
        if (!mAllowUserDragging) {
            return false;
        }
        return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }
}
