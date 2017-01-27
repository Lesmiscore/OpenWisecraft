package com.nao20010128nao.Wisecraft.misc;
import android.support.v7.app.*;

public interface DialogBuilder<This extends DialogBuilder,Dialog extends AppCompatDialog>
{
	@android.support.annotation.NonNull()
	public android.content.Context getContext();

	public This setTitle(int titleId);

	public This setTitle(java.lang.CharSequence title);

	public This setCustomTitle(android.view.View customTitleView);

	public This setMessage(int messageId);

	public This setMessage(java.lang.CharSequence message);

	public This setIcon(int iconId);

	public This setIcon(android.graphics.drawable.Drawable icon);

	public This setIconAttribute(int attrId);

	public This setPositiveButton(int textId, android.content.DialogInterface.OnClickListener listener);

	public This setPositiveButton(java.lang.CharSequence text, android.content.DialogInterface.OnClickListener listener);

	public This setNegativeButton(int textId, android.content.DialogInterface.OnClickListener listener);

	public This setNegativeButton(java.lang.CharSequence text, android.content.DialogInterface.OnClickListener listener);

	public This setNeutralButton(int textId, android.content.DialogInterface.OnClickListener listener);

	public This setNeutralButton(java.lang.CharSequence text, android.content.DialogInterface.OnClickListener listener);

	public This setCancelable(boolean cancelable);

	public This setOnCancelListener(android.content.DialogInterface.OnCancelListener onCancelListener);

	public This setOnDismissListener(android.content.DialogInterface.OnDismissListener onDismissListener);

	public This setOnKeyListener(android.content.DialogInterface.OnKeyListener onKeyListener);

	public This setItems(int itemsId, android.content.DialogInterface.OnClickListener listener);

	public This setItems(java.lang.CharSequence[] items, android.content.DialogInterface.OnClickListener listener);

	public This setAdapter(android.widget.ListAdapter adapter, android.content.DialogInterface.OnClickListener listener);

	public This setCursor(android.database.Cursor cursor, android.content.DialogInterface.OnClickListener listener, java.lang.String labelColumn);

	public This setMultiChoiceItems(int itemsId, boolean[] checkedItems, android.content.DialogInterface.OnMultiChoiceClickListener listener);

	public This setMultiChoiceItems(java.lang.CharSequence[] items, boolean[] checkedItems, android.content.DialogInterface.OnMultiChoiceClickListener listener);

	public This setMultiChoiceItems(android.database.Cursor cursor, java.lang.String isCheckedColumn, java.lang.String labelColumn, android.content.DialogInterface.OnMultiChoiceClickListener listener);

	public This setSingleChoiceItems(int itemsId, int checkedItem, android.content.DialogInterface.OnClickListener listener);

	public This setSingleChoiceItems(android.database.Cursor cursor, int checkedItem, java.lang.String labelColumn, android.content.DialogInterface.OnClickListener listener);

	public This setSingleChoiceItems(java.lang.CharSequence[] items, int checkedItem, android.content.DialogInterface.OnClickListener listener);

	public This setSingleChoiceItems(android.widget.ListAdapter adapter, int checkedItem, android.content.DialogInterface.OnClickListener listener);

	public This setOnItemSelectedListener(android.widget.AdapterView.OnItemSelectedListener listener);

	public This setView(int layoutResId);

	public This setView(android.view.View view);

	/** @deprecated */
	@java.lang.Deprecated()
	@android.support.annotation.RestrictTo(value={android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP,})
	public This setView(android.view.View view, int viewSpacingLeft, int viewSpacingTop, int viewSpacingRight, int viewSpacingBottom);

	/** @deprecated */
	@java.lang.Deprecated()
	public This setInverseBackgroundForced(boolean useInverseBackground);

	@android.support.annotation.RestrictTo(value={android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP,})
	public This setRecycleOnMeasureEnabled(boolean enabled);

	public Dialog create();

	public Dialog show();
}
