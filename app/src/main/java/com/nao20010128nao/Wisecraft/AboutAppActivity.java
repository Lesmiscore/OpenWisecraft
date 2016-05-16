package com.nao20010128nao.Wisecraft;
import android.widget.*;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import com.nao20010128nao.TESTAPP.ScrollingActivity;

public class AboutAppActivity extends ScrollingActivity
{
	static Typeface icons;
	
	ImageView logo;
	PopupWindow pw;
	Button twitter,googleplus,youtube;
	TextView noCm1,noCm2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("useBright",false)){
			setTheme(R.style.AppTheme_Bright_OpenSource);
			getTheme().applyStyle(R.style.AppTheme_Bright_OpenSource,true);
		}
		super.onCreate(savedInstanceState);
		if(icons==null)
			icons=Typeface.createFromAsset(getAssets(),"icomoon2.ttf");
		setContentView(R.layout.about_app);
		logo=(ImageView)getWindow().getDecorView().findViewById(R.id.pickaxe);
		findViewById(R.id.gotoTranslationPage).setOnClickListener(new View.OnClickListener(){
				public void onClick(View v){
					Intent intent=new Intent(Intent.ACTION_VIEW);
					intent.addCategory(Intent.CATEGORY_DEFAULT);
					intent.setData(Uri.parse(getResources().getString(R.string.aboutAppTranslationUrl)));
					startActivity(Intent.createChooser(intent,getResources().getString(R.string.gotoTranslationPage)));
				}
			});
		findViewById(R.id.gotoBugReport).setOnClickListener(new View.OnClickListener(){
				public void onClick(View v){
					Intent intent=new Intent(Intent.ACTION_VIEW);
					intent.addCategory(Intent.CATEGORY_DEFAULT);
					intent.setData(Uri.parse(getResources().getString(R.string.bugReportUrl)));
					startActivity(Intent.createChooser(intent,getResources().getString(R.string.gotoBugReport)));
				}
			});
		{
			twitter=(Button)findViewById(R.id.twitter);
			twitter.setTypeface(icons);
			twitter.setTextColor(0xff1da1f2);
			twitter.setText(String.valueOf((char)0xe906));
			
			twitter.setOnClickListener(new View.OnClickListener(){
					public void onClick(View v){
						openUrlForDeveloper(getResources().getString(R.string.nao20010128naoTwitterLink));
					}
				});
		}
		{
			youtube=(Button)findViewById(R.id.youtube);
			youtube.setTypeface(icons);
			youtube.setTextColor(0xffe73128);
			  youtube.setText(String.valueOf((char)0xe900));//Player icon (larger triangle)
			//youtube.setText(String.valueOf((char)0xe901));//YouTube with frame
			//youtube.setText(String.valueOf((char)0xe902));//simple YouTube
			//youtube.setText(String.valueOf((char)0xe907));//Player icon (smaller triangle)
			//youtube.setText(String.valueOf((char)0xe908));//horizontal YouTube
			
			youtube.setOnClickListener(new View.OnClickListener(){
					public void onClick(View v){
						openUrlForDeveloper(getResources().getString(R.string.nao20010128naoYouTubeLink));
					}
				});
		}
		{
			googleplus=(Button)findViewById(R.id.googleplus);
			googleplus.setTypeface(icons);
			googleplus.setTextColor(0xffdd5044);
			googleplus.setText(String.valueOf((char)0xe903));
			  googleplus.setText(String.valueOf((char)0xe904));//Google+ without frame
			//googleplus.setText(String.valueOf((char)0xe905));//Google+ with frame (like older Google+ app icon)
			
			googleplus.setOnClickListener(new View.OnClickListener(){
					public void onClick(View v){
						openUrlForDeveloper(getResources().getString(R.string.nao20010128naoGooglePlusLink));
					}
				});
		}
		{
			noCm1=(TextView)findViewById(R.id.nonCommercial1);
			noCm2=(TextView)findViewById(R.id.nonCommercial2);
			noCm1.setTypeface(icons);noCm2.setTypeface(icons);
			noCm1.setText(String.valueOf((char)0xe909));
			noCm2.setText(String.valueOf((char)0xe90a));
		}
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
	
	public void openUrlForDeveloper(String url){
		Intent intent=new Intent(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setData(Uri.parse(url));
		startActivity(Intent.createChooser(intent,getResources().getString(R.string.aboutDeveloper)));
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
