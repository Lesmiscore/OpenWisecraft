package com.nao20010128nao.Wisecraft.misc;

import android.app.*;
import android.graphics.*;
import android.os.*;
import android.support.v7.app.*;

public abstract class WisecraftBaseActivity extends AppCompatActivity {

    public void setShowBackButton(boolean value) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(value);
        getSupportActionBar().setDisplayShowHomeEnabled(value);
    }

    public void setTaskDescription(CompatTaskDescription taskDesc) {
        if (Build.VERSION.SDK_INT >= 21) {
            setTaskDescription(
                new ActivityManager.TaskDescription(
                    taskDesc.getLabel(),
                    taskDesc.getIcon(),
                    taskDesc.getPrimaryColor()
                )
            );
        }
    }

    public static class CompatTaskDescription {
        private String mLabel;
        private Bitmap mIcon;
        private int mColorPrimary;

        public CompatTaskDescription(String label, Bitmap icon, int colorPrimary) {
            if ((colorPrimary != 0) && (Color.alpha(colorPrimary) != 255)) {
                throw new RuntimeException("A TaskDescription's primary color should be opaque");
            }
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

