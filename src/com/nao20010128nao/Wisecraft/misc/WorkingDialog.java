package com.nao20010128nao.Wisecraft.misc;
import android.content.*;
import android.app.*;
import com.nao20010128nao.Wisecraft.*;

public class WorkingDialog extends ContextWrapper
{
	ProgressDialog waitDialog;
	public WorkingDialog (Context c){
		super(c);
	}
	public void showWorkingDialog(){
		showWorkingDialog(getResources().getString(R.string.working));
	}
	public void showWorkingDialog(String message){
		if(waitDialog!=null){
			hideWorkingDialog();
		}
		waitDialog= new ProgressDialog(this);
		waitDialog.setIndeterminate(true);
		waitDialog.setMessage(message);
		waitDialog.setCancelable(false);
		waitDialog.show();
	}
	public void hideWorkingDialog(){
		if(waitDialog==null){
			return;
		}
		waitDialog.cancel();
		waitDialog=null;
	}
}
