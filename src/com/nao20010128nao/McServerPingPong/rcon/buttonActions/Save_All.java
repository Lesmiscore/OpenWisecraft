package com.nao20010128nao.McServerPingPong.rcon.buttonActions;

import com.nao20010128nao.McServerPingPong.rcon.*;
import android.widget.*;
import android.view.*;
import android.app.*;
import com.nao20010128nao.McServerPingPong.*;
import android.content.*;

public class Save_All extends BaseAction
{
	public Save_All(RCONActivity act){
		super(act);
	}

	@Override
	public void onClick(View p1) {
		// TODO: Implement this method
		new AlertDialog.Builder(this)
			.setMessage(R.string.auSure)
			.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface di,int w){
					getActivity().performSend("save-all");
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
