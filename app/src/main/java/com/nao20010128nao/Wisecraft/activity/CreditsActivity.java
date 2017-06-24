package com.nao20010128nao.Wisecraft.activity;

import android.content.*;
import android.os.*;
import com.nao20010128nao.Wisecraft.scrolling.scrolling.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;

public class CreditsActivity extends ScrollingActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemePatcher.applyThemeForActivity(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TheApplication.injectContextSpecial(newBase));
    }

}
