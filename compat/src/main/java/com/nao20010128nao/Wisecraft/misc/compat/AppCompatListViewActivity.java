package com.nao20010128nao.Wisecraft.misc.compat;
import android.app.ListActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.content.res.Configuration;
import android.support.v7.app.ActionBar;

public class AppCompatListViewActivity extends ListActivity
{
	AppCompatDelegate dlg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_lv);
		dlg=AppCompatDelegate.create(this,null);
		dlg.onCreate(savedInstanceState);
		
	}

	public ActionBar getSupportActionBar(){
		return dlg.getSupportActionBar();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO: Implement this method
		super.onConfigurationChanged(newConfig);
		dlg.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onDestroy() {
		// TODO: Implement this method
		super.onDestroy();
		dlg.onDestroy();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onPostCreate(savedInstanceState);
		dlg.onPostCreate(savedInstanceState);
	}

	@Override
	protected void onPostResume() {
		// TODO: Implement this method
		super.onPostResume();
		dlg.onPostResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO: Implement this method
		super.onSaveInstanceState(outState);
		dlg.onSaveInstanceState(outState);
	}

	@Override
	protected void onStop() {
		// TODO: Implement this method
		super.onStop();
		dlg.onStop();
	}
}
