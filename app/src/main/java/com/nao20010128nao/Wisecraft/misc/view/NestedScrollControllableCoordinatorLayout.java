package com.nao20010128nao.Wisecraft.misc.view;

import android.content.*;
import android.support.design.widget.*;
import android.util.*;

public class NestedScrollControllableCoordinatorLayout extends CoordinatorLayout 
{
	private boolean nestedScrollEnabled = true;
	
	public NestedScrollControllableCoordinatorLayout(Context context) {
		super(context);
	}

    public NestedScrollControllableCoordinatorLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

    public NestedScrollControllableCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	public void setNestedScrollEnabled(boolean allowUserDragging) {
        nestedScrollEnabled = allowUserDragging;
    }

	public boolean getNestedScrollEnabled(){
		return nestedScrollEnabled;
	}

	@Override
	public boolean isNestedScrollingEnabled() {
		return super.isNestedScrollingEnabled()&nestedScrollEnabled;
	}

	@Override
	public boolean startNestedScroll(int axes) {
		if(!nestedScrollEnabled){
			return false;
		}
		return super.startNestedScroll(axes);
	}

	@Override
	public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
		if(!nestedScrollEnabled){
			return false;
		}
		return super.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
	}

	@Override
	public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
		if(!nestedScrollEnabled){
			return false;
		}
		return super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
	}

	@Override
	public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
		if(!nestedScrollEnabled){
			return false;
		}
		return super.dispatchNestedFling(velocityX, velocityY, consumed);
	}

	@Override
	public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
		if(!nestedScrollEnabled){
			return false;
		}
		return super.dispatchNestedPreFling(velocityX, velocityY);
	}
}
