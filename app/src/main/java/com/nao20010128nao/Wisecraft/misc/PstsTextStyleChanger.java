package com.nao20010128nao.Wisecraft.misc;

import android.support.v4.view.*;
import android.view.*;
import android.widget.*;
import com.astuetz.*;

import java.util.*;

public class PstsTextStyleChanger implements ViewPager.OnPageChangeListener {
    int selected, unselected;
    ViewPager pager;
    PagerSlidingTabStrip pagerSlider;

    public PstsTextStyleChanger(int selected, int unselected, ViewPager vp, PagerSlidingTabStrip psts) {
        this.selected = selected;
        this.unselected = unselected;
        pager = vp;
        pagerSlider = psts;
    }

    public void onPageSelected(int pos) {
        int[] styles = new int[pager.getAdapter().getCount()];
        Arrays.fill(styles, unselected);
        styles[pos] = selected;
        for (int i = 0; i < styles.length; i++) {
            TextView v = (TextView) ((ViewGroup) pagerSlider.getChildAt(0)).getChildAt(i);
            v.setTypeface(v.getTypeface(), styles[i]);
        }
    }

    @Override
    public void onPageScrollStateChanged(int p1) {
    }

    @Override
    public void onPageScrolled(int p1, float p2, int p3) {
    }
}
