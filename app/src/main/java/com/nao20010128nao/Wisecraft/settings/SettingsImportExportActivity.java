package com.nao20010128nao.Wisecraft.settings;
import android.os.*;
import android.support.v4.content.*;
import android.support.v4.view.*;
import android.support.v7.app.*;
import android.view.*;
import com.astuetz.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;

public class SettingsImportExportActivity extends AppCompatActivity {
	ViewPager tabs;
	UsefulPagerAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		tabs = (ViewPager)findViewById(R.id.pager);
		tabs.setAdapter(adapter = new UsefulPagerAdapter(this));
		PagerSlidingTabStrip psts=(PagerSlidingTabStrip)findViewById(R.id.tabs);
		psts.setViewPager(tabs);
		
		psts.setIndicatorColor(ContextCompat.getColor(this, R.color.upd_2));
		psts.setTextColor(ContextCompat.getColor(this, R.color.upd_2));
		psts.setOnPageChangeListener(new PstsTabColorUpdater(ContextCompat.getColor(this, R.color.upd_2), ServerInfoActivity.PALE_PRIMARY, tabs, psts));






		int offset=getIntent().getIntExtra("offset", 0);
		if (adapter.getCount() >= 2 & offset == 0)tabs.setCurrentItem(1);
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
			return inflater.inflate(R.layout.settings_import_fragment, container, false);
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
