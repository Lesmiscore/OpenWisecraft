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
		builder.setTitle(title);
		return this;
	}

	public DialogLauncherListener setCustomTitle(android.view.View customTitleView) {
		builder.setCustomTitle(customTitleView);
		return this;
	}

	public DialogLauncherListener setMessage(int messageId) {
		builder.setMessage(messageId);
		return this;
	}

	public DialogLauncherListener setMessage(java.lang.CharSequence message) {
		builder.setMessage(message);
		return this;
	}

	public DialogLauncherListener setIcon(int iconId) {
		builder.setIcon(iconId);
		return this;
	}

	public DialogLauncherListener setIcon(android.graphics.drawable.Drawable icon) {
		builder.setIcon(icon);
		return this;
	}

	public DialogLauncherListener setIconAttribute(int attrId) {
		builder.setIconAttribute(attrId);
		return this;
	}

	public DialogLauncherListener setPositiveButton(int textId, android.content.DialogInterface.OnClickListener listener) {
		builder.setPositiveButton(textId,listener);
		return this;
	}

	public DialogLauncherListener setPositiveButton(java.lang.CharSequence text, android.content.DialogInterface.OnClickListener listener) {
		builder.setPositiveButton(text,listener);
		return this;
	}

	public DialogLauncherListener setNegativeButton(int textId, android.content.DialogInterface.OnClickListener listener) {
		builder.setNegativeButton(textId,listener);
		return this;
	}

	public DialogLauncherListener setNegativeButton(java.lang.CharSequence text, android.content.DialogInterface.OnClickListener listener) {
		builder.setNegativeButton(text,listener);
		return this;
	}

	public DialogLauncherListener setNeutralButton(int textId, android.content.DialogInterface.OnClickListener listener) {
		builder.setNeutralButton(textId,listener);
		return this;
	}

	public DialogLauncherListener setNeutralButton(java.lang.CharSequence text, android.content.DialogInterface.OnClickListener listener) {
		builder.setNeutralButton(text,listener);
		return this;
	}

	public DialogLauncherListener setCancelable(boolean cancelable) {
		builder.setCancelable(cancelable);
		return this;
	}

	public DialogLauncherListener setOnCancelListener(android.content.DialogInterface.OnCancelListener onCancelListener) {
		builder.setOnCancelListener(onCancelListener);
		return this;
	}

	public DialogLauncherListener setOnDismissListener(android.content.DialogInterface.OnDismissListener onDismissListener) {
		builder.setOnDismissListener(onDismissListener);
		return this;
	}

	public DialogLauncherListener setOnKeyListener(android.content.DialogInterface.OnKeyListener onKeyListener) {
		builder.setOnKeyListener(onKeyListener);
		return this;
	}

	public DialogLauncherListener setItems(int itemsId, android.content.DialogInterface.OnClickListener listener) {
		builder.setItems(itemsId,listener);
		return this;
	}

	public DialogLauncherListener setItems(java.lang.CharSequence[] items, android.content.DialogInterface.OnClickListener listener) {
		builder.setItems(items,listener);
		return this;
	}

	public DialogLauncherListener setAdapter(android.widget.ListAdapter adapter, android.content.DialogInterface.OnClickListener listener) {
		builder.setAdapter(adapter,listener);
		return this;
	}

	public DialogLauncherListener setCursor(android.database.Cursor cursor, android.content.DialogInterface.OnClickListener listener, java.lang.String labelColumn) {
		builder.setCursor(cursor,listener,labelColumn);
		return this;
	}

	public DialogLauncherListener setMultiChoiceItems(int itemsId, boolean[] checkedItems, android.content.DialogInterface.OnMultiChoiceClickListener listener) {
		builder.setMultiChoiceItems(itemsId,checkedItems,listener);
		return this;
	}

	public DialogLauncherListener setMultiChoiceItems(java.lang.CharSequence[] items, boolean[] checkedItems, android.content.DialogInterface.OnMultiChoiceClickListener listener) {
		builder.setMultiChoiceItems(items,checkedItems,listener);
		return this;
	}

	public DialogLauncherListener setMultiChoiceItems(android.database.Cursor cursor, java.lang.String isCheckedColumn, java.lang.String labelColumn, android.content.DialogInterface.OnMultiChoiceClickListener listener) {
		builder.setMultiChoiceItems(cursor,isCheckedColumn,labelColumn,listener);
		return this;
	}

	public DialogLauncherListener setSingleChoiceItems(int itemsId, int checkedItem, android.content.DialogInterface.OnClickListener listener) {
		builder.setSingleChoiceItems(itemsId,checkedItem,listener);
		return this;
	}

	public DialogLauncherListener setSingleChoiceItems(android.database.Cursor cursor, int checkedItem, java.lang.String labelColumn, android.content.DialogInterface.OnClickListener listener) {
		builder.setSingleChoiceItems(cursor,checkedItem,labelColumn,listener);
		return this;
	}

	public DialogLauncherListener setSingleChoiceItems(java.lang.CharSequence[] items, int checkedItem, android.content.DialogInterface.OnClickListener listener) {
		builder.setSingleChoiceItems(items,checkedItem,listener);
		return this;
	}

	public DialogLauncherListener setSingleChoiceItems(android.widget.ListAdapter adapter, int checkedItem, android.content.DialogInterface.OnClickListener listener) {
		builder.setSingleChoiceItems(adapter,checkedItem,listener);
		return this;
	}

	public DialogLauncherListener setOnItemSelectedListener(android.widget.AdapterView.OnItemSelectedListener listener) {
		builder.setOnItemSelectedListener(listener);
		return this;
	}

	public DialogLauncherListener setView(int layoutResId) {
		builder.setView(layoutResId);
		return this;
	}

	public DialogLauncherListener setView(android.view.View view) {
		builder.setView(view);
		return this;
	}

	/** @deprecated */
	@java.lang.Deprecated()
	@android.support.annotation.RestrictTo(value={android.support.annotation.RestrictTo.Scope.GROUP_ID})
	public DialogLauncherListener setView(android.view.View view, int viewSpacingLeft, int viewSpacingTop, int viewSpacingRight, int viewSpacingBottom) {
		builder.setView(view,viewSpacingLeft,viewSpacingTop,viewSpacingRight,viewSpacingBottom);
		return this;
	}

	/** @deprecated */
	@java.lang.Deprecated()
	public DialogLauncherListener setInverseBackgroundForced(boolean useInverseBackground) {
		builder.setInverseBackgroundForced(useInverseBackground);
		return this;
	}

	@android.support.annotation.RestrictTo(value={android.support.annotation.RestrictTo.Scope.GROUP_ID})
	public DialogLauncherListener setRecycleOnMeasureEnabled(boolean enabled) {
		builder.setRecycleOnMeasureEnabled(enabled);
		return this;
	}
	
}
