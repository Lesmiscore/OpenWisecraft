package com.nao20010128nao.Wisecraft.misc.view;

import android.content.*;
import android.support.v4.view.*;
import android.util.*;
import android.view.*;


/*
 http://stackoverflow.com/questions/13346824/viewpager-detect-when-user-is-trying-to-swipe-out-of-bounds
*/
public class OverScrollViewPager extends ViewPager {

    float mStartDragX;
    OnSwipeOutListener mListener;


    public OverScrollViewPager(Context ctx) {
        super(ctx);
    }

    public OverScrollViewPager(Context ctx, AttributeSet as) {
        super(ctx, as);
    }

    public void setOnSwipeOutListener(OnSwipeOutListener listener) {
        mListener = listener;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartDragX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mStartDragX < x && getCurrentItem() == 0) {
                    if (mListener != null) mListener.onSwipeOutAtStart();
                } else if (mStartDragX > x && getCurrentItem() == getAdapter().getCount() - 1) {
                    if (mListener != null) mListener.onSwipeOutAtEnd();
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public interface OnSwipeOutListener {
        void onSwipeOutAtStart();

        void onSwipeOutAtEnd();
    }

}
