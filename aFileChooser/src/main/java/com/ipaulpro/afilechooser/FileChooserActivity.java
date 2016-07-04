/*
 * Copyright (C) 2013 Paul Burke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ipaulpro.afilechooser;

import android.support.v7.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import android.support.v7.app.*;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;

/**
 * Main Activity that handles the FileListFragments
 *
 * @version 2013-06-25
 * @author paulburke (ipaulpro)
 */
public class FileChooserActivity extends AppCompatActivity implements
        OnBackStackChangedListener, FileListFragment.Callbacks {

    public static final String PATH = "path";
    public static final File EXTERNAL_BASE_PATH = Environment
            .getExternalStorageDirectory();

    private static final boolean HAS_ACTIONBAR = true;//AppCompat shows an ActionBar

    private FragmentManager mFragmentManager;
    private BroadcastReceiver mStorageListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, R.string.storage_removed, Toast.LENGTH_LONG).show();
            finishWithResult(null);
        }
    };

    protected File mPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.addOnBackStackChangedListener(this);

        if (savedInstanceState == null) {
			if(getIntent().hasExtra(PATH)){
				mPath=new File(getIntent().getStringExtra(PATH)).getAbsoluteFile();
			}else{
				mPath = EXTERNAL_BASE_PATH;
			}
            addFragment();
        } else {
            mPath = new File(savedInstanceState.getString(PATH)).getAbsoluteFile();
        }

        setTitle(mPath.toString());
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterStorageListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerStorageListener();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(PATH, mPath.toString());
    }

    @Override
    public void onBackStackChanged() {

        int count = mFragmentManager.getBackStackEntryCount();
        if (count > 0) {
            BackStackEntry fragment = mFragmentManager.getBackStackEntryAt(count - 1);
            mPath = new File(fragment.getName()).getAbsoluteFile();
        } else {
            mPath = EXTERNAL_BASE_PATH;
        }

        setTitle(mPath.toString());
        if (HAS_ACTIONBAR)
            invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (HAS_ACTIONBAR) {
            boolean hasBackStack = !mPath.equals(EXTERNAL_BASE_PATH);

            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(hasBackStack);
            actionBar.setHomeButtonEnabled(hasBackStack);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
				replaceFragment(mPath.getParentFile());
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Add the initial Fragment with given path.
     */
    private void addFragment() {
        FileListFragment fragment = FileListFragment.newInstance(mPath.toString());
        mFragmentManager.beginTransaction()
                .add(android.R.id.content, fragment).commit();
    }

    /**
     * "Replace" the existing Fragment with a new one using given path. We're
     * really adding a Fragment to the back stack.
     *
     * @param file The file (directory) to display.
     */
    private void replaceFragment(File file) {
        mPath = file;

        FileListFragment fragment = FileListFragment.newInstance(mPath.toString());
        mFragmentManager.beginTransaction()
                .replace(android.R.id.content, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(mPath.toString()).commit();
    }

    /**
     * Finish this Activity with a result code and URI of the selected file.
     *
     * @param file The file selected.
     */
    private void finishWithResult(File file) {
        if (file != null) {
            Uri uri = Uri.fromFile(file);
            setResult(RESULT_OK, new Intent().setData(uri).putExtra("path",file.toString()));
            finish();
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    /**
     * Called when the user selects a File
     *
     * @param file The file that was selected
     */
    @Override
    public void onFileSelected(File file) {
        if (file != null) {
            if (file.isDirectory()) {
                replaceFragment(file);
            } else {
                finishWithResult(file);
            }
        } else {
            Toast.makeText(FileChooserActivity.this, R.string.error_selecting_file,
                    Toast.LENGTH_SHORT).show();
        }
    }
	
    /**
     * Register the external storage BroadcastReceiver.
     */
    private void registerStorageListener() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        registerReceiver(mStorageListener, filter);
    }

    /**
     * Unregister the external storage BroadcastReceiver.
     */
    private void unregisterStorageListener() {
        unregisterReceiver(mStorageListener);
    }
	
	@Override
	public void onBackPressed() {
		// TODO: Implement this method
		boolean hasBackStack = !mPath.equals(EXTERNAL_BASE_PATH);
		if(hasBackStack){
			replaceFragment(mPath.getParentFile());
		}else{
			finishWithResult(null);
		}
	}
	
	protected Presenter getPresenter(){
		if(getApplication() instanceof Presenter){
			return (Presenter)getApplication();
		}else{
			return null;
		}
	}
	
	

	public Drawable getTintedDrawable(int res,int color){
		return getTintedDrawable(res,color,this);
	}

	public static Drawable getTintedDrawable(int res,int color,Context ctx){
		Drawable d=ctx.getResources().getDrawable(res);
		d=DrawableCompat.wrap(d);
		DrawableCompat.setTint(d,color);
		return d;
	}
}
