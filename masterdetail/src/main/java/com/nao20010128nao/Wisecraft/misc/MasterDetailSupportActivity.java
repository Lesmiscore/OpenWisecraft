package com.nao20010128nao.Wisecraft.misc;

import android.os.*;
import android.support.annotation.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import com.nao20010128nao.Wisecraft.misc.MasterDetail.*;

public abstract class MasterDetailSupportActivity extends AppCompatActivity {

    private boolean mTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup root=getViewToInflate();
        getLayoutInflater().inflate(R.layout.activity_item_list,root);

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

    public boolean isTwoPane() {
        return mTwoPane;
    }

    public abstract void setupRecyclerView(@NonNull RecyclerView recyclerView);

    public ViewGroup getViewToInflate(){
        return findViewById(android.R.id.content);
    }
}
