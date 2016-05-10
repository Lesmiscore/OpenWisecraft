package com.nao20010128nao.Wisecraft;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import com.nao20010128nao.TESTAPP.ScrollingActivity;

public class AboutAppActivity extends ScrollingActivity
{
	ImageView logo;
	PopupWindow pw;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("useBright",false)){
			setTheme(R.style.AppTheme_Bright_OpenSource);
			getTheme().applyStyle(R.style.AppTheme_Bright_OpenSource,true);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_app);
		logo=(ImageView)getWindow().getDecorView().findViewById(R.id.pickaxe);
		findViewById(R.id.gotoTranslationPage).setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				Intent intent=new Intent(Intent.ACTION_VIEW);
				intent.addCategory(Intent.CATEGORY_DEFAULT);
				intent.setData(Uri.parse(getResources().getString(R.string.aboutAppTranslationUrl)));
				startActivity(Intent.createChooser(intent,getResources().getString(R.string.share)));
			}
		});
	}

	@Override
	protected int getLayoutResId() {
		// TODO: Implement this method
		return R.layout.about_app_decor;
	}
	
	public int getStatusBarHeight(){
        final Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.top;
    }
	
	public static class LogoImageViewBehavior extends CoordinatorLayout.Behavior<ImageView>{
		boolean mIsAnimating;
		AboutAppActivity ctx;
		public LogoImageViewBehavior(Context context, AttributeSet attrs) {
			super();
			ctx=(AboutAppActivity)context;
		}
	}
}
