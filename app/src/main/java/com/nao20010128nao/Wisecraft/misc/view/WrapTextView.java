package com.nao20010128nao.Wisecraft.misc.view;

import android.text.*;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.TextView;

public class WrapTextView  extends AppCompatTextView {

    private CharSequence mOrgText = "";
    private BufferType mOrgBufferType = BufferType.NORMAL;

    public WrapTextView(Context context) {
        super(context);
        setFilters(new InputFilter[] { new WrapTextViewFilter(this) });
    }

    public WrapTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFilters(new InputFilter[] { new WrapTextViewFilter(this) });
    }

    public WrapTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFilters(new InputFilter[] { new WrapTextViewFilter(this) });
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
							int bottom) {
        setText(mOrgText, mOrgBufferType);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        mOrgText = text;
        mOrgBufferType = type;
        super.setText(text, type);
    }

    @Override
    public CharSequence getText() {
        return mOrgText;
    }

    @Override
    public int length() {
        return mOrgText.length();
    }
	
	public class WrapTextViewFilter implements InputFilter {
		private final TextView view;

		public WrapTextViewFilter(TextView view) {
			this.view = view;
		}

		//@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			TextPaint paint = view.getPaint();
			int w = view.getWidth();
			int wpl = view.getCompoundPaddingLeft();
			int wpr = view.getCompoundPaddingRight();
			int width = w - wpl - wpr;

			SpannableStringBuilder result = new SpannableStringBuilder();
			for (int index = start; index < end; index++) {

				if (Layout.getDesiredWidth(source, start, index + 1, paint) > width) {
					result.append(source.subSequence(start, index));
					result.append("\n");
					start = index;

				} else if (source.charAt(index) == '\n') {
					result.append(source.subSequence(start, index));
					start = index;
				}
			}

			if (start < end) {
				result.append(source.subSequence(start, end));
			}
			return result;
		}
	}
}
