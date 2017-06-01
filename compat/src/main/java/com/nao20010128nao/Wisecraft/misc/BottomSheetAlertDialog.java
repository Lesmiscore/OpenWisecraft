package com.nao20010128nao.Wisecraft.misc;


import android.content.*;
import android.database.*;
import android.graphics.drawable.*;
import android.os.*;
import android.support.annotation.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import android.content.*;
import android.content.res.*;
import android.database.*;
import android.graphics.drawable.*;
import android.os.*;
import android.support.annotation.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import android.widget.FrameLayout.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import java.lang.ref.*;

import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.FrameLayout.LayoutParams;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * A subclass of Dialog that can display one, two or three buttons. If you only want to
 * display a String in this dialog box, use the setMessage() method.  If you
 * want to display a more complex view, look up the FrameLayout called "custom"
 * and add your view to it:
 *
 * <pre>
 * FrameLayout fl = (FrameLayout) findViewById(android.R.id.custom);
 * fl.addView(myView, new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
 * </pre>
 *
 * <p>The AlertDialog class takes care of automatically setting
 * {@link WindowManager.LayoutParams#FLAG_ALT_FOCUSABLE_IM
 * WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM} for you based on whether
 * any views in the dialog return true from {@link View#onCheckIsTextEditor()
 * View.onCheckIsTextEditor()}.  Generally you want this set for a Dialog
 * without text editors, so that it will be placed on top of the current
 * input method UI.  You can modify this behavior by forcing the flag to your
 * desired mode after calling {@link #onCreate}.
 *
 * <div class="special reference">
 * <h3>Developer Guides</h3>
 * <p>For more information about creating dialogs, read the
 * <a href="{@docRoot}guide/topics/ui/dialogs.html">Dialogs</a> developer guide.</p>
 * </div>
 */
public class BottomSheetAlertDialog extends ModifiedBottomSheetDialog implements DialogInterface {

    final AlertController mAlert;

    /**
     * No layout hint.
     */
    static final int LAYOUT_HINT_NONE = 0;

    /**
     * Hint layout to the side.
     */
    static final int LAYOUT_HINT_SIDE = 1;

    protected BottomSheetAlertDialog(@NonNull Context context) {
        this(context, 0);
    }

    /**
     * Construct an AlertDialog that uses an explicit theme.  The actual style
     * that an AlertDialog uses is a private implementation, however you can
     * here supply either the name of an attribute in the theme from which
     * to get the dialog's style (such as {@link R.attr#alertDialogTheme}.
     */
    protected BottomSheetAlertDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, getThemeResId(context, themeResId));
        mAlert = new AlertController(getContext(), this, getWindow());
    }

    protected BottomSheetAlertDialog(@NonNull Context context, boolean cancelable,
		@Nullable OnCancelListener cancelListener) {
        this(context, 0);
        setCancelable(cancelable);
        setOnCancelListener(cancelListener);
    }

    /**
     * Gets one of the buttons used in the dialog. Returns null if the specified
     * button does not exist or the dialog has not yet been fully created (for
     * example, via {@link #show()} or {@link #create()}).
     *
     * @param whichButton The identifier of the button that should be returned.
     *                    For example, this can be
     *                    {@link DialogInterface#BUTTON_POSITIVE}.
     * @return The button from the dialog, or null if a button does not exist.
     */
    public Button getButton(int whichButton) {
        return mAlert.getButton(whichButton);
    }

    /**
     * Gets the list view used in the dialog.
     *
     * @return The {@link ListView} from the dialog.
     */
    public ListView getListView() {
        return mAlert.getListView();
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        mAlert.setTitle(title);
    }

    /**
     * @see Builder#setCustomTitle(View)
     *
     * This method has no effect if called after {@link #show()}.
     */
    public void setCustomTitle(View customTitleView) {
        mAlert.setCustomTitle(customTitleView);
    }

    /**
     * Sets the message to display.
     *
     * @param message The message to display in the dialog.
     */
    public void setMessage(CharSequence message) {
        mAlert.setMessage(message);
    }

    /**
     * Set the view to display in the dialog. This method has no effect if called
     * after {@link #show()}.
     */
    public void setView(View view) {
        mAlert.setView(view);
    }

    /**
     * Set the view to display in the dialog, specifying the spacing to appear around that
     * view.  This method has no effect if called after {@link #show()}.
     *
     * @param view              The view to show in the content area of the dialog
     * @param viewSpacingLeft   Extra space to appear to the left of {@code view}
     * @param viewSpacingTop    Extra space to appear above {@code view}
     * @param viewSpacingRight  Extra space to appear to the right of {@code view}
     * @param viewSpacingBottom Extra space to appear below {@code view}
     */
    public void setView(View view, int viewSpacingLeft, int viewSpacingTop, int viewSpacingRight,
		int viewSpacingBottom) {
        mAlert.setView(view, viewSpacingLeft, viewSpacingTop, viewSpacingRight, viewSpacingBottom);
    }

    /**
     * Internal api to allow hinting for the best button panel layout.
     */
    void setButtonPanelLayoutHint(int layoutHint) {
        mAlert.setButtonPanelLayoutHint(layoutHint);
    }

    /**
     * Sets a message to be sent when a button is pressed. This method has no effect if called
     * after {@link #show()}.
     *
     * @param whichButton Which button to set the message for, can be one of
     *                    {@link DialogInterface#BUTTON_POSITIVE},
     *                    {@link DialogInterface#BUTTON_NEGATIVE}, or
     *                    {@link DialogInterface#BUTTON_NEUTRAL}
     * @param text        The text to display in positive button.
     * @param msg         The {@link Message} to be sent when clicked.
     */
    public void setButton(int whichButton, CharSequence text, Message msg) {
        mAlert.setButton(whichButton, text, null, msg);
    }

    /**
     * Sets a listener to be invoked when the positive button of the dialog is pressed. This method
     * has no effect if called after {@link #show()}.
     *
     * @param whichButton Which button to set the listener on, can be one of
     *                    {@link DialogInterface#BUTTON_POSITIVE},
     *                    {@link DialogInterface#BUTTON_NEGATIVE}, or
     *                    {@link DialogInterface#BUTTON_NEUTRAL}
     * @param text        The text to display in positive button.
     * @param listener    The {@link DialogInterface.OnClickListener} to use.
     */
    public void setButton(int whichButton, CharSequence text, OnClickListener listener) {
        mAlert.setButton(whichButton, text, listener, null);
    }

    /**
     * Set resId to 0 if you don't want an icon.
     * @param resId the resourceId of the drawable to use as the icon or 0
     * if you don't want an icon.
     */
    public void setIcon(int resId) {
        mAlert.setIcon(resId);
    }

    /**
     * Set the {@link Drawable} to be used in the title.
     *
     * @param icon Drawable to use as the icon or null if you don't want an icon.
     */
    public void setIcon(Drawable icon) {
        mAlert.setIcon(icon);
    }

    /**
     * Sets an icon as supplied by a theme attribute. e.g. android.R.attr.alertDialogIcon
     *
     * @param attrId ID of a theme attribute that points to a drawable resource.
     */
    public void setIconAttribute(int attrId) {
        TypedValue out = new TypedValue();
        getContext().getTheme().resolveAttribute(attrId, out, true);
        mAlert.setIcon(out.resourceId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAlert.installContent();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAlert.onKeyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mAlert.onKeyUp(keyCode, event)) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    public static class Builder implements DialogBuilder<Builder,BottomSheetAlertDialog>{
        private final AlertController.AlertParams P;
        private final int mTheme;

        /**
         * Creates a builder for an alert dialog that uses the default alert
         * dialog theme.
         * <p>
         * The default alert dialog theme is defined by
         * {@link android.R.attr#alertDialogTheme} within the parent
         * {@code context}'s theme.
         *
         * @param context the parent context
         */
        public Builder(@NonNull Context context) {
            this(context, getThemeResId(context, 0));
        }

        /**
         * Creates a builder for an alert dialog that uses an explicit theme
         * resource.
         * <p>
         * The specified theme resource ({@code themeResId}) is applied on top
         * of the parent {@code context}'s theme. It may be specified as a
         * style resource containing a fully-populated theme, such as
         * {@link R.style#Theme_AppCompat_Dialog}, to replace all
         * attributes in the parent {@code context}'s theme including primary
         * and accent colors.
         * <p>
         * To preserve attributes such as primary and accent colors, the
         * {@code themeResId} may instead be specified as an overlay theme such
         * as {@link R.style#ThemeOverlay_AppCompat_Dialog}. This will
         * override only the window attributes necessary to style the alert
         * window as a dialog.
         * <p>
         * Alternatively, the {@code themeResId} may be specified as {@code 0}
         * to use the parent {@code context}'s resolved value for
         * {@link android.R.attr#alertDialogTheme}.
         *
         * @param context the parent context
         * @param themeResId the resource ID of the theme against which to inflate
         *                   this dialog, or {@code 0} to use the parent
         *                   {@code context}'s default alert dialog theme
         */
        public Builder(@NonNull Context context, @StyleRes int themeResId) {
            P = new AlertController.AlertParams(new ContextThemeWrapper(
                    context, getThemeResId(context, themeResId)));
            mTheme = themeResId;
        }

        /**
         * Returns a {@link Context} with the appropriate theme for dialogs created by this Builder.
         * Applications should use this Context for obtaining LayoutInflaters for inflating views
         * that will be used in the resulting dialogs, as it will cause views to be inflated with
         * the correct theme.
         *
         * @return A Context for built Dialogs.
         */
        @NonNull
        public Context getContext() {
            return P.mContext;
        }

        /**
         * Set the title using the given resource id.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setTitle(@StringRes int titleId) {
            P.mTitle = P.mContext.getText(titleId);
            return this;
        }

        /**
         * Set the title displayed in the {@link Dialog}.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setTitle(@Nullable CharSequence title) {
            P.mTitle = title;
            return this;
        }

        /**
         * Set the title using the custom view {@code customTitleView}.
         * <p>
         * The methods {@link #setTitle(int)} and {@link #setIcon(int)} should
         * be sufficient for most titles, but this is provided if the title
         * needs more customization. Using this will replace the title and icon
         * set via the other methods.
         * <p>
         * <strong>Note:</strong> To ensure consistent styling, the custom view
         * should be inflated or constructed using the alert dialog's themed
         * context obtained via {@link #getContext()}.
         *
         * @param customTitleView the custom view to use as the title
         * @return this Builder object to allow for chaining of calls to set
         *         methods
         */
        public Builder setCustomTitle(@Nullable View customTitleView) {
            P.mCustomTitleView = customTitleView;
            return this;
        }

        /**
         * Set the message to display using the given resource id.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setMessage(@StringRes int messageId) {
            P.mMessage = P.mContext.getText(messageId);
            return this;
        }

        /**
         * Set the message to display.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setMessage(@Nullable CharSequence message) {
            P.mMessage = message;
            return this;
        }

        /**
         * Set the resource id of the {@link Drawable} to be used in the title.
         * <p>
         * Takes precedence over values set using {@link #setIcon(Drawable)}.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setIcon(@DrawableRes int iconId) {
            P.mIconId = iconId;
            return this;
        }

        /**
         * Set the {@link Drawable} to be used in the title.
         * <p>
         * <strong>Note:</strong> To ensure consistent styling, the drawable
         * should be inflated or constructed using the alert dialog's themed
         * context obtained via {@link #getContext()}.
         *
         * @return this Builder object to allow for chaining of calls to set
         *         methods
         */
        public Builder setIcon(@Nullable Drawable icon) {
            P.mIcon = icon;
            return this;
        }

        /**
         * Set an icon as supplied by a theme attribute. e.g.
         * {@link android.R.attr#alertDialogIcon}.
         * <p>
         * Takes precedence over values set using {@link #setIcon(int)} or
         * {@link #setIcon(Drawable)}.
         *
         * @param attrId ID of a theme attribute that points to a drawable resource.
         */
        public Builder setIconAttribute(@AttrRes int attrId) {
            TypedValue out = new TypedValue();
            P.mContext.getTheme().resolveAttribute(attrId, out, true);
            P.mIconId = out.resourceId;
            return this;
        }

        /**
         * Set a listener to be invoked when the positive button of the dialog is pressed.
         * @param textId The resource id of the text to display in the positive button
         * @param listener The {@link DialogInterface.OnClickListener} to use.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setPositiveButton(@StringRes int textId, final OnClickListener listener) {
            P.mPositiveButtonText = P.mContext.getText(textId);
            P.mPositiveButtonListener = listener;
            return this;
        }

        /**
         * Set a listener to be invoked when the positive button of the dialog is pressed.
         * @param text The text to display in the positive button
         * @param listener The {@link DialogInterface.OnClickListener} to use.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setPositiveButton(CharSequence text, final OnClickListener listener) {
            P.mPositiveButtonText = text;
            P.mPositiveButtonListener = listener;
            return this;
        }

        /**
         * Set a listener to be invoked when the negative button of the dialog is pressed.
         * @param textId The resource id of the text to display in the negative button
         * @param listener The {@link DialogInterface.OnClickListener} to use.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setNegativeButton(@StringRes int textId, final OnClickListener listener) {
            P.mNegativeButtonText = P.mContext.getText(textId);
            P.mNegativeButtonListener = listener;
            return this;
        }

        /**
         * Set a listener to be invoked when the negative button of the dialog is pressed.
         * @param text The text to display in the negative button
         * @param listener The {@link DialogInterface.OnClickListener} to use.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setNegativeButton(CharSequence text, final OnClickListener listener) {
            P.mNegativeButtonText = text;
            P.mNegativeButtonListener = listener;
            return this;
        }

        /**
         * Set a listener to be invoked when the neutral button of the dialog is pressed.
         * @param textId The resource id of the text to display in the neutral button
         * @param listener The {@link DialogInterface.OnClickListener} to use.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setNeutralButton(@StringRes int textId, final OnClickListener listener) {
            P.mNeutralButtonText = P.mContext.getText(textId);
            P.mNeutralButtonListener = listener;
            return this;
        }

        /**
         * Set a listener to be invoked when the neutral button of the dialog is pressed.
         * @param text The text to display in the neutral button
         * @param listener The {@link DialogInterface.OnClickListener} to use.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setNeutralButton(CharSequence text, final OnClickListener listener) {
            P.mNeutralButtonText = text;
            P.mNeutralButtonListener = listener;
            return this;
        }

        /**
         * Sets whether the dialog is cancelable or not.  Default is true.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setCancelable(boolean cancelable) {
            P.mCancelable = cancelable;
            return this;
        }

        /**
         * Sets the callback that will be called if the dialog is canceled.
         *
         * <p>Even in a cancelable dialog, the dialog may be dismissed for reasons other than
         * being canceled or one of the supplied choices being selected.
         * If you are interested in listening for all cases where the dialog is dismissed
         * and not just when it is canceled, see
         * {@link #setOnDismissListener(android.content.DialogInterface.OnDismissListener)
         * setOnDismissListener}.</p>
         *
         * @return This Builder object to allow for chaining of calls to set methods
         * @see #setCancelable(boolean)
         * @see #setOnDismissListener(android.content.DialogInterface.OnDismissListener)
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setOnCancelListener(OnCancelListener onCancelListener) {
            P.mOnCancelListener = onCancelListener;
            return this;
        }

        /**
         * Sets the callback that will be called when the dialog is dismissed for any reason.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setOnDismissListener(OnDismissListener onDismissListener) {
            P.mOnDismissListener = onDismissListener;
            return this;
        }

        /**
         * Sets the callback that will be called if a key is dispatched to the dialog.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setOnKeyListener(OnKeyListener onKeyListener) {
            P.mOnKeyListener = onKeyListener;
            return this;
        }

        /**
         * Set a list of items to be displayed in the dialog as the content, you will be notified of the
         * selected item via the supplied listener. This should be an array type i.e. R.array.foo
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setItems(@ArrayRes int itemsId, final OnClickListener listener) {
            P.mItems = P.mContext.getResources().getTextArray(itemsId);
            P.mOnClickListener = listener;
            return this;
        }

        /**
         * Set a list of items to be displayed in the dialog as the content, you will be notified of the
         * selected item via the supplied listener.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setItems(CharSequence[] items, final OnClickListener listener) {
            P.mItems = items;
            P.mOnClickListener = listener;
            return this;
        }

        /**
         * Set a list of items, which are supplied by the given {@link ListAdapter}, to be
         * displayed in the dialog as the content, you will be notified of the
         * selected item via the supplied listener.
         *
         * @param adapter The {@link ListAdapter} to supply the list of items
         * @param listener The listener that will be called when an item is clicked.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setAdapter(final ListAdapter adapter, final OnClickListener listener) {
            P.mAdapter = adapter;
            P.mOnClickListener = listener;
            return this;
        }

        /**
         * Set a list of items, which are supplied by the given {@link Cursor}, to be
         * displayed in the dialog as the content, you will be notified of the
         * selected item via the supplied listener.
         *
         * @param cursor The {@link Cursor} to supply the list of items
         * @param listener The listener that will be called when an item is clicked.
         * @param labelColumn The column name on the cursor containing the string to display
         *          in the label.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setCursor(final Cursor cursor, final OnClickListener listener,
			String labelColumn) {
            P.mCursor = cursor;
            P.mLabelColumn = labelColumn;
            P.mOnClickListener = listener;
            return this;
        }

        /**
         * Set a list of items to be displayed in the dialog as the content,
         * you will be notified of the selected item via the supplied listener.
         * This should be an array type, e.g. R.array.foo. The list will have
         * a check mark displayed to the right of the text for each checked
         * item. Clicking on an item in the list will not dismiss the dialog.
         * Clicking on a button will dismiss the dialog.
         *
         * @param itemsId the resource id of an array i.e. R.array.foo
         * @param checkedItems specifies which items are checked. It should be null in which case no
         *        items are checked. If non null it must be exactly the same length as the array of
         *        items.
         * @param listener notified when an item on the list is clicked. The dialog will not be
         *        dismissed when an item is clicked. It will only be dismissed if clicked on a
         *        button, if no buttons are supplied it's up to the user to dismiss the dialog.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setMultiChoiceItems(@ArrayRes int itemsId, boolean[] checkedItems,
			final OnMultiChoiceClickListener listener) {
            P.mItems = P.mContext.getResources().getTextArray(itemsId);
            P.mOnCheckboxClickListener = listener;
            P.mCheckedItems = checkedItems;
            P.mIsMultiChoice = true;
            return this;
        }

        /**
         * Set a list of items to be displayed in the dialog as the content,
         * you will be notified of the selected item via the supplied listener.
         * The list will have a check mark displayed to the right of the text
         * for each checked item. Clicking on an item in the list will not
         * dismiss the dialog. Clicking on a button will dismiss the dialog.
         *
         * @param items the text of the items to be displayed in the list.
         * @param checkedItems specifies which items are checked. It should be null in which case no
         *        items are checked. If non null it must be exactly the same length as the array of
         *        items.
         * @param listener notified when an item on the list is clicked. The dialog will not be
         *        dismissed when an item is clicked. It will only be dismissed if clicked on a
         *        button, if no buttons are supplied it's up to the user to dismiss the dialog.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setMultiChoiceItems(CharSequence[] items, boolean[] checkedItems,
			final OnMultiChoiceClickListener listener) {
            P.mItems = items;
            P.mOnCheckboxClickListener = listener;
            P.mCheckedItems = checkedItems;
            P.mIsMultiChoice = true;
            return this;
        }

        /**
         * Set a list of items to be displayed in the dialog as the content,
         * you will be notified of the selected item via the supplied listener.
         * The list will have a check mark displayed to the right of the text
         * for each checked item. Clicking on an item in the list will not
         * dismiss the dialog. Clicking on a button will dismiss the dialog.
         *
         * @param cursor the cursor used to provide the items.
         * @param isCheckedColumn specifies the column name on the cursor to use to determine
         *        whether a checkbox is checked or not. It must return an integer value where 1
         *        means checked and 0 means unchecked.
         * @param labelColumn The column name on the cursor containing the string to display in the
         *        label.
         * @param listener notified when an item on the list is clicked. The dialog will not be
         *        dismissed when an item is clicked. It will only be dismissed if clicked on a
         *        button, if no buttons are supplied it's up to the user to dismiss the dialog.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setMultiChoiceItems(Cursor cursor, String isCheckedColumn, String labelColumn,
			final OnMultiChoiceClickListener listener) {
            P.mCursor = cursor;
            P.mOnCheckboxClickListener = listener;
            P.mIsCheckedColumn = isCheckedColumn;
            P.mLabelColumn = labelColumn;
            P.mIsMultiChoice = true;
            return this;
        }

        /**
         * Set a list of items to be displayed in the dialog as the content, you will be notified of
         * the selected item via the supplied listener. This should be an array type i.e.
         * R.array.foo The list will have a check mark displayed to the right of the text for the
         * checked item. Clicking on an item in the list will not dismiss the dialog. Clicking on a
         * button will dismiss the dialog.
         *
         * @param itemsId the resource id of an array i.e. R.array.foo
         * @param checkedItem specifies which item is checked. If -1 no items are checked.
         * @param listener notified when an item on the list is clicked. The dialog will not be
         *        dismissed when an item is clicked. It will only be dismissed if clicked on a
         *        button, if no buttons are supplied it's up to the user to dismiss the dialog.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setSingleChoiceItems(@ArrayRes int itemsId, int checkedItem,
			final OnClickListener listener) {
            P.mItems = P.mContext.getResources().getTextArray(itemsId);
            P.mOnClickListener = listener;
            P.mCheckedItem = checkedItem;
            P.mIsSingleChoice = true;
            return this;
        }

        /**
         * Set a list of items to be displayed in the dialog as the content, you will be notified of
         * the selected item via the supplied listener. The list will have a check mark displayed to
         * the right of the text for the checked item. Clicking on an item in the list will not
         * dismiss the dialog. Clicking on a button will dismiss the dialog.
         *
         * @param cursor the cursor to retrieve the items from.
         * @param checkedItem specifies which item is checked. If -1 no items are checked.
         * @param labelColumn The column name on the cursor containing the string to display in the
         *        label.
         * @param listener notified when an item on the list is clicked. The dialog will not be
         *        dismissed when an item is clicked. It will only be dismissed if clicked on a
         *        button, if no buttons are supplied it's up to the user to dismiss the dialog.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setSingleChoiceItems(Cursor cursor, int checkedItem, String labelColumn,
			final OnClickListener listener) {
            P.mCursor = cursor;
            P.mOnClickListener = listener;
            P.mCheckedItem = checkedItem;
            P.mLabelColumn = labelColumn;
            P.mIsSingleChoice = true;
            return this;
        }

        /**
         * Set a list of items to be displayed in the dialog as the content, you will be notified of
         * the selected item via the supplied listener. The list will have a check mark displayed to
         * the right of the text for the checked item. Clicking on an item in the list will not
         * dismiss the dialog. Clicking on a button will dismiss the dialog.
         *
         * @param items the items to be displayed.
         * @param checkedItem specifies which item is checked. If -1 no items are checked.
         * @param listener notified when an item on the list is clicked. The dialog will not be
         *        dismissed when an item is clicked. It will only be dismissed if clicked on a
         *        button, if no buttons are supplied it's up to the user to dismiss the dialog.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setSingleChoiceItems(CharSequence[] items, int checkedItem, final OnClickListener listener) {
            P.mItems = items;
            P.mOnClickListener = listener;
            P.mCheckedItem = checkedItem;
            P.mIsSingleChoice = true;
            return this;
        }

        /**
         * Set a list of items to be displayed in the dialog as the content, you will be notified of
         * the selected item via the supplied listener. The list will have a check mark displayed to
         * the right of the text for the checked item. Clicking on an item in the list will not
         * dismiss the dialog. Clicking on a button will dismiss the dialog.
         *
         * @param adapter The {@link ListAdapter} to supply the list of items
         * @param checkedItem specifies which item is checked. If -1 no items are checked.
         * @param listener notified when an item on the list is clicked. The dialog will not be
         *        dismissed when an item is clicked. It will only be dismissed if clicked on a
         *        button, if no buttons are supplied it's up to the user to dismiss the dialog.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setSingleChoiceItems(ListAdapter adapter, int checkedItem, final OnClickListener listener) {
            P.mAdapter = adapter;
            P.mOnClickListener = listener;
            P.mCheckedItem = checkedItem;
            P.mIsSingleChoice = true;
            return this;
        }

        /**
         * Sets a listener to be invoked when an item in the list is selected.
         *
         * @param listener the listener to be invoked
         * @return this Builder object to allow for chaining of calls to set methods
         * @see AdapterView#setOnItemSelectedListener(android.widget.AdapterView.OnItemSelectedListener)
         */
        public Builder setOnItemSelectedListener(final AdapterView.OnItemSelectedListener listener) {
            P.mOnItemSelectedListener = listener;
            return this;
        }

        /**
         * Set a custom view resource to be the contents of the Dialog. The
         * resource will be inflated, adding all top-level views to the screen.
         *
         * @param layoutResId Resource ID to be inflated.
         * @return this Builder object to allow for chaining of calls to set
         *         methods
         */
        public Builder setView(int layoutResId) {
            P.mView = null;
            P.mViewLayoutResId = layoutResId;
            P.mViewSpacingSpecified = false;
            return this;
        }

        /**
         * Sets a custom view to be the contents of the alert dialog.
         * <p>
         * When using a pre-Holo theme, if the supplied view is an instance of
         * a {@link ListView} then the light background will be used.
         * <p>
         * <strong>Note:</strong> To ensure consistent styling, the custom view
         * should be inflated or constructed using the alert dialog's themed
         * context obtained via {@link #getContext()}.
         *
         * @param view the view to use as the contents of the alert dialog
         * @return this Builder object to allow for chaining of calls to set
         *         methods
         */
        public Builder setView(View view) {
            P.mView = view;
            P.mViewLayoutResId = 0;
            P.mViewSpacingSpecified = false;
            return this;
        }

        /**
         * Set a custom view to be the contents of the Dialog, specifying the
         * spacing to appear around that view. If the supplied view is an
         * instance of a {@link ListView} the light background will be used.
         *
         * @param view              The view to use as the contents of the Dialog.
         * @param viewSpacingLeft   Spacing between the left edge of the view and
         *                          the dialog frame
         * @param viewSpacingTop    Spacing between the top edge of the view and
         *                          the dialog frame
         * @param viewSpacingRight  Spacing between the right edge of the view
         *                          and the dialog frame
         * @param viewSpacingBottom Spacing between the bottom edge of the view
         *                          and the dialog frame
         * @return This Builder object to allow for chaining of calls to set
         * methods
         *
         *
         * This is currently hidden because it seems like people should just
         * be able to put padding around the view.
         * @hide
         */
        
        @Deprecated
        public Builder setView(View view, int viewSpacingLeft, int viewSpacingTop,
			int viewSpacingRight, int viewSpacingBottom) {
            P.mView = view;
            P.mViewLayoutResId = 0;
            P.mViewSpacingSpecified = true;
            P.mViewSpacingLeft = viewSpacingLeft;
            P.mViewSpacingTop = viewSpacingTop;
            P.mViewSpacingRight = viewSpacingRight;
            P.mViewSpacingBottom = viewSpacingBottom;
            return this;
        }

        /**
         * Sets the Dialog to use the inverse background, regardless of what the
         * contents is.
         *
         * @param useInverseBackground Whether to use the inverse background
         * @return This Builder object to allow for chaining of calls to set methods
         * @deprecated This flag is only used for pre-Material themes. Instead,
         *             specify the window background using on the alert dialog
         *             theme.
         */
        @Deprecated
        public Builder setInverseBackgroundForced(boolean useInverseBackground) {
            P.mForceInverseBackground = useInverseBackground;
            return this;
        }

        /**
         * @hide
         */
        
        public Builder setRecycleOnMeasureEnabled(boolean enabled) {
            P.mRecycleOnMeasure = enabled;
            return this;
        }


        /**
         * Creates an {@link AlertDialog} with the arguments supplied to this
         * builder.
         * <p>
         * Calling this method does not display the dialog. If no additional
         * processing is needed, {@link #show()} may be called instead to both
         * create and display the dialog.
         */
        public BottomSheetAlertDialog create() {
            // We can't use Dialog's 3-arg constructor with the createThemeContextWrapper param,
            // so we always have to re-set the theme
            final BottomSheetAlertDialog dialog = new BottomSheetAlertDialog(P.mContext, mTheme);
            P.apply(dialog.mAlert);
            dialog.setCancelable(P.mCancelable);
            if (P.mCancelable) {
                dialog.setCanceledOnTouchOutside(true);
            }
            dialog.setOnCancelListener(P.mOnCancelListener);
            dialog.setOnDismissListener(P.mOnDismissListener);
            if (P.mOnKeyListener != null) {
                dialog.setOnKeyListener(P.mOnKeyListener);
            }
            return dialog;
        }

        /**
         * Creates an {@link AlertDialog} with the arguments supplied to this
         * builder and immediately displays the dialog.
         * <p>
         * Calling this method is functionally identical to:
         * <pre>
         *     AlertDialog dialog = builder.create();
         *     dialog.show();
         * </pre>
         */
        public BottomSheetAlertDialog show() {
            final BottomSheetAlertDialog dialog = create();
            dialog.show();
            return dialog;
        }
    }

	
	
	static class AlertController {

		/**
		 * No layout hint.
		 */
		static final int LAYOUT_HINT_NONE = 0;

		/**
		 * Hint layout to the side.
		 */
		static final int LAYOUT_HINT_SIDE = 1;



		private final Context mContext;
		final AppCompatDialog mDialog;
		private final Window mWindow;

		private CharSequence mTitle;
		private CharSequence mMessage;
		ListView mListView;
		private View mView;

		private int mViewLayoutResId;

		private int mViewSpacingLeft;
		private int mViewSpacingTop;
		private int mViewSpacingRight;
		private int mViewSpacingBottom;
		private boolean mViewSpacingSpecified = false;

		Button mButtonPositive;
		private CharSequence mButtonPositiveText;
		Message mButtonPositiveMessage;

		Button mButtonNegative;
		private CharSequence mButtonNegativeText;
		Message mButtonNegativeMessage;

		Button mButtonNeutral;
		private CharSequence mButtonNeutralText;
		Message mButtonNeutralMessage;

		NestedScrollView mScrollView;

		private int mIconId = 0;
		private Drawable mIcon;

		private ImageView mIconView;
		private TextView mTitleView;
		private TextView mMessageView;
		private View mCustomTitleView;

		ListAdapter mAdapter;

		int mCheckedItem = -1;

		private int mAlertDialogLayout;
		private int mButtonPanelSideLayout;
		int mListLayout;
		int mMultiChoiceItemLayout;
		int mSingleChoiceItemLayout;
		int mListItemLayout;

		private int mButtonPanelLayoutHint = LAYOUT_HINT_NONE;

		Handler mHandler;

		private final View.OnClickListener mButtonHandler = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Message m;
				if (v == mButtonPositive && mButtonPositiveMessage != null) {
					m = Message.obtain(mButtonPositiveMessage);
				} else if (v == mButtonNegative && mButtonNegativeMessage != null) {
					m = Message.obtain(mButtonNegativeMessage);
				} else if (v == mButtonNeutral && mButtonNeutralMessage != null) {
					m = Message.obtain(mButtonNeutralMessage);
				} else {
					m = null;
				}

				if (m != null) {
					m.sendToTarget();
				}

				// Post a message so we dismiss after the above handlers are executed
				mHandler.obtainMessage(ButtonHandler.MSG_DISMISS_DIALOG, mDialog)
					.sendToTarget();
			}
		};

		private static final class ButtonHandler extends Handler {
			// Button clicks have Message.what as the BUTTON{1,2,3} constant
			private static final int MSG_DISMISS_DIALOG = 1;

			private WeakReference<DialogInterface> mDialog;

			public ButtonHandler(DialogInterface dialog) {
				mDialog = new WeakReference<>(dialog);
			}

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {

					case DialogInterface.BUTTON_POSITIVE:
					case DialogInterface.BUTTON_NEGATIVE:
					case DialogInterface.BUTTON_NEUTRAL:
					((DialogInterface.OnClickListener) msg.obj).onClick(mDialog.get(), msg.what);
					break;

					case MSG_DISMISS_DIALOG:
					((DialogInterface) msg.obj).dismiss();
				}
			}
		}

		public AlertController(Context context, AppCompatDialog di, Window window) {
			mContext = context;
			mDialog = di;
			mWindow = window;
			mHandler = new ButtonHandler(di);

			final TypedArray a = context.obtainStyledAttributes(null, R.styleable.AlertDialog,
				R.attr.alertDialogStyle, 0);

			mAlertDialogLayout = a.getResourceId(R.styleable.AlertDialog_android_layout, 0);
			mButtonPanelSideLayout = a.getResourceId(R.styleable.AlertDialog_buttonPanelSideLayout, 0);

			mListLayout = a.getResourceId(R.styleable.AlertDialog_listLayout, 0);
			mMultiChoiceItemLayout = a.getResourceId(R.styleable.AlertDialog_multiChoiceItemLayout, 0);
			mSingleChoiceItemLayout = a
				.getResourceId(R.styleable.AlertDialog_singleChoiceItemLayout, 0);
			mListItemLayout = a.getResourceId(R.styleable.AlertDialog_listItemLayout, 0);

			a.recycle();

			/* We use a custom title so never request a window title */
			di.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
		}

		static boolean canTextInput(View v) {
			if (v.onCheckIsTextEditor()) {
				return true;
			}

			if (!(v instanceof ViewGroup)) {
				return false;
			}

			ViewGroup vg = (ViewGroup) v;
			int i = vg.getChildCount();
			while (i > 0) {
				i--;
				v = vg.getChildAt(i);
				if (canTextInput(v)) {
					return true;
				}
			}

			return false;
		}

		public void installContent() {
			final int contentView = selectContentView();
			mDialog.setContentView(contentView);
			setupView();
		}

		private int selectContentView() {
			if (mButtonPanelSideLayout == 0) {
				return mAlertDialogLayout;
			}
			if (mButtonPanelLayoutHint == LAYOUT_HINT_SIDE) {
				return mButtonPanelSideLayout;
			}
			return mAlertDialogLayout;
		}

		public void setTitle(CharSequence title) {
			mTitle = title;
			if (mTitleView != null) {
				mTitleView.setText(title);
			}
		}

		/**
		 * @see AlertDialog.Builder#setCustomTitle(View)
		 */
		public void setCustomTitle(View customTitleView) {
			mCustomTitleView = customTitleView;
		}

		public void setMessage(CharSequence message) {
			mMessage = message;
			if (mMessageView != null) {
				mMessageView.setText(message);
			}
		}

		/**
		 * Set the view resource to display in the dialog.
		 */
		public void setView(int layoutResId) {
			mView = null;
			mViewLayoutResId = layoutResId;
			mViewSpacingSpecified = false;
		}

		/**
		 * Set the view to display in the dialog.
		 */
		public void setView(View view) {
			mView = view;
			mViewLayoutResId = 0;
			mViewSpacingSpecified = false;
		}

		/**
		 * Set the view to display in the dialog along with the spacing around that view
		 */
		public void setView(View view, int viewSpacingLeft, int viewSpacingTop, int viewSpacingRight,
			int viewSpacingBottom) {
			mView = view;
			mViewLayoutResId = 0;
			mViewSpacingSpecified = true;
			mViewSpacingLeft = viewSpacingLeft;
			mViewSpacingTop = viewSpacingTop;
			mViewSpacingRight = viewSpacingRight;
			mViewSpacingBottom = viewSpacingBottom;
		}

		/**
		 * Sets a hint for the best button panel layout.
		 */
		public void setButtonPanelLayoutHint(int layoutHint) {
			mButtonPanelLayoutHint = layoutHint;
		}

		/**
		 * Sets a click listener or a message to be sent when the button is clicked.
		 * You only need to pass one of {@code listener} or {@code msg}.
		 *
		 * @param whichButton Which button, can be one of
		 *                    {@link DialogInterface#BUTTON_POSITIVE},
		 *                    {@link DialogInterface#BUTTON_NEGATIVE}, or
		 *                    {@link DialogInterface#BUTTON_NEUTRAL}
		 * @param text        The text to display in positive button.
		 * @param listener    The {@link DialogInterface.OnClickListener} to use.
		 * @param msg         The {@link Message} to be sent when clicked.
		 */
		public void setButton(int whichButton, CharSequence text,
			DialogInterface.OnClickListener listener, Message msg) {

			if (msg == null && listener != null) {
				msg = mHandler.obtainMessage(whichButton, listener);
			}

			switch (whichButton) {

				case DialogInterface.BUTTON_POSITIVE:
				mButtonPositiveText = text;
				mButtonPositiveMessage = msg;
				break;

				case DialogInterface.BUTTON_NEGATIVE:
				mButtonNegativeText = text;
				mButtonNegativeMessage = msg;
				break;

				case DialogInterface.BUTTON_NEUTRAL:
				mButtonNeutralText = text;
				mButtonNeutralMessage = msg;
				break;

				default:
				throw new IllegalArgumentException("Button does not exist");
			}
		}

		/**
		 * Specifies the icon to display next to the alert title.
		 *
		 * @param resId the resource identifier of the drawable to use as the icon,
		 *              or 0 for no icon
		 */
		public void setIcon(int resId) {
			mIcon = null;
			mIconId = resId;

			if (mIconView != null) {
				if (resId != 0) {
					mIconView.setVisibility(View.VISIBLE);
					mIconView.setImageResource(mIconId);
				} else {
					mIconView.setVisibility(View.GONE);
				}
			}
		}

		/**
		 * Specifies the icon to display next to the alert title.
		 *
		 * @param icon the drawable to use as the icon or null for no icon
		 */
		public void setIcon(Drawable icon) {
			mIcon = icon;
			mIconId = 0;

			if (mIconView != null) {
				if (icon != null) {
					mIconView.setVisibility(View.VISIBLE);
					mIconView.setImageDrawable(icon);
				} else {
					mIconView.setVisibility(View.GONE);
				}
			}
		}

		/**
		 * @param attrId the attributeId of the theme-specific drawable
		 *               to resolve the resourceId for.
		 *
		 * @return resId the resourceId of the theme-specific drawable
		 */
		public int getIconAttributeResId(int attrId) {
			TypedValue out = new TypedValue();
			mContext.getTheme().resolveAttribute(attrId, out, true);
			return out.resourceId;
		}

		public ListView getListView() {
			return mListView;
		}

		public Button getButton(int whichButton) {
			switch (whichButton) {
				case DialogInterface.BUTTON_POSITIVE:
				return mButtonPositive;
				case DialogInterface.BUTTON_NEGATIVE:
				return mButtonNegative;
				case DialogInterface.BUTTON_NEUTRAL:
				return mButtonNeutral;
				default:
				return null;
			}
		}

		@SuppressWarnings({"UnusedDeclaration"})
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			return mScrollView != null && mScrollView.executeKeyEvent(event);
		}

		@SuppressWarnings({"UnusedDeclaration"})
		public boolean onKeyUp(int keyCode, KeyEvent event) {
			return mScrollView != null && mScrollView.executeKeyEvent(event);
		}

		/**
		 * Resolves whether a custom or default panel should be used. Removes the
		 * default panel if a custom panel should be used. If the resolved panel is
		 * a view stub, inflates before returning.
		 *
		 * @param customPanel the custom panel
		 * @param defaultPanel the default panel
		 * @return the panel to use
		 */
		@Nullable
		private ViewGroup resolvePanel(@Nullable View customPanel, @Nullable View defaultPanel) {
			if (customPanel == null) {
				// Inflate the default panel, if needed.
				if (defaultPanel instanceof ViewStub) {
					defaultPanel = ((ViewStub) defaultPanel).inflate();
				}

				return (ViewGroup) defaultPanel;
			}

			// Remove the default panel entirely.
			if (defaultPanel != null) {
				final ViewParent parent = defaultPanel.getParent();
				if (parent instanceof ViewGroup) {
					((ViewGroup) parent).removeView(defaultPanel);
				}
			}

			// Inflate the custom panel, if needed.
			if (customPanel instanceof ViewStub) {
				customPanel = ((ViewStub) customPanel).inflate();
			}

			return (ViewGroup) customPanel;
		}

		private void setupView() {
			final View parentPanel = mWindow.findViewById(R.id.parentPanel);
			final View defaultTopPanel = parentPanel.findViewById(R.id.topPanel);
			final View defaultContentPanel = parentPanel.findViewById(R.id.contentPanel);
			final View defaultButtonPanel = parentPanel.findViewById(R.id.buttonPanel);

			// Install custom content before setting up the title or buttons so
			// that we can handle panel overrides.
			final ViewGroup customPanel = (ViewGroup) parentPanel.findViewById(R.id.customPanel);
			setupCustomContent(customPanel);

			final View customTopPanel = customPanel.findViewById(R.id.topPanel);
			final View customContentPanel = customPanel.findViewById(R.id.contentPanel);
			final View customButtonPanel = customPanel.findViewById(R.id.buttonPanel);

			// Resolve the correct panels and remove the defaults, if needed.
			final ViewGroup topPanel = resolvePanel(customTopPanel, defaultTopPanel);
			final ViewGroup contentPanel = resolvePanel(customContentPanel, defaultContentPanel);
			final ViewGroup buttonPanel = resolvePanel(customButtonPanel, defaultButtonPanel);

			setupContent(contentPanel);
			setupButtons(buttonPanel);
			setupTitle(topPanel);

			final boolean hasCustomPanel = customPanel != null
				&& customPanel.getVisibility() != View.GONE;
			final boolean hasTopPanel = topPanel != null
				&& topPanel.getVisibility() != View.GONE;
			final boolean hasButtonPanel = buttonPanel != null
				&& buttonPanel.getVisibility() != View.GONE;

			// Only display the text spacer if we don't have buttons.
			if (!hasButtonPanel) {
				if (contentPanel != null) {
					final View spacer = contentPanel.findViewById(R.id.textSpacerNoButtons);
					if (spacer != null) {
						spacer.setVisibility(View.VISIBLE);
					}
				}
			}

			if (hasTopPanel) {
				// Only clip scrolling content to padding if we have a title.
				if (mScrollView != null) {
					mScrollView.setClipToPadding(true);
				}
			}

			// Update scroll indicators as needed.
			if (!hasCustomPanel) {
				final View content = mListView != null ? mListView : mScrollView;
				if (content != null) {
					final int indicators = (hasTopPanel ? ViewCompat.SCROLL_INDICATOR_TOP : 0)
						| (hasButtonPanel ? ViewCompat.SCROLL_INDICATOR_BOTTOM : 0);
					setScrollIndicators(contentPanel, content, indicators,
						ViewCompat.SCROLL_INDICATOR_TOP | ViewCompat.SCROLL_INDICATOR_BOTTOM);
				}
			}

			final ListView listView = mListView;
			if (listView != null && mAdapter != null) {
				listView.setAdapter(mAdapter);
				final int checkedItem = mCheckedItem;
				if (checkedItem > -1) {
					listView.setItemChecked(checkedItem, true);
					listView.setSelection(checkedItem);
				}
			}
		}

		private void setScrollIndicators(ViewGroup contentPanel, View content,
			final int indicators, final int mask) {
			// Set up scroll indicators (if present).
			View indicatorUp = mWindow.findViewById(R.id.scrollIndicatorUp);
			View indicatorDown = mWindow.findViewById(R.id.scrollIndicatorDown);

			if (Build.VERSION.SDK_INT >= 23) {
				// We're on Marshmallow so can rely on the View APIs
				ViewCompat.setScrollIndicators(content, indicators, mask);
				// We can also remove the compat indicator views
				if (indicatorUp != null) {
					contentPanel.removeView(indicatorUp);
				}
				if (indicatorDown != null) {
					contentPanel.removeView(indicatorDown);
				}
			} else {
				// First, remove the indicator views if we're not set to use them
				if (indicatorUp != null && (indicators & ViewCompat.SCROLL_INDICATOR_TOP) == 0) {
					contentPanel.removeView(indicatorUp);
					indicatorUp = null;
				}
				if (indicatorDown != null && (indicators & ViewCompat.SCROLL_INDICATOR_BOTTOM) == 0) {
					contentPanel.removeView(indicatorDown);
					indicatorDown = null;
				}

				if (indicatorUp != null || indicatorDown != null) {
					final View top = indicatorUp;
					final View bottom = indicatorDown;

					if (mMessage != null) {
						// We're just showing the ScrollView, set up listener.
						mScrollView.setOnScrollChangeListener(
                                (NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> manageScrollIndicators(v, top, bottom));
						// Set up the indicators following layout.
						mScrollView.post(() -> manageScrollIndicators(mScrollView, top, bottom));
					} else if (mListView != null) {
						// We're just showing the AbsListView, set up listener.
						mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
								@Override
								public void onScrollStateChanged(AbsListView view, int scrollState) {}

								@Override
								public void onScroll(AbsListView v, int firstVisibleItem,
									int visibleItemCount, int totalItemCount) {
									manageScrollIndicators(v, top, bottom);
								}
							});
						// Set up the indicators following layout.
						mListView.post(() -> manageScrollIndicators(mListView, top, bottom));
					} else {
						// We don't have any content to scroll, remove the indicators.
						if (top != null) {
							contentPanel.removeView(top);
						}
						if (bottom != null) {
							contentPanel.removeView(bottom);
						}
					}
				}
			}
		}

		private void setupCustomContent(ViewGroup customPanel) {
			final View customView;
			if (mView != null) {
				customView = mView;
			} else if (mViewLayoutResId != 0) {
				final LayoutInflater inflater = LayoutInflater.from(mContext);
				customView = inflater.inflate(mViewLayoutResId, customPanel, false);
			} else {
				customView = null;
			}

			final boolean hasCustomView = customView != null;
			if (!hasCustomView || !canTextInput(customView)) {
				mWindow.setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
					WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
			}

			if (hasCustomView) {
				final FrameLayout custom = (FrameLayout) mWindow.findViewById(R.id.custom);
				custom.addView(customView, new LayoutParams(MATCH_PARENT, MATCH_PARENT));

				if (mViewSpacingSpecified) {
					custom.setPadding(
						mViewSpacingLeft, mViewSpacingTop, mViewSpacingRight, mViewSpacingBottom);
				}

				if (mListView != null) {
					((LinearLayout.LayoutParams) customPanel.getLayoutParams()).weight = 0;
				}
			} else {
				customPanel.setVisibility(View.GONE);
			}
		}

		private void setupTitle(ViewGroup topPanel) {
			if (mCustomTitleView != null) {
				// Add the custom title view directly to the topPanel layout
				LayoutParams lp = new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

				topPanel.addView(mCustomTitleView, 0, lp);

				// Hide the title template
				View titleTemplate = mWindow.findViewById(R.id.title_template);
				titleTemplate.setVisibility(View.GONE);
			} else {
				mIconView = (ImageView) mWindow.findViewById(android.R.id.icon);

				final boolean hasTextTitle = !TextUtils.isEmpty(mTitle);
				if (hasTextTitle) {
					// Display the title if a title is supplied, else hide it.
					mTitleView = (TextView) mWindow.findViewById(R.id.alertTitle);
					mTitleView.setText(mTitle);

					// Do this last so that if the user has supplied any icons we
					// use them instead of the default ones. If the user has
					// specified 0 then make it disappear.
					if (mIconId != 0) {
						mIconView.setImageResource(mIconId);
					} else if (mIcon != null) {
						mIconView.setImageDrawable(mIcon);
					} else {
						// Apply the padding from the icon to ensure the title is
						// aligned correctly.
						mTitleView.setPadding(mIconView.getPaddingLeft(),
							mIconView.getPaddingTop(),
							mIconView.getPaddingRight(),
							mIconView.getPaddingBottom());
						mIconView.setVisibility(View.GONE);
					}
				} else {
					// Hide the title template
					final View titleTemplate = mWindow.findViewById(R.id.title_template);
					titleTemplate.setVisibility(View.GONE);
					mIconView.setVisibility(View.GONE);
					topPanel.setVisibility(View.GONE);
				}
			}
		}

		private void setupContent(ViewGroup contentPanel) {
			mScrollView = (NestedScrollView) mWindow.findViewById(R.id.scrollView);
			mScrollView.setFocusable(false);
			mScrollView.setNestedScrollingEnabled(false);

			// Special case for users that only want to display a String
			mMessageView = (TextView) contentPanel.findViewById(android.R.id.message);
			if (mMessageView == null) {
				return;
			}

			if (mMessage != null) {
				mMessageView.setText(mMessage);
			} else {
				mMessageView.setVisibility(View.GONE);
				mScrollView.removeView(mMessageView);

				if (mListView != null) {
					final ViewGroup scrollParent = (ViewGroup) mScrollView.getParent();
					final int childIndex = scrollParent.indexOfChild(mScrollView);
					scrollParent.removeViewAt(childIndex);
					scrollParent.addView(mListView, childIndex,
						new LayoutParams(MATCH_PARENT, MATCH_PARENT));
				} else {
					contentPanel.setVisibility(View.GONE);
				}
			}
		}

		static void manageScrollIndicators(View v, View upIndicator, View downIndicator) {
			if (upIndicator != null) {
				upIndicator.setVisibility(
					ViewCompat.canScrollVertically(v, -1) ? View.VISIBLE : View.INVISIBLE);
			}
			if (downIndicator != null) {
				downIndicator.setVisibility(
					ViewCompat.canScrollVertically(v, 1) ? View.VISIBLE : View.INVISIBLE);
			}
		}

		private void setupButtons(ViewGroup buttonPanel) {
			int BIT_BUTTON_POSITIVE = 1;
			int BIT_BUTTON_NEGATIVE = 2;
			int BIT_BUTTON_NEUTRAL = 4;
			int whichButtons = 0;
			mButtonPositive = (Button) buttonPanel.findViewById(android.R.id.button1);
			mButtonPositive.setOnClickListener(mButtonHandler);

			if (TextUtils.isEmpty(mButtonPositiveText)) {
				mButtonPositive.setVisibility(View.GONE);
			} else {
				mButtonPositive.setText(mButtonPositiveText);
				mButtonPositive.setVisibility(View.VISIBLE);
				whichButtons = whichButtons | BIT_BUTTON_POSITIVE;
			}

			mButtonNegative = (Button) buttonPanel.findViewById(android.R.id.button2);
			mButtonNegative.setOnClickListener(mButtonHandler);

			if (TextUtils.isEmpty(mButtonNegativeText)) {
				mButtonNegative.setVisibility(View.GONE);
			} else {
				mButtonNegative.setText(mButtonNegativeText);
				mButtonNegative.setVisibility(View.VISIBLE);

				whichButtons = whichButtons | BIT_BUTTON_NEGATIVE;
			}

			mButtonNeutral = (Button) buttonPanel.findViewById(android.R.id.button3);
			mButtonNeutral.setOnClickListener(mButtonHandler);

			if (TextUtils.isEmpty(mButtonNeutralText)) {
				mButtonNeutral.setVisibility(View.GONE);
			} else {
				mButtonNeutral.setText(mButtonNeutralText);
				mButtonNeutral.setVisibility(View.VISIBLE);

				whichButtons = whichButtons | BIT_BUTTON_NEUTRAL;
			}

			final boolean hasButtons = whichButtons != 0;
			if (!hasButtons) {
				buttonPanel.setVisibility(View.GONE);
			}
		}

		public static class AlertParams {
			public final Context mContext;
			public final LayoutInflater mInflater;

			public int mIconId = 0;
			public Drawable mIcon;
			public int mIconAttrId = 0;
			public CharSequence mTitle;
			public View mCustomTitleView;
			public CharSequence mMessage;
			public CharSequence mPositiveButtonText;
			public DialogInterface.OnClickListener mPositiveButtonListener;
			public CharSequence mNegativeButtonText;
			public DialogInterface.OnClickListener mNegativeButtonListener;
			public CharSequence mNeutralButtonText;
			public DialogInterface.OnClickListener mNeutralButtonListener;
			public boolean mCancelable;
			public DialogInterface.OnCancelListener mOnCancelListener;
			public DialogInterface.OnDismissListener mOnDismissListener;
			public DialogInterface.OnKeyListener mOnKeyListener;
			public CharSequence[] mItems;
			public ListAdapter mAdapter;
			public DialogInterface.OnClickListener mOnClickListener;
			public int mViewLayoutResId;
			public View mView;
			public int mViewSpacingLeft;
			public int mViewSpacingTop;
			public int mViewSpacingRight;
			public int mViewSpacingBottom;
			public boolean mViewSpacingSpecified = false;
			public boolean[] mCheckedItems;
			public boolean mIsMultiChoice;
			public boolean mIsSingleChoice;
			public int mCheckedItem = -1;
			public DialogInterface.OnMultiChoiceClickListener mOnCheckboxClickListener;
			public Cursor mCursor;
			public String mLabelColumn;
			public String mIsCheckedColumn;
			public boolean mForceInverseBackground;
			public AdapterView.OnItemSelectedListener mOnItemSelectedListener;
			public OnPrepareListViewListener mOnPrepareListViewListener;
			public boolean mRecycleOnMeasure = true;

			/**
			 * Interface definition for a callback to be invoked before the ListView
			 * will be bound to an adapter.
			 */
			public interface OnPrepareListViewListener {

				/**
				 * Called before the ListView is bound to an adapter.
				 * @param listView The ListView that will be shown in the dialog.
				 */
				void onPrepareListView(ListView listView);
			}

			public AlertParams(Context context) {
				mContext = context;
				mCancelable = true;
				mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			}

			public void apply(AlertController dialog) {
				if (mCustomTitleView != null) {
					dialog.setCustomTitle(mCustomTitleView);
				} else {
					if (mTitle != null) {
						dialog.setTitle(mTitle);
					}
					if (mIcon != null) {
						dialog.setIcon(mIcon);
					}
					if (mIconId != 0) {
						dialog.setIcon(mIconId);
					}
					if (mIconAttrId != 0) {
						dialog.setIcon(dialog.getIconAttributeResId(mIconAttrId));
					}
				}
				if (mMessage != null) {
					dialog.setMessage(mMessage);
				}
				if (mPositiveButtonText != null) {
					dialog.setButton(DialogInterface.BUTTON_POSITIVE, mPositiveButtonText,
						mPositiveButtonListener, null);
				}
				if (mNegativeButtonText != null) {
					dialog.setButton(DialogInterface.BUTTON_NEGATIVE, mNegativeButtonText,
						mNegativeButtonListener, null);
				}
				if (mNeutralButtonText != null) {
					dialog.setButton(DialogInterface.BUTTON_NEUTRAL, mNeutralButtonText,
						mNeutralButtonListener, null);
				}
				// For a list, the client can either supply an array of items or an
				// adapter or a cursor
				if ((mItems != null) || (mCursor != null) || (mAdapter != null)) {
					createListView(dialog);
				}
				if (mView != null) {
					if (mViewSpacingSpecified) {
						dialog.setView(mView, mViewSpacingLeft, mViewSpacingTop, mViewSpacingRight,
							mViewSpacingBottom);
					} else {
						dialog.setView(mView);
					}
				} else if (mViewLayoutResId != 0) {
					dialog.setView(mViewLayoutResId);
				}

				/*
				 dialog.setCancelable(mCancelable);
				 dialog.setOnCancelListener(mOnCancelListener);
				 if (mOnKeyListener != null) {
				 dialog.setOnKeyListener(mOnKeyListener);
				 }
				 */
			}

			private void createListView(final AlertController dialog) {
				final ListView listView = (ListView) mInflater.inflate(dialog.mListLayout, null);
				final ListAdapter adapter;

				if (mIsMultiChoice) {
					if (mCursor == null) {
						adapter = new ArrayAdapter<CharSequence>(
							mContext, dialog.mMultiChoiceItemLayout, android.R.id.text1, mItems) {
							@Override
							public View getView(int position, View convertView, ViewGroup parent) {
								View view = super.getView(position, convertView, parent);
								if (mCheckedItems != null) {
									boolean isItemChecked = mCheckedItems[position];
									if (isItemChecked) {
										listView.setItemChecked(position, true);
									}
								}
								return view;
							}
						};
					} else {
						adapter = new CursorAdapter(mContext, mCursor, false) {
							private final int mLabelIndex;
							private final int mIsCheckedIndex;

							{
								final Cursor cursor = getCursor();
								mLabelIndex = cursor.getColumnIndexOrThrow(mLabelColumn);
								mIsCheckedIndex = cursor.getColumnIndexOrThrow(mIsCheckedColumn);
							}

							@Override
							public void bindView(View view, Context context, Cursor cursor) {
								CheckedTextView text = (CheckedTextView) view.findViewById(
									android.R.id.text1);
								text.setText(cursor.getString(mLabelIndex));
								listView.setItemChecked(cursor.getPosition(),
									cursor.getInt(mIsCheckedIndex) == 1);
							}

							@Override
							public View newView(Context context, Cursor cursor, ViewGroup parent) {
								return mInflater.inflate(dialog.mMultiChoiceItemLayout,
									parent, false);
							}

						};
					}
				} else {
					final int layout;
					if (mIsSingleChoice) {
						layout = dialog.mSingleChoiceItemLayout;
					} else {
						layout = dialog.mListItemLayout;
					}

					if (mCursor != null) {
						adapter = new SimpleCursorAdapter(mContext, layout, mCursor,
							new String[] { mLabelColumn }, new int[] { android.R.id.text1 });
					} else if (mAdapter != null) {
						adapter = mAdapter;
					} else {
						adapter = new CheckedItemAdapter(mContext, layout, android.R.id.text1, mItems);
					}
				}

				if (mOnPrepareListViewListener != null) {
					mOnPrepareListViewListener.onPrepareListView(listView);
				}

				/* Don't directly set the adapter on the ListView as we might
				 * want to add a footer to the ListView later.
				 */
				dialog.mAdapter = adapter;
				dialog.mCheckedItem = mCheckedItem;

				if (mOnClickListener != null) {
					listView.setOnItemClickListener((parent, v, position, id) -> {
                        mOnClickListener.onClick(dialog.mDialog, position);
                        if (!mIsSingleChoice) {
                            dialog.mDialog.dismiss();
                        }
                    });
				} else if (mOnCheckboxClickListener != null) {
					listView.setOnItemClickListener((parent, v, position, id) -> {
                        if (mCheckedItems != null) {
                            mCheckedItems[position] = listView.isItemChecked(position);
                        }
                        mOnCheckboxClickListener.onClick(
                            dialog.mDialog, position, listView.isItemChecked(position));
                    });
				}

				// Attach a given OnItemSelectedListener to the ListView
				if (mOnItemSelectedListener != null) {
					listView.setOnItemSelectedListener(mOnItemSelectedListener);
				}

				if (mIsSingleChoice) {
					listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				} else if (mIsMultiChoice) {
					listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
				}
				dialog.mListView = listView;
			}
		}

		private static class CheckedItemAdapter extends ArrayAdapter<CharSequence> {
			public CheckedItemAdapter(Context context, int resource, int textViewResourceId,
				CharSequence[] objects) {
				super(context, resource, textViewResourceId, objects);
			}

			@Override
			public boolean hasStableIds() {
				return true;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}
		}
	}
}
