package com.nao20010128nao.McServerPingPong.rcon.buttonActions;
import com.nao20010128nao.McServerPingPong.rcon.*;
import android.view.*;
import com.nao20010128nao.McServerPingPong.*;
import java.io.*;
import com.google.rconclient.rcon.*;
import android.app.*;
import android.content.*;

public class Kick extends NameSelectAction
{
	public Kick(RCONActivity a){
		super(a);
	}

	@Override
	public void onSelected(final String s) {
		// TODO: Implement this method
		new AlertDialog.Builder(this)
			.setMessage(getResString(R.string.kickAsk).replace("[PLAYER]",s))
			.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface di,int w){
					getActivity().performSend("kick "+s);
				}
			})
			.setNegativeButton(android.R.string.cancel,Consistant.BLANK_DIALOG_CLICK_LISTENER)
			.show();
	}

	@Override
	public int getViewId() {
		// TODO: Implement this method
		return R.id.kick;
	}
}
