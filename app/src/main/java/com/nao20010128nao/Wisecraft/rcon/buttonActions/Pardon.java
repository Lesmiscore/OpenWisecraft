package com.nao20010128nao.Wisecraft.rcon.buttonActions;
import android.app.*;
import android.content.*;
import com.google.rconclient.rcon.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.rcon.*;
import java.io.*;

public class Pardon extends NameSelectAction
{
	public Pardon(RCONActivity a){
		super(a);
	}

	@Override
	public void onSelected(final String s) {
		// TODO: Implement this method
		new AlertDialog.Builder(this)
			.setMessage(getResString(R.string.pardonAsk).replace("[PLAYER]",s))
			.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface di,int w){
					getActivity().performSend("pardon "+s);
				}
			})
			.setNegativeButton(android.R.string.cancel,Constant.BLANK_DIALOG_CLICK_LISTENER)
			.show();
	}
	
	@Override
	public String[] onPlayersList() throws IOException, AuthenticationException {
		// TODO: Implement this method
		return getActivity().getRCon().banList();
	}
	
	@Override
	public int getViewId() {
		// TODO: Implement this method
		return R.id.pardon;
	}
}
