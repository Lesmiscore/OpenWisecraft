package com.nao20010128nao.Wisecraft.rcon.buttonActions;
import com.nao20010128nao.Wisecraft.rcon.*;
import android.view.*;
import com.nao20010128nao.Wisecraft.*;
import java.io.*;
import com.google.rconclient.rcon.*;
import android.app.*;
import android.content.*;

public class PardonIp extends NameSelectAction
{
	public PardonIp(RCONActivity a){
		super(a);
	}

	@Override
	public void onSelected(final String s) {
		// TODO: Implement this method
		new AlertDialog.Builder(this)
			.setMessage(getResString(R.string.pardonIpAsk).replace("[PLAYER]",s))
			.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface di,int w){
					getActivity().performSend("pardon-ip "+s);
				}
			})
			.setNegativeButton(android.R.string.cancel,Constant.BLANK_DIALOG_CLICK_LISTENER)
			.show();
	}

	@Override
	public String onPlayerNameHint() {
		// TODO: Implement this method
		return getResString(R.string.pardonIpHint);
	}

	@Override
	public String[] onPlayersList() throws IOException, AuthenticationException {
		// TODO: Implement this method
		return getActivity().getRCon().banIPList();
	}

	@Override
	public int getViewId() {
		// TODO: Implement this method
		return R.id.pardonip;
	}
}
