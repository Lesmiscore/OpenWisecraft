package com.nao20010128nao.Wisecraft.misc;

import android.app.*;
import android.graphics.*;
import android.os.*;
import android.support.v7.app.*;

public abstract class WisecraftBaseActivity extends AppCompatActivity 
{
	
	public void setTaskDescription(CompatTaskDescription taskDesc){
		if(Build.VERSION.SDK_INT>=22){
			setTaskDescription(
				new ActivityManager.TaskDescription(
					taskDesc.getLabel(),
					taskDesc.getIcon(),
					taskDesc.getPrimaryColor()
				)
			);
		}
	}
	
	public static class CompatTaskDescription{
		private String mLabel;
        private Bitmap mIcon;
        private int mColorPrimary;
        
        public CompatTaskDescription(String label, Bitmap icon, int colorPrimary) {
            this(label, icon, colorPrimary, null);
            if ((colorPrimary != 0) && (Color.alpha(colorPrimary) != 255)) {
                throw new RuntimeException("A TaskDescription's primary color should be opaque");
            }
        }
        private CompatTaskDescription(String label, Bitmap icon, int colorPrimary,
			Void identifier) {
            mLabel = label;
            mIcon = icon;
            mColorPrimary = colorPrimary;
        }
        public String getLabel() {
            return mLabel;
        }
        public Bitmap getIcon() {
            return mIcon;
        }
        public int getPrimaryColor() {
            return mColorPrimary;
        }
	}
}
	
