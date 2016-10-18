package com.nao20010128nao.Wisecraft.misc.compat;
import android.app.ActivityGroup;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuInflater;
import android.view.View;

public class CompatActivityGroup extends ActivityGroup
{
	AppCompatDelegate dlg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		dlg=AppCompatDelegate.create(this,null);
		dlg.installViewFactory();
		dlg.onCreate(savedInstanceState);
		super.onCreate(savedInstanceState);
	}

	public ActionBar getSupportActionBar(){
		return dlg.getSupportActionBar();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		dlg.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dlg.onDestroy();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		dlg.onPostCreate(savedInstanceState);
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		dlg.onPostResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		dlg.onSaveInstanceState(outState);
	}

	@Override
	protected void onStop() {
		super.onStop();
		dlg.onStop();
	}

	@Override
	public MenuInflater getMenuInflater() {
		return dlg.getMenuInflater();
	}

	@Override
	protected void onTitleChanged(CharSequence title, int color) {
		super.onTitleChanged(title, color);
		dlg.setTitle(title);
	}

	@Override
	public void invalidateOptionsMenu() {
		super.invalidateOptionsMenu();
		dlg.invalidateOptionsMenu();
	}

	@Override
	public void setContentView(View view) {
		dlg.setContentView(view);
	}

	@Override
	public void setContentView(int layoutResID) {
		dlg.setContentView(layoutResID);
	}
}
