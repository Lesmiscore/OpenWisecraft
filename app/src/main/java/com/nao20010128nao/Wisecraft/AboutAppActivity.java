package com.nao20010128nao.Wisecraft;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.preference.*;
import android.view.*;
import com.nao20010128nao.Wisecraft.misc.*;

public class AboutAppActivity extends AboutAppActivityBase
{
	static Typeface icons;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		Utils.getActionBarTextView(this).setTextColor(Color.WHITE);
		//34
		addCardAt(R.layout.about_app_2,0);//234
		addCardAt(R.layout.about_app_1,0);//1234
		addCard(R.layout.about_app_5);//12345
        addCard(R.layout.about_app_6);//123456
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
        findViewById(R.id.gotoWisecraftTools).setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    Intent intent=new Intent(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse(getResources().getString(R.string.wisecraftToolsUrl)));
                    startActivity(Intent.createChooser(intent,getResources().getString(R.string.gotoWisecraftTools)));
                }
			});
	}
}
