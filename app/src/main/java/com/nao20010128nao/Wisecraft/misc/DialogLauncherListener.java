package com.nao20010128nao.Wisecraft.misc;

import android.app.*;
import android.content.*;
import android.support.v7.app.*;
import android.view.*;

import android.support.v7.app.AlertDialog;

public class DialogLauncherListener extends AlertDialog.Builder implements DialogInterface.OnClickListener,View.OnClickListener 
{
	
	public DialogLauncherListener(Activity a){
		super(a);
	}

	@Override
	public void onClick(View p1) {
		show();
	}

	@Override
	public void onClick(DialogInterface p1, int p2) {
		show();
	}
}
