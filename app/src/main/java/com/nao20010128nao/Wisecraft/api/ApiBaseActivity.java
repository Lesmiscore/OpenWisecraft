package com.nao20010128nao.Wisecraft.api;
import android.content.*;
import android.os.*;
import android.support.v7.app.*;
import com.nao20010128nao.Wisecraft.*;

public abstract class ApiBaseActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
	}
	@Override
	protected void attachBaseContext(Context newBase) {
        TheApplication.instance.initForActivities();
		super.attachBaseContext(TheApplication.injectContextSpecial(newBase));
	}
}
