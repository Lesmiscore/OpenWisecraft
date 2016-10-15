package com.nao20010128nao.Wisecraft.misc.pref;
import android.app.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.preference.*;
import android.support.v4.content.*;
import android.support.v7.app.*;
import android.support.v7.preference.*;
import android.view.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;

import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import com.nao20010128nao.Wisecraft.R;

public class PreferenceUtils
{
	public static void onBindViewHolder(Context context,Preference pref,PreferenceViewHolder holder){
		((TextView)holder.findViewById(android.R.id.title)).setSingleLine(false);
		((TextView)holder.findViewById(android.R.id.title)).setTextAppearance(context,R.style.AppPreferenceTextAppearance);
		holder.itemView.setMinimumHeight(context.getResources().getDimensionPixelOffset(R.dimen.settings_pref_height));
		if(pref instanceof SetTextColor){
			onBindViewHolder((SetTextColor)pref,holder);
		}else{
			((TextView)holder.findViewById(android.R.id.title)).setTextColor(getDefaultPreferenceTextColor(context));
		}
		if(!pref.isEnabled()){
			((TextView)holder.findViewById(android.R.id.title)).setTextColor(ContextCompat.getColor(context,R.color.color888));
		}
		Utils.applyTypefaceForViewTree(holder.itemView,TheApplication.instance.getLocalizedFont());
	}
	public static void onBindViewHolder(SetTextColor pref,PreferenceViewHolder holder){
		((TextView)holder.findViewById(android.R.id.title)).setTextColor(pref.getTextColor());
	}
	public static int getDefaultPreferenceTextColor(Context context){
		TypedArray ta=context.obtainStyledAttributes(new int[]{R.attr.wcDefaultPreferenceTextColor});
		int color=ta.getColor(0,Color.BLACK);
		ta.recycle();
		return color;
	}
	//This is a relief measure of EditTextPreferenceDialogFragmentCompat. Will be deleted.
	public static void showEditTextDialog(Activity activity,final Preference preference,String defaultValue,Treatment<View> editor){
		final SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(activity);
		final View v=LayoutInflater.from(activity).inflate(R.layout.preference_dialog_edittext,null);
		v.findViewById(android.R.id.message).setVisibility(View.GONE);
		((EditText)v.findViewById(android.R.id.edit)).setText(pref.getString(preference.getKey(),defaultValue));
		if(editor!=null)editor.process(v);
		new AlertDialog.Builder(activity)
			.setTitle(preference.getTitle())
			.setView(v)
			.setCancelable(true)
			.setNegativeButton(android.R.string.cancel,null)
			.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface di,int w){
					pref.edit().putString(preference.getKey(),((EditText)v.findViewById(android.R.id.edit)).getText().toString()).commit();
				}
			})
			.show();
	}
}
