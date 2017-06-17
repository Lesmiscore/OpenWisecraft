package com.nao20010128nao.Wisecraft.activity;

import android.content.*;
import android.os.*;
import com.mikepenz.materialdrawer.model.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.rcon.*;

public class RCONActivity extends RCONActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemePatcher.applyThemeForActivity(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public PrimaryDrawerItem onCreatePrimaryDrawerItem() {
        return new LineWrappingPrimaryDrawerItem();
    }

    @Override
    public SectionDrawerItem onCreateSectionDrawerItem() {
        return new LineWrappingSectionDrawerItem();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TheApplication.injectContextSpecial(newBase));
    }
}
