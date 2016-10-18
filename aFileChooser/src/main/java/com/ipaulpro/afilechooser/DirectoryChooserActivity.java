package com.ipaulpro.afilechooser;
import android.view.*;
import java.io.*;
import android.support.v4.view.*;
import android.net.*;
import android.content.*;
import android.os.*;

public class DirectoryChooserActivity extends FileChooserActivity
{
	File lastDir;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		lastDir=mPath;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem select=menu.add(Menu.NONE,0,0,R.string.selectDir).setIcon(getTintedDrawable(R.drawable.ic_check_black_48dp,getPresenter().isLightTheme(this)?0xff_666666:0xff_ffffff));
		MenuItemCompat.setShowAsAction(select,MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case 0:
				finishWithResult(lastDir);
				return true;
			case android.R.id.home:
				lastDir=lastDir.getParentFile();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onFileSelected(File file) {
		// Ignore selection
		if(file.isDirectory()){
			lastDir=file;
			super.onFileSelected(file);
		}
	}
}
