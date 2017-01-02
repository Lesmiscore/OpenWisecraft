package com.nao20010128nao.Wisecraft.misc;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.nao20010128nao.Wisecraft.misc.MasterDetail.R;

public abstract class MasterDetailSupportActivity extends AppCompatActivity {

    private boolean mTwoPane=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.item_detail_container) != null) {
            mTwoPane = true;
        }
    }

    public boolean isTwoPane(){
        return mTwoPane;
    }

    public abstract void setupRecyclerView(@NonNull RecyclerView recyclerView);
}
