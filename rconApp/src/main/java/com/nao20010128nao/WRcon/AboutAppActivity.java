package com.nao20010128nao.WRcon;
import android.content.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import android.graphics.*;

public class AboutAppActivity extends AboutAppActivityBase
{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addCardAt(R.layout.about_app_2_local,0);
		addCardAt(R.layout.about_app_1_local,0);
		findViewById(R.id.gotoTranslationPage).setOnClickListener(new View.OnClickListener(){
				public void onClick(View v){
					Intent intent=new Intent(Intent.ACTION_VIEW);
					intent.addCategory(Intent.CATEGORY_DEFAULT);
					intent.setData(Uri.parse(getResources().getString(R.string.aboutAppTranslationUrl)));
					startActivity(Intent.createChooser(intent,getResources().getString(R.string.gotoTranslationPage)));
				}
			});
		new Handler().post(new Runnable(){
				public void run(){
					TextView tv=CompatUtils.getActionBarTextView(CompatUtils.getToolbar(AboutAppActivity.this));
					if(tv!=null)tv.setTextColor(Color.WHITE);
				}
			});
	}
}
