package com.nao20010128nao.Wisecraft.misc;

import android.content.*;
import android.support.annotation.*;
import android.support.v7.widget.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.*;

public class BottomSheetListDialog extends ModifiedBottomSheetDialog {
    private RecyclerView rv;
    private TextView title;

    public BottomSheetListDialog(@NonNull Context context) {
        super(context);
        init(context);
    }

    public BottomSheetListDialog(@NonNull Context context, @StyleRes int theme) {
        super(context, theme);
        init(context);
    }

    private void init(Context context) {
        setContentView(R.layout.bottom_sheet_list_dialog);
        rv = findViewById(R.id.list);
        title = findViewById(R.id.title);
    }

    @Override
    public void setTitle(CharSequence title0) {
        title.setText(title0);
    }

    @Override
    public void setTitle(int titleId) {
        title.setText(titleId);
    }

    public RecyclerView getRecyclerView() {
        return rv;
    }

    public RecyclerView.Adapter getAdapter() {
        return rv.getAdapter();
    }

    public void setAdapter(RecyclerView.Adapter adap) {
        rv.setAdapter(adap);
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return rv.getLayoutManager();
    }

    public void setLayoutManager(RecyclerView.LayoutManager lm) {
        rv.setLayoutManager(lm);
    }
}
