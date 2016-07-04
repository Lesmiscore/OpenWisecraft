package com.ipaulpro.afilechooser;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.support.v4.view.MenuItemCompat;

public class FileOpenChooserActivity extends FileChooserActivity
{

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO: Implement this method
		SubMenu addFile=menu.addSubMenu(0,4,0,R.string.create).setIcon(getPresenter().isLightTheme(this)?R.drawable.ic_action_new_light:R.drawable.ic_action_new_dark);
		addFile.add(0,5,1,R.string.add_file).setIcon(getTintedDrawable(R.drawable.ic_description_black_48dp,getPresenter().isLightTheme(this)?0xff_666666:0xff_ffffff));
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
