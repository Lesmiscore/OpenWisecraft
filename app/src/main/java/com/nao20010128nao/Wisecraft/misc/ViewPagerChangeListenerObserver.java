package com.nao20010128nao.Wisecraft.misc;

import android.support.v4.view.*;
import java.util.*;

public class ViewPagerChangeListenerObserver implements ViewPager.OnPageChangeListener {
	List<ViewPager.OnPageChangeListener> listeners;
	public ViewPagerChangeListenerObserver(ViewPager.OnPageChangeListener... listeners) {
		this.listeners=flatten(Arrays.asList(listeners));
	}

	public void onPageSelected(int pos) {
		for(ViewPager.OnPageChangeListener pcl:listeners){
			pcl.onPageSelected(pos);
		}
	}

	@Override
	public void onPageScrollStateChanged(int p1) {
		for(ViewPager.OnPageChangeListener pcl:listeners){
			pcl.onPageScrollStateChanged(p1);
		}
	}

	@Override
	public void onPageScrolled(int p1, float p2, int p3) {
		for(ViewPager.OnPageChangeListener pcl:listeners){
			pcl.onPageScrolled(p1,p2,p3);
		}
	}
	
	private List<ViewPager.OnPageChangeListener> flatten(List<ViewPager.OnPageChangeListener> listeners){
		ArrayList<ViewPager.OnPageChangeListener> result=new ArrayList<ViewPager.OnPageChangeListener>();
		for(ViewPager.OnPageChangeListener pcl:listeners){
			if(pcl instanceof ViewPagerChangeListenerObserver){
				result.addAll(flatten(((ViewPagerChangeListenerObserver)pcl).listeners));
			}else{
				result.add(pcl);
			}
		}
		return result;
	}
}
