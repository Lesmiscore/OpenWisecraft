package com.nao20010128nao.Wisecraft.misc;

import android.app.*;
import android.content.*;
import android.support.v7.app.*;
import android.view.*;

import android.support.v7.app.AlertDialog;

public class DialogLauncherListener implements DialogInterface.OnClickListener,View.OnClickListener 
{
	AlertDialog.Builder builder;
	
	public DialogLauncherListener(Activity a){
		builder=new AlertDialog.Builder(a);
	}

	@Override
	public void onClick(View p1) {
		builder.show();
	}

	@Override
	public void onClick(DialogInterface p1, int p2) {
		builder.show();
	}
	
	@android.support.annotation.NonNull()
	public android.content.Context getContext() {
		return builder.getContext();
	}

	public DialogLauncherListener setTitle(int titleId) {
		builder.setTitle(titleId);
		return this;
	}

	public DialogLauncherListener setTitle(java.lang.CharSequence title) {
		return this;
	}

	public DialogLauncherListener setCustomTitle(android.view.View customTitleView) {
		return this;
	}

	public DialogLauncherListener setMessage(int messageId) {
		return this;
	}

	public DialogLauncherListener setMessage(java.lang.CharSequence message) {
		return this;
	}

	public DialogLauncherListener setIcon(int iconId) {
		return this;
	}

	public DialogLauncherListener setIcon(android.graphics.drawable.Drawable icon) {
		return this;
	}

	public DialogLauncherListener setIconAttribute(int attrId) {
		return this;
	}

	public DialogLauncherListener setPositiveButton(int textId, android.content.DialogInterface.OnClickListener listener) {
		return this;
	}

	public DialogLauncherListener setPositiveButton(java.lang.CharSequence text, android.content.DialogInterface.OnClickListener listener) {
		return this;
	}

	public DialogLauncherListener setNegativeButton(int textId, android.content.DialogInterface.OnClickListener listener) {
		return this;
	}

	public DialogLauncherListener setNegativeButton(java.lang.CharSequence text, android.content.DialogInterface.OnClickListener listener) {
		return this;
	}

	public DialogLauncherListener setNeutralButton(int textId, android.content.DialogInterface.OnClickListener listener) {
		return this;
	}

	public DialogLauncherListener setNeutralButton(java.lang.CharSequence text, android.content.DialogInterface.OnClickListener listener) {
		return this;
	}

	public DialogLauncherListener setCancelable(boolean cancelable) {
		return this;
	}

	public DialogLauncherListener setOnCancelListener(android.content.DialogInterface.OnCancelListener onCancelListener) {
		return this;
	}

	public DialogLauncherListener setOnDismissListener(android.content.DialogInterface.OnDismissListener onDismissListener) {
		return this;
	}

	public DialogLauncherListener setOnKeyListener(android.content.DialogInterface.OnKeyListener onKeyListener) {
		return this;
	}

	public DialogLauncherListener setItems(int itemsId, android.content.DialogInterface.OnClickListener listener) {
		return this;
	}

	public DialogLauncherListener setItems(java.lang.CharSequence[] items, android.content.DialogInterface.OnClickListener listener) {
		return this;
	}

	public DialogLauncherListener setAdapter(android.widget.ListAdapter adapter, android.content.DialogInterface.OnClickListener listener) {
		return this;
	}

	public DialogLauncherListener setCursor(android.database.Cursor cursor, android.content.DialogInterface.OnClickListener listener, java.lang.String labelColumn) {
		return this;
	}

	public DialogLauncherListener setMultiChoiceItems(int itemsId, boolean[] checkedItems, android.content.DialogInterface.OnMultiChoiceClickListener listener) {
		return this;
	}

	public DialogLauncherListener setMultiChoiceItems(java.lang.CharSequence[] items, boolean[] checkedItems, android.content.DialogInterface.OnMultiChoiceClickListener listener) {
		return this;
	}

	public DialogLauncherListener setMultiChoiceItems(android.database.Cursor cursor, java.lang.String isCheckedColumn, java.lang.String labelColumn, android.content.DialogInterface.OnMultiChoiceClickListener listener) {
		return this;
	}

	public DialogLauncherListener setSingleChoiceItems(int itemsId, int checkedItem, android.content.DialogInterface.OnClickListener listener) {
		return this;
	}

	public DialogLauncherListener setSingleChoiceItems(android.database.Cursor cursor, int checkedItem, java.lang.String labelColumn, android.content.DialogInterface.OnClickListener listener) {
		return this;
	}

	public DialogLauncherListener setSingleChoiceItems(java.lang.CharSequence[] items, int checkedItem, android.content.DialogInterface.OnClickListener listener) {
		return this;
	}

	public DialogLauncherListener setSingleChoiceItems(android.widget.ListAdapter adapter, int checkedItem, android.content.DialogInterface.OnClickListener listener) {
		return this;
	}

	public DialogLauncherListener setOnItemSelectedListener(android.widget.AdapterView.OnItemSelectedListener listener) {
		return this;
	}

	public DialogLauncherListener setView(int layoutResId) {
		return this;
	}

	public DialogLauncherListener setView(android.view.View view) {
		return this;
	}

	/** @deprecated */
	@java.lang.Deprecated()
	@android.support.annotation.RestrictTo(value={android.support.annotation.RestrictTo.Scope.GROUP_ID})
	public DialogLauncherListener setView(android.view.View view, int viewSpacingLeft, int viewSpacingTop, int viewSpacingRight, int viewSpacingBottom) {
		return this;
	}

	/** @deprecated */
	@java.lang.Deprecated()
	public DialogLauncherListener setInverseBackgroundForced(boolean useInverseBackground) {
		return this;
	}

	@android.support.annotation.RestrictTo(value={android.support.annotation.RestrictTo.Scope.GROUP_ID})
	public DialogLauncherListener setRecycleOnMeasureEnabled(boolean enabled) {
		return this;
	}
	
}
