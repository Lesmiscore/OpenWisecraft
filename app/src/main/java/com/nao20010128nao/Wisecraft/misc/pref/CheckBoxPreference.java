package com.nao20010128nao.Wisecraft.misc.pref;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.support.v4.widget.TextViewCompat;

public class CheckBoxPreference extends android.preference.CheckBoxPreference
{
	public CheckBoxPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CheckBoxPreference(
		Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public CheckBoxPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckBoxPreference(Context context) {
        super(context);
    }

	@Override
	protected void onBindView(View view) {
		// TODO: Implement this method
		super.onBindView(view);
		((TextView)view.findViewById(android.R.id.title)).setSingleLine(false);
	}
}
