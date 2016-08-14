package com.nao20010128nao.Wisecraft.misc;

import android.support.v4.app.*;
import java.util.*;

public class UsefulPagerAdapter2 extends FragmentStatePagerAdapter{
	List<Map.Entry<Fragment,String>> pages=new ArrayList<>();

	public UsefulPagerAdapter2(FragmentManager fm){
		super(fm);
	}

	public UsefulPagerAdapter2(FragmentActivity fa){
		this(fa.getSupportFragmentManager());
	}

	public void addTab(Fragment clat,String title){
		pages.add(new KVP<Fragment,String>(clat,title));
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO: Implement this method
		return pages.size();
	}

	@Override
	public Fragment getItem(int p1) {
		// TODO: Implement this method
		return pages.get(p1).getKey();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		// TODO: Implement this method
		return pages.get(position).getValue();
	}
}
