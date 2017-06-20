package com.nao20010128nao.TESTAPP;

import android.os.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;

public abstract class ScrollingActivity extends AppCompatActivity implements ScrollingActivityDelegate.Callback {
    FrameLayout content;
    ScrollingActivityDelegate sad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSDelegate().onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(View view) {
        getSDelegate().setContentView(view);
    }

    @Override
    public void setContentView(int layoutResID) {
        getSDelegate().setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getSDelegate().setContentView(view, params);
    }

    @Override
    public View findViewById(int id) {
        return getSDelegate().findViewById(id);
    }

    protected ScrollingActivityDelegate getSDelegate() {
        if (sad == null) sad = new ScrollingActivityDelegate(this, getDelegate(), this);
        return sad;
    }

    public int getLayoutResId() {
        return R.layout.activity_scrolling;
    }
}
