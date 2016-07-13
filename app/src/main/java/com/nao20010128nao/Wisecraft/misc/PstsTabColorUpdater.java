package com.nao20010128nao.Wisecraft.misc;
import android.support.v4.view.ViewPager;
import com.astuetz.PagerSlidingTabStrip;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.Arrays;

public class PstsTabColorUpdater implements ViewPager.OnPageChangeListener{
	int selected,unselected;
	ViewPager pager;
	PagerSlidingTabStrip pagerSlider;
	public PstsTabColorUpdater(int selected,int unselected,ViewPager vp,PagerSlidingTabStrip psts){
		this.selected=selected;
		this.unselected=unselected;
		pager=vp;
		pagerSlider=psts;
	}

	public void onPageSelected(int pos){
		int[] colors=new int[pager.getAdapter().getCount()];
		Arrays.fill(colors,unselected);
		colors[pos]=selected;
		for(int i=0;i<colors.length;i++){
			((TextView)((ViewGroup)pagerSlider.getChildAt(0)).getChildAt(i)).setTextColor(colors[i]);
		}
	}

	@Override
	public void onPageScrollStateChanged(int p1) {
		// TODO: Implement this method
	}

	@Override
	public void onPageScrolled(int p1, float p2, int p3) {
		// TODO: Implement this method
	}
}
