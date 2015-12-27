package com.nao20010128nao.Wisecraft.rcon.buttonActions;

import com.nao20010128nao.Wisecraft.rcon.*;
import android.widget.*;
import android.view.*;
import android.app.*;
import com.nao20010128nao.Wisecraft.*;
import android.content.*;

public class Save_Off extends BaseAction
{
	public Save_Off(RCONActivity act){
		super(act);
	}

	@Override
	public void onClick(View p1) {
		// TODO: Implement this method
		new AlertDialog.Builder(this)
			.setMessage(R.string.auSure)
			.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface di,int w){
					getActivity().performSend("save-off");
				}
			})
			.setNegativeButton(android.R.string.cancel,Consistant.BLANK_DIALOG_CLICK_LISTENER)
			.show();
	}

	@Override
	public int getViewId() {
		// TODO: Implement this method
		return R.id.saveall;
	}
}
