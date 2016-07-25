package com.nao20010128nao.Wisecraft;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.preference.*;
import android.support.design.widget.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.nao20010128nao.TESTAPP.*;
import android.support.v7.widget.Toolbar;
import com.nao20010128nao.Wisecraft.misc.Utils;

public class AboutAppActivity extends AboutAppActivityBase
{
	static Typeface icons;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("useBright",false)){
			setTheme(R.style.AppTheme_Bright_OpenSource);
			getTheme().applyStyle(R.style.AppTheme_Bright_OpenSource,true);
		}
		super.onCreate(savedInstanceState);
		Utils.getActionBarTextView(this).setTextColor(Color.WHITE);
		//34
		addCardAt(R.layout.about_app_2,0);//234
		addCardAt(R.layout.about_app_1,0);//1234
		addCard(R.layout.about_app_5);//12345
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
	}
}
