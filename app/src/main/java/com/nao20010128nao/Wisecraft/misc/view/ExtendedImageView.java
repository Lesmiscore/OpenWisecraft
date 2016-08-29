package com.nao20010128nao.Wisecraft.misc.view;
import android.content.*;
import android.graphics.drawable.*;
import android.support.v4.content.*;
import android.support.v7.widget.*;
import android.util.*;

public class ExtendedImageView extends AppCompatImageView {
	public ExtendedImageView(Context context) {
		super(context);
	}

    public ExtendedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

    public ExtendedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void setColor(int color) {
		setImageDrawable(new ColorDrawable(color));
	}

	public void setColorRes(int les) {
		setColor(ContextCompat.getColor(getContext(), les));
	}
}
