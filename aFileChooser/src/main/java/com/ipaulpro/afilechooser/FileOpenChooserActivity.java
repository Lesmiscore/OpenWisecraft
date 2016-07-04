package com.ipaulpro.afilechooser;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.support.v4.view.MenuItemCompat;
import android.os.Bundle;
import java.io.File;

public class FileOpenChooserActivity extends FileChooserActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		if(getIntent().hasExtra("path")){
			File f=new File(getIntent().getStringExtra("path"));
			if(f.isFile())f=f.getParentFile();
			getIntent().putExtra("path",f.toString());
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO: Implement this method
		SubMenu addFile=menu.addSubMenu(0,4,0,R.string.create).setIcon(getPresenter().isLightTheme(this)?R.drawable.ic_action_new_light:R.drawable.ic_action_new_dark);
		addFile.add(0,5,1,R.string.add_file).setIcon(getTintedDrawable(com.nao20010128nao.MaterialIcons.R.drawable.ic_description_black_48dp,getPresenter().isLightTheme(this)?0xff_666666:0xff_ffffff));
		addFile.add(0,6,2,R.string.add_dir).setIcon(getPresenter().isLightTheme(this)?R.drawable.ic_action_collection_light:R.drawable.ic_action_collection_dark);
		MenuItemCompat.setShowAsAction(addFile.getItem(),MenuItem.SHOW_AS_ACTION_ALWAYS);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO: Implement this method
		return super.onOptionsItemSelected(item);
	}
}
