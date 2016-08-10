package com.nao20010128nao.Wisecraft.misc.pref;
import android.content.res.*;
import android.support.v7.preference.*;
import android.view.*;
import android.widget.*;
import com.mikhaellopez.circularimageview.*;
import com.nao20010128nao.Wisecraft.*;

import com.nao20010128nao.Wisecraft.R;
import android.support.v4.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import com.azeesoft.lib.colorpicker.*;

public class ColorPickerPreferenceCompat extends Preference
{
	int defaultColor;
	
	
	public ColorPickerPreferenceCompat(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context,attrs,defStyleAttr,defStyleRes);
		init (context,attrs,defStyleAttr,defStyleRes);
	}
    public ColorPickerPreferenceCompat(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
		this(context,attrs,defStyleAttr,0);
	}
    public ColorPickerPreferenceCompat(android.content.Context context, android.util.AttributeSet attrs) {
		this(context,attrs,0);
	}
    public ColorPickerPreferenceCompat(android.content.Context context) {
		this(context,null);
	}

	private void init(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr, int defStyleRes){
		final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.ColorPickerPreferenceCompat,
			defStyleAttr, defStyleRes);
		defaultColor=a.getColor(R.styleable.ColorPickerPreferenceCompat_color,Color.BLACK);	
	}
	
	@Override
	public void onBindViewHolder(PreferenceViewHolder holder) {
		// TODO: Implement this method
		super.onBindViewHolder(holder);
		PreferenceUtils.onBindViewHolder(getContext(),this,holder);
		LinearLayout widgetFrame=(LinearLayout)holder.findViewById(android.R.id.widget_frame);
		widgetFrame.removeAllViews();
		LayoutInflater.from(getContext()).inflate(R.layout.color_picker_preference,widgetFrame);
		CircularImageView civ=(CircularImageView)holder.findViewById(R.id.circle);
		civ.setImageDrawable(new ColorDrawable(getPersistedInt(defaultColor)));
	}

	@Override
	protected void onClick() {
		// TODO: Implement this method
		super.onClick();
		ColorPickerDialog cpd=ColorPickerDialog.createColorPickerDialog(getContext(),ColorPickerDialog.LIGHT_THEME);
		cpd.setLastColor(getPersistedInt(defaultColor));
		cpd.setOnColorPickedListener(new ColorPickerDialog.OnColorPickedListener(){
			public void onColorPicked(int color,String hex){
				persistInt(color);
				notifyChanged();
			}
		});
		cpd.setOnClosedListener(new ColorPickerDialog.OnClosedListener(){
			public void onClosed(){
				
			}
		});
		cpd.show();
	}
}
