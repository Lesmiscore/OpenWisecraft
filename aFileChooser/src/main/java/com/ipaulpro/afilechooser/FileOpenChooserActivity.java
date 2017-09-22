package com.ipaulpro.afilechooser;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.support.v4.view.MenuItemCompat;
import android.os.Bundle;
import java.io.File;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.content.DialogInterface;
import android.widget.EditText;
import java.io.IOException;

public class FileOpenChooserActivity extends FileChooserActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if(getIntent().hasExtra("path")){
			File f=new File(getIntent().getStringExtra("path"));
			if(f.isFile())f=f.getParentFile();
			getIntent().putExtra("path",f.toString());
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		SubMenu addFile=menu.addSubMenu(0,4,0,R.string.create).setIcon(getTintedDrawable(R.drawable.ic_add_black_48dp,getPresenter().isLightTheme(this)?0xff_666666:0xff_ffffff));
		addFile.add(0,5,1,R.string.add_file).setIcon(getTintedDrawable(R.drawable.ic_insert_drive_file_black_48dp,getPresenter().isLightTheme(this)?0xff_666666:0xff_ffffff));
		addFile.add(0,6,2,R.string.add_dir).setIcon(getTintedDrawable(R.drawable.ic_folder_black_48dp,getPresenter().isLightTheme(this)?0xff_666666:0xff_ffffff));
		MenuItemCompat.setShowAsAction(addFile.getItem(),MenuItem.SHOW_AS_ACTION_ALWAYS);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case 5:
				View v=getLayoutInflater().inflate(R.layout.add_file,null);
				final EditText et= v.findViewById(R.id.filename);
				new AlertDialog.Builder(this,getPresenter().getDialogStyleId())
					.setView(v)
					.setTitle(R.string.add_file)
					.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface di,int w){
							boolean succeed;
							try {
								succeed=new File(mPath, et.getText().toString()).createNewFile();
							} catch (IOException e) {
								succeed=false;
							}
							if(!succeed){
								getPresenter().showSelfMessage(FileOpenChooserActivity.this,R.string.failed_add_file,Presenter.MESSAGE_SHOW_LENGTH_SHORT);
								return;
							}
						}
					})
					.setNeutralButton(R.string.open,new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface di,int w){
							boolean succeed;
							File f=new File(mPath, et.getText().toString());
							try {
								succeed=f.createNewFile();
							} catch (IOException e) {
								succeed=false;
							}
							if(!succeed){
								getPresenter().showSelfMessage(FileOpenChooserActivity.this,R.string.failed_add_file,Presenter.MESSAGE_SHOW_LENGTH_SHORT);
								return;
							}
							finishWithResult(f);
						}
					})
					.show();
				break;
			case 6:
				View v_=getLayoutInflater().inflate(R.layout.add_file,null);
				final EditText et_= v_.findViewById(R.id.filename);
				new AlertDialog.Builder(this,getPresenter().getDialogStyleId())
					.setView(v_)
					.setTitle(R.string.add_dir)
					.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface di,int w){
							boolean succeed=new File(mPath, et_.getText().toString()).mkdirs();
							if(!succeed){
								getPresenter().showSelfMessage(FileOpenChooserActivity.this,R.string.failed_add_dir,Presenter.MESSAGE_SHOW_LENGTH_SHORT);
								return;
							}
						}
					})
					.show();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}
