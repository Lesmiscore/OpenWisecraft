package com.nao20010128nao.Wisecraft.settings;

import android.os.*;
import android.support.v4.view.*;
import android.support.v7.app.*;
import android.view.*;
import com.astuetz.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.activity.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.contextwrappers.extender.*;

public class SettingsImportExportActivity extends AppCompatActivity {
    ViewPager tabs;
    UsefulPagerAdapter adapter;
    ServerListStyleLoader slsl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        slsl = (ServerListStyleLoader) getSystemService(ContextWrappingExtender.SERVER_LIST_STYLE_LOADER);

        tabs = findViewById(R.id.pager);
        tabs.setAdapter(adapter = new UsefulPagerAdapter(this));
        PagerSlidingTabStrip psts = findViewById(R.id.tabs);
        psts.setViewPager(tabs);

        psts.setIndicatorColor(slsl.getTextColor());
        psts.setTextColor(slsl.getTextColor());
        psts.setOnPageChangeListener(new PstsTabColorUpdater(slsl.getTextColor(), ServerInfoActivity.translucent(slsl.getTextColor()), tabs, psts));


        int offset = getIntent().getIntExtra("offset", 0);
        if (adapter.getCount() >= 2 & offset == 0) tabs.setCurrentItem(1);
        tabs.setCurrentItem(offset);
    }

    public static class SettingsImportFragment extends BaseFragment<SettingsImportExportActivity> {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.settings_import_fragment, container, false);
        }
    }

    public static class SettingsExportFragment extends BaseFragment<SettingsImportExportActivity> {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }
}
