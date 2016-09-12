package com.nao20010128nao.Wisecraft.misc;

import android.content.*;
import android.support.v7.widget.*;
import android.util.*;

public class HPLinearLayoutManager extends LinearLayoutManager {

    public HPLinearLayoutManager(Context context) {
        super(context);
    }

    public HPLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public HPLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Magic here
     */
    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }
}
