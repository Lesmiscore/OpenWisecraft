package com.nao20010128nao.Wisecraft.misc.contextwrappers.colored_text;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import com.nao20010128nao.Wisecraft.R;

class CalligraphyFactory {

    private static final String ACTION_BAR_TITLE = "action_bar_title";
    private static final String ACTION_BAR_SUBTITLE = "action_bar_subtitle";

	private int color;
	
    /**
     * Some styles are in sub styles, such as actionBarTextStyle etc..
     *
     * @param view view to check.
     * @return 2 element array, default to -1 unless a style has been found.
     */
    protected static int[] getStyleForTextView(TextView view) {
        final int[] styleIds = new int[]{-1, -1};
        // Try to find the specific actionbar styles
        if (isActionBarTitle(view)) {
            styleIds[0] = android.R.attr.actionBarStyle;
            styleIds[1] = android.R.attr.titleTextStyle;
        } else if (isActionBarSubTitle(view)) {
            styleIds[0] = android.R.attr.actionBarStyle;
            styleIds[1] = android.R.attr.subtitleTextStyle;
        }
        if (styleIds[0] == -1) {
            // Use TextAppearance as default style
            styleIds[0] = android.R.attr.textAppearance;
        }
        return styleIds;
    }

    /**
     * An even dirtier way to see if the TextView is part of the ActionBar
     *
     * @param view TextView to check is Title
     * @return true if it is.
     */
    @SuppressLint("NewApi")
    protected static boolean isActionBarTitle(TextView view) {
        if (matchesResourceIdName(view, ACTION_BAR_TITLE)) return true;
        if (parentIsToolbarV7(view)) {
            final android.support.v7.widget.Toolbar parent = (android.support.v7.widget.Toolbar) view.getParent();
            return TextUtils.equals(parent.getTitle(), view.getText());
        }
        return false;
    }

    /**
     * An even dirtier way to see if the TextView is part of the ActionBar
     *
     * @param view TextView to check is Title
     * @return true if it is.
     */
    @SuppressLint("NewApi")
    protected static boolean isActionBarSubTitle(TextView view) {
        if (matchesResourceIdName(view, ACTION_BAR_SUBTITLE)) return true;
        if (parentIsToolbarV7(view)) {
            final android.support.v7.widget.Toolbar parent = (android.support.v7.widget.Toolbar) view.getParent();
            return TextUtils.equals(parent.getSubtitle(), view.getText());
        }
        return false;
    }

    protected static boolean parentIsToolbarV7(View view) {
        return true && view.getParent() != null && (view.getParent() instanceof android.support.v7.widget.Toolbar);
    }

    /**
     * Use to match a view against a potential view id. Such as ActionBar title etc.
     *
     * @param view    not null view you want to see has resource matching name.
     * @param matches not null resource name to match against. Its not case sensitive.
     * @return true if matches false otherwise.
     */
    protected static boolean matchesResourceIdName(View view, String matches) {
        if (view.getId() == View.NO_ID) return false;
        final String resourceEntryName = view.getResources().getResourceEntryName(view.getId());
        return resourceEntryName.equalsIgnoreCase(matches);
    }

    private final int[] mAttributeId;

    public CalligraphyFactory(int attributeId, int textColor) {
        this.mAttributeId = new int[]{attributeId};
		this.color=textColor;
    }

    /**
     * Handle the created view
     *
     * @param view    nullable.
     * @param context shouldn't be null.
     * @param attrs   shouldn't be null.
     * @return null if null is passed in.
     */

    public View onViewCreated(View view, Context context, AttributeSet attrs) {
        if (view != null && view.getTag(R.id.calligraphy_tag_id) != Boolean.TRUE) {
            onViewCreatedInternal(view, context, attrs);
            view.setTag(R.id.calligraphy_tag_id, Boolean.TRUE);
        }
        return view;
    }

    void onViewCreatedInternal(View view, final Context context, AttributeSet attrs) {
        if (view instanceof TextView) {
            
		}

        // AppCompat API21+ The ActionBar doesn't inflate default Title/SubTitle, we need to scan the
        // Toolbar(Which underlies the ActionBar) for its children.
        if (true && view instanceof android.support.v7.widget.Toolbar) {
            final Toolbar toolbar = (Toolbar) view;
            toolbar.getViewTreeObserver().addOnGlobalLayoutListener(new ToolbarLayoutListener(this, context, toolbar));
        }

        // Try to set typeface for custom views using interface method or via reflection if available
        if (true) {
            final Method setTypeface = ReflectionUtils.getMethod(view.getClass(), "setTextColor");
            if (setTypeface != null) {
                ReflectionUtils.invokeMethod(view, setTypeface, color);
            }
        }

    }

    private static class ToolbarLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {

        static String BLANK = " ";

        private final WeakReference<CalligraphyFactory> mCalligraphyFactory;
        private final WeakReference<Context> mContextRef;
        private final WeakReference<Toolbar> mToolbarReference;
        private final CharSequence originalSubTitle;

        private ToolbarLayoutListener(final CalligraphyFactory calligraphyFactory,
                                      final Context context, Toolbar toolbar) {
            mCalligraphyFactory = new WeakReference<>(calligraphyFactory);
            mContextRef = new WeakReference<>(context);
            mToolbarReference = new WeakReference<>(toolbar);
            originalSubTitle = toolbar.getSubtitle();
            toolbar.setSubtitle(BLANK);
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override public void onGlobalLayout() {
            final Toolbar toolbar = mToolbarReference.get();
            final Context context = mContextRef.get();
            final CalligraphyFactory factory = mCalligraphyFactory.get();
            if (toolbar == null) return;
            if (factory == null || context == null) {
                removeSelf(toolbar);
                return;
            }

            int childCount = toolbar.getChildCount();
            if (childCount != 0) {
                // Process children, defer draw as it has set the typeface.
                for (int i = 0; i < childCount; i++) {
                    factory.onViewCreated(toolbar.getChildAt(i), context, null);
                }
            }
            removeSelf(toolbar);
            toolbar.setSubtitle(originalSubTitle);
        }

        private void removeSelf(final Toolbar toolbar) {// Our dark deed is done
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                //noinspection deprecation
                toolbar.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            } else {
                toolbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        }

    }

}

