package com.nao20010128nao.Wisecraft.misc;
import com.nao20010128nao.Wisecraft.misc.compat.AppCompatListActivity;
import android.os.Bundle;
import android.support.v4.widget.ViewDragHelper;
import android.view.View;
import android.support.v7.widget.LinearLayoutCompat;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class DraggingView extends LinearLayoutCompat{
	ViewDragHelper vdh;
	int ofs,tofs;
    float initialMotionY;
    int top;
    int dragRange;
    float dragOffset;
	public DraggingView(android.content.Context context) {
		super(context);
	}
	public DraggingView(android.content.Context context, android.util.AttributeSet attrs) {
		super(context,attrs);
	}
	public DraggingView(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
		super(context,attrs,defStyleAttr);
	}
	
	private void setup(Context ctx){
		ServerListActivityBase3 act;
		if(ctx instanceof ServerListActivityBase3){
			act=(ServerListActivityBase3)ctx;
		}else{
			Log.d("ServerListActivity","DraggingView was inflated out of ServerListActivity! Is this correct?");
			return;
		}
		vdh=act.vdh=vdh=ViewDragHelper.create(this,new DraggingCallback());
	}

	public void setUnmoveableViewOffset(int value){
		ofs=value;
	}
	public void setMoveableViewOffset(int value){
		tofs=value;
	}

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setup(getContext());
    }

    @Override
    public void computeScroll() {
        if (vdh.continueSettling(true)) {
            postInvalidateOnAnimation();
        }
    }

    public void smoothSlideTo(float offset) {
        final int topBound = getPaddingTop();
        float y = topBound + offset * this.dragRange;
        if (vdh.smoothSlideViewTo(getChildAt(ofs), getChildAt(ofs).getLeft(), (int) y)) {
            postInvalidateOnAnimation();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        final int action = event.getActionMasked();

        if (action != MotionEvent.ACTION_DOWN) {
            vdh.cancel();
            return super.onInterceptTouchEvent(event);
        }

        final float x = event.getX();
        final float y = event.getY();
        boolean isHeaderViewUnder = false;

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                this.initialMotionY = y;
                isHeaderViewUnder = vdh.isViewUnder(getChildAt(ofs), (int) x, (int) y);
                break;
            }
        }

        return vdh.shouldInterceptTouchEvent(event) || isHeaderViewUnder;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        vdh.processTouchEvent(event);

        final int action = event.getActionMasked();
        final float x = event.getX();
        final float y = event.getY();

        boolean isHeaderViewUnder = vdh.isViewUnder(getChildAt(ofs), (int) x, (int) y);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                this.initialMotionY = y;
                break;
            }

            case MotionEvent.ACTION_UP: {
                if (isHeaderViewUnder) {
                    final float dy = y - this.initialMotionY;
                    final int slop = vdh.getTouchSlop();
                    if (Math.abs(dy) < Math.abs(slop)) {
                        if (this.dragOffset == 0) {
                            smoothSlideTo(1f);
                        } else {
                            smoothSlideTo(0f);
                        }
                    } else {
                        float headerViewCenterY = getChildAt(ofs).getY() + getChildAt(ofs).getHeight() / 2;
                        
                        if (headerViewCenterY >= getHeight() / 2) {
                            smoothSlideTo(1f);
                        } else {
                            smoothSlideTo(0f);
                        }
                    }
                }
                break;
            }
        }

        return isHeaderViewUnder && isViewHit(getChildAt(ofs), (int) y) || isViewHit(getChildAt(tofs), (int) y);
    }

    private boolean isViewHit(View view, int y) {
        int[] parentLocation = new int[2];
        this.getLocationOnScreen(parentLocation);
        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);
        int screenY = parentLocation[1] + y;
        return screenY >= viewLocation[1] && screenY < viewLocation[1] + view.getHeight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, 0),
                resolveSizeAndState(maxHeight, heightMeasureSpec, 0));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        this.dragRange = getHeight() - getChildAt(ofs).getHeight();
        getChildAt(ofs).layout(0, this.top, r, this.top + getChildAt(ofs).getMeasuredHeight());
        getChildAt(tofs).layout(0, this.top + getChildAt(ofs).getMeasuredHeight(), r, this.top + b);
    }
    
	class DraggingCallback extends ViewDragHelper.Callback {
		@Override
		public boolean tryCaptureView(View p1, int p2) {
			// TODO: Implement this method
			return p1!=getChildAt(ofs);
		}
	}
}
