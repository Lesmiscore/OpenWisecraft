package com.nao20010128nao.Wisecraft.misc;
import android.support.v4.app.*;
import java.util.*;

public class UsefulPagerAdapter extends FragmentPagerAdapter{
	List<Map.Entry<Class,String>> pages=new ArrayList<>();

	public UsefulPagerAdapter(FragmentManager fm){
		super(fm);
	}
	
	public UsefulPagerAdapter(FragmentActivity fa){
		this(fa.getSupportFragmentManager());
	}

	public void addTab(Class<? extends Fragment> clat,String title){
		pages.add(new KVP<Class,String>(clat,title));
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
		try {
			return (Fragment)pages.get(p1).getKey().newInstance();
		} catch (Throwable e) {
			return null;
		}
	}

	@Override
	public CharSequence getPageTitle(int position) {
		// TODO: Implement this method
		return pages.get(position).getValue();
	}
}
