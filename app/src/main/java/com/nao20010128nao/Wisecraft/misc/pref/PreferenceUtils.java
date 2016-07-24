package com.nao20010128nao.Wisecraft.misc.pref;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.TextView;
import com.nao20010128nao.Wisecraft.R;
import com.nao20010128nao.Wisecraft.misc.SetTextColor;
import android.content.Context;

public class PreferenceUtils
{
	public static void onBindViewHolder(Context context,Preference pref,PreferenceViewHolder holder){
		((TextView)holder.findViewById(android.R.id.title)).setSingleLine(false);
		((TextView)holder.findViewById(android.R.id.title)).setTextAppearance(context,R.style.AppPreferenceTextAppearance);
		holder.itemView.setMinimumHeight(context.getResources().getDimensionPixelOffset(R.dimen.settings_pref_height));
		if(pref instanceof SetTextColor){
			onBindViewHolder((SetTextColor)pref,holder);
		}
	}
	public static void onBindViewHolder(SetTextColor pref,PreferenceViewHolder holder){
		((TextView)holder.findViewById(android.R.id.title)).setTextColor(pref.getTextColor());
	}
}
