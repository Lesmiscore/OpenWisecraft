package com.nao20010128nao.Wisecraft.rcon.buttonActions;
import com.nao20010128nao.Wisecraft.rcon.*;
import android.view.*;
import com.nao20010128nao.Wisecraft.*;
import java.io.*;
import com.google.rconclient.rcon.*;
import android.app.*;
import android.content.*;

public class Op extends NameSelectAction
{
	public Op(RCONActivity a){
		super(a);
	}

	@Override
	public void onSelected(final String s) {
		// TODO: Implement this method
		new AlertDialog.Builder(this)
			.setMessage(getResString(R.string.giveOpAsk).replace("[PLAYER]",s))
			.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface di,int w){
					getActivity().performSend("op "+s);
				}
			})
			.setNegativeButton(android.R.string.cancel,new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface di,int w){}
			})
			.show();
	}

	@Override
	public int getViewId() {
		// TODO: Implement this method
		return R.id.op;
	}
}
