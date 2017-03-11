package com.nao20010128nao.Wisecraft.misc;
import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import com.nao20010128nao.Wisecraft.*;

import android.support.v7.app.ActionBar;
import com.nao20010128nao.Wisecraft.misc.compat.R;

public class WorkingDialog extends ContextWrapper {
	ProgressDialog waitDialog;
	public WorkingDialog(Activity c) {
		super(c);
	}
	public void showWorkingDialog() {
		showWorkingDialog(getResources().getString(R.string.working));
	}
	public void showWorkingDialog(String message) {
		if (waitDialog != null) {
			hideWorkingDialog();
		}
		waitDialog = new AppCompatProgressDialog(this){
			
			@Override
			public void onBackPressed() {
				/* no-op */
			}

			@Override
			public void setTitle(CharSequence title) {
				/* no-op */
			}

			@Override
			public void setTitle(int titleId) {
				/* no-op */
			}

			@Override
			public void setCustomTitle(View customTitleView) {
				/* no-op */
			}
		};
		waitDialog.setIndeterminate(true);
		waitDialog.setMessage(message);
		waitDialog.setCancelable(false);
		waitDialog.show();
	}
	public void hideWorkingDialog() {
		if (waitDialog == null) {
			return;
		}
		waitDialog.cancel();
		waitDialog = null;
	}
	
	class AppCompatProgressDialog extends ProgressDialog{
		AppCompatDelegate dlg;
		public AppCompatProgressDialog(Context ctx){
			super(ctx);
			dlg=AppCompatDelegate.create(this,null);
		}


		@Override
		protected void onCreate(Bundle savedInstanceState) {
			dlg.installViewFactory();
			dlg.onCreate(savedInstanceState);
			dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
			super.onCreate(savedInstanceState);
		}
		
		public ActionBar getSupportActionBar() {
			return dlg.getSupportActionBar();
		}

		public void setSupportActionBar(Toolbar toolbar) {
			dlg.setSupportActionBar(toolbar);
		}

		@Override
		public void setContentView(int layoutResID) {
			dlg.setContentView(layoutResID);
		}

		@Override
		public void setContentView(View view) {
			dlg.setContentView(view);
		}

		@Override
		public void setContentView(View view, ViewGroup.LayoutParams params) {
			dlg.setContentView(view, params);
		}

		@Override
		public void addContentView(View view, ViewGroup.LayoutParams params) {
			dlg.addContentView(view, params);
		}

		@Override
		protected void onStop() {
			super.onStop();
			dlg.onStop();
		}

		public void invalidateOptionsMenu() {
			dlg.invalidateOptionsMenu();
		}
	}
}
