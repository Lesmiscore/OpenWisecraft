package com.nao20010128nao.Wisecraft.misc;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContextWrapper;
import com.nao20010128nao.Wisecraft.R;

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
		waitDialog = new ProgressDialog(this);
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
}
