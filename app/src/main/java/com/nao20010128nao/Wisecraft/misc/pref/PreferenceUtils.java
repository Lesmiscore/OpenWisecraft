package com.nao20010128nao.Wisecraft.misc.pref;
import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.nao20010128nao.Wisecraft.R;
import com.nao20010128nao.Wisecraft.misc.SetTextColor;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.DialogInterface;
import com.nao20010128nao.Wisecraft.misc.Treatment;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.*;

public class PreferenceUtils
{
	public static void onBindViewHolder(Context context,Preference pref,PreferenceViewHolder holder){
		((TextView)holder.findViewById(android.R.id.title)).setSingleLine(false);
		((TextView)holder.findViewById(android.R.id.title)).setTextAppearance(context,R.style.AppPreferenceTextAppearance);
		holder.itemView.setMinimumHeight(context.getResources().getDimensionPixelOffset(R.dimen.settings_pref_height));
		if(pref instanceof SetTextColor){
			onBindViewHolder((SetTextColor)pref,holder);
		}
		Utils.applyTypefaceForViewTree(holder.itemView,TheApplication.instance.getLocalizedFont());
	}
	public static void onBindViewHolder(SetTextColor pref,PreferenceViewHolder holder){
		((TextView)holder.findViewById(android.R.id.title)).setTextColor(pref.getTextColor());
	}
	//This is a relief measure of EditTextPreferenceDialogFragmentCompat. Will be deleted.
	public static void showEditTextDialog(Activity activity,final Preference preference,String defaultValue,Treatment<View> editor){
		final SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(activity);
		final View v=LayoutInflater.from(activity).inflate(R.layout.preference_dialog_edittext,null);
		v.findViewById(android.R.id.message).setVisibility(View.GONE);
		((EditText)v.findViewById(android.R.id.edit)).setText(pref.getString(preference.getKey(),defaultValue));
		if(editor!=null)editor.process(v);
		new AlertDialog.Builder(activity,R.style.AppAlertDialog)
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
