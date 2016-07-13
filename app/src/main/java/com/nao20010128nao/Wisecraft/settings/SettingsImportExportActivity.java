package com.nao20010128nao.Wisecraft.settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import com.nao20010128nao.Wisecraft.misc.UsefulPagerAdapter;
import com.astuetz.PagerSlidingTabStrip;
import com.nao20010128nao.Wisecraft.misc.PstsTabColorUpdater;
import com.nao20010128nao.Wisecraft.R;
import com.nao20010128nao.Wisecraft.ServerInfoActivity;
import com.nao20010128nao.Wisecraft.misc.BaseFragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

public class SettingsImportExportActivity extends AppCompatActivity
{
	ViewPager tabs;
	UsefulPagerAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		tabs = (ViewPager)findViewById(R.id.pager);
		tabs.setAdapter(adapter=new UsefulPagerAdapter(this));
		PagerSlidingTabStrip psts=(PagerSlidingTabStrip)findViewById(R.id.tabs);
		psts.setViewPager(tabs);
		
			psts.setIndicatorColor(getResources().getColor(R.color.upd_2));
			psts.setTextColor(getResources().getColor(R.color.upd_2));
			psts.setOnPageChangeListener(new PstsTabColorUpdater(getResources().getColor(R.color.upd_2),ServerInfoActivity.PALE_PRIMARY,tabs,psts));
		
		
		
		
		
		
		int offset=getIntent().getIntExtra("offset", 0);
		if(adapter.getCount()>=2&offset==0)tabs.setCurrentItem(1);
		tabs.setCurrentItem(offset);
	}
	
	public static class SettingsImportFragment extends BaseFragment<SettingsImportExportActivity> {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			// TODO: Implement this method
			super.onCreate(savedInstanceState);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO: Implement this method
			return inflater.inflate(R.layout.settings_import_fragment,container,false);
		}
	}
	
	public static class SettingsExportFragment extends BaseFragment<SettingsImportExportActivity> {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			// TODO: Implement this method
			super.onCreate(savedInstanceState);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO: Implement this method
			return super.onCreateView(inflater, container, savedInstanceState);
		}
	}
}
