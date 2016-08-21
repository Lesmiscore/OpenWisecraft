package com.nao20010128nao.Wisecraft.misc.pref;
import android.content.Context;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.widget.TextView;
import com.nao20010128nao.Wisecraft.misc.compat.R;
import com.nao20010128nao.Wisecraft.misc.SetTextColor;
import android.graphics.Color;

public class CheckBoxPreferenceCompat extends android.support.v7.preference.CheckBoxPreference implements SetTextColor{
	int color=Color.BLACK;
	
	public CheckBoxPreferenceCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CheckBoxPreferenceCompat(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public CheckBoxPreferenceCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckBoxPreferenceCompat(Context context) {
        super(context);
    }
	
	@Override
	public void onBindViewHolder(PreferenceViewHolder holder) {
		// TODO: Implement this method
		super.onBindViewHolder(holder);
		PreferenceUtils.onBindViewHolder(getContext(),this,holder);
	}

	@Override
	public void setTextColor(int color) {
		// TODO: Implement this method
		this.color=color;
	}

	@Override
	public int getTextColor() {
		// TODO: Implement this method
		return color;
	}
}
