package com.nao20010128nao.WRcon;
import com.nao20010128nao.Wisecraft.AboutAppActivityBase;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.net.Uri;

public class AboutAppActivity extends AboutAppActivityBase
{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addCardAt(R.layout.about_app_2_local,0);
		addCardAt(R.layout.about_app_1_local,0);
		findViewById(R.id.gotoTranslationPage).setOnClickListener(v -> {
            Intent intent=new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse(getResources().getString(R.string.aboutAppTranslationUrl)));
            startActivity(Intent.createChooser(intent,getResources().getString(R.string.gotoTranslationPage)));
        });
	}
}
