package com.nao20010128nao.Wisecraft.misc;

/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.*;
import android.content.res.*;
import android.os.*;
import android.support.annotation.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;

/**
 * Base class for {@link android.app.Dialog}s styled as a bottom sheet.
 */
public class ModifiedBottomSheetDialog extends AppCompatDialog {

    private BottomSheetBehavior<FrameLayout> mBehavior;

    boolean mCancelable = true;
    private boolean mCanceledOnTouchOutside = true;
    private boolean mCanceledOnTouchOutsideSet;

    public ModifiedBottomSheetDialog(@NonNull Context context) {
        this(context, 0);
    }

    public ModifiedBottomSheetDialog(@NonNull Context context, @StyleRes int theme) {
        super(context, getThemeResId(context, theme));
        // We hide the title bar for any style configuration. Otherwise, there will be a gap
        // above the bottom sheet when it is expanded.
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    protected ModifiedBottomSheetDialog(@NonNull Context context, boolean cancelable,
		OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        mCancelable = cancelable;
    }

    @Override
    public void setContentView(@LayoutRes int layoutResId) {
        super.setContentView(wrapInBottomSheet(layoutResId, null, null));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setLayout(
			ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(wrapInBottomSheet(0, view, null));
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(wrapInBottomSheet(0, view, params));
    }

    @Override
    public void setCancelable(boolean cancelable) {
        super.setCancelable(cancelable);
        if (mCancelable != cancelable) {
            mCancelable = cancelable;
            if (mBehavior != null) {
                mBehavior.setHideable(cancelable);
            }
        }
    }

    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
        if (cancel && !mCancelable) {
            mCancelable = true;
        }
        mCanceledOnTouchOutside = cancel;
        mCanceledOnTouchOutsideSet = true;
    }
	
	public void setMBehavior(BottomSheetBehavior<FrameLayout> mBehavior) {
		this.mBehavior = mBehavior;
	}

	public BottomSheetBehavior<FrameLayout> getMBehavior() {
		return mBehavior;
	}

	@Override
	public void cancel() {
		mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
	}

	@Override
	public void dismiss() {
		mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
	}
	
	private void forceCancel(){
		super.dismiss();//cancel() calls dismiss() inside at Dialog class
	}
	
    private View wrapInBottomSheet(int layoutResId, View view, ViewGroup.LayoutParams params) {
        final View decor=View.inflate(getContext(), getDecorResourceId(getContext()), null);
		final CoordinatorLayout coordinator = (CoordinatorLayout) (decor instanceof CoordinatorLayout?decor:decor.findViewById(R.id.coordinator));
        if (layoutResId != 0 && view == null) {
            view = getLayoutInflater().inflate(layoutResId, coordinator, false);
        }
        FrameLayout bottomSheet = (FrameLayout) coordinator.findViewById(R.id.design_bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottomSheet);
        mBehavior.setBottomSheetCallback(mBottomSheetCallback);
        mBehavior.setHideable(mCancelable);
        if (params == null) {
            bottomSheet.addView(view);
        } else {
            bottomSheet.addView(view, params);
        }
        // We treat the CoordinatorLayout as outside the dialog though it is technically inside
        coordinator.findViewById(R.id.touch_outside).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (mCancelable && isShowing() && shouldWindowCloseOnTouchOutside()) {
						cancel();
					}
				}
			});
        return decor;
    }

    boolean shouldWindowCloseOnTouchOutside() {
        if (!mCanceledOnTouchOutsideSet) {
            if (Build.VERSION.SDK_INT < 11) {
                mCanceledOnTouchOutside = true;
            } else {
                TypedArray a = getContext().obtainStyledAttributes(
					new int[]{android.R.attr.windowCloseOnTouchOutside});
                mCanceledOnTouchOutside = a.getBoolean(0, true);
                a.recycle();
            }
            mCanceledOnTouchOutsideSet = true;
        }
        return mCanceledOnTouchOutside;
    }

    static int getThemeResId(Context context, int themeId) {
        if (themeId == 0) {
            // If the provided theme is 0, then retrieve the dialogTheme from our theme
			TypedArray ta=context.getTheme().obtainStyledAttributes(R.styleable.ModifiedBottomSheetDialog);
			if(ta.hasValue(R.styleable.ModifiedBottomSheetDialog_modifiedBottomSheetDialogTheme)){
				themeId=ta.getResourceId(R.styleable.ModifiedBottomSheetDialog_modifiedBottomSheetDialogTheme,0);
			}else if(ta.hasValue(R.styleable.ModifiedBottomSheetDialog_bottomSheetDialogTheme)){
				themeId=ta.getResourceId(R.styleable.ModifiedBottomSheetDialog_bottomSheetDialogTheme,0);
			}else{
				themeId = R.style.Theme_LibCompat_Light_BottomSheetDialog;
			}
			ta.recycle();
        }
        return themeId;
    }
	
	private static int getDecorResourceId(Context c){
		int res=R.layout.design_bottom_sheet_dialog;
		TypedArray ta=c.getTheme().obtainStyledAttributes(R.styleable.ModifiedBottomSheetDialog);
		if(ta.hasValue(R.styleable.ModifiedBottomSheetDialog_wcBottomSheetDialogDecor)){
			res=ta.getResourceId(R.styleable.ModifiedBottomSheetDialog_wcBottomSheetDialogDecor,res);
		}
		ta.recycle();
		return res;
	}

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetCallback
	= new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet,
			@BottomSheetBehavior.State int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                forceCancel();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

}
