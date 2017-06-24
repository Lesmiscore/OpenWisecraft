package com.nao20010128nao.Wisecraft.activity;

import android.content.*;
import android.graphics.*;
import android.os.*;
import com.nao20010128nao.Wisecraft.scrolling.scrolling.*;
import com.nao20010128nao.Wisecraft.R;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;

public class OpenSourceActivity extends ScrollingActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemePatcher.applyThemeForActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.osl_parent);
        getSupportActionBar().setElevation(0f);
        Utils.getActionBarTextView(this).setTextColor(Color.WHITE);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TheApplication.injectContextSpecial(newBase));
    }

    @Override
    public int getLayoutResId() {
        return R.layout.osl_decor;
    }
}
