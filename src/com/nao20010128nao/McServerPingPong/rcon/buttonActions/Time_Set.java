package com.nao20010128nao.McServerPingPong.rcon.buttonActions;
import com.nao20010128nao.McServerPingPong.rcon.*;
import android.app.*;
import com.nao20010128nao.McServerPingPong.*;
import android.content.*;
import java.io.*;
import com.google.rconclient.rcon.*;

public class Time_Set extends NameSelectAction
{
	public Time_Set(RCONActivity a){
		super(a);
	}

	@Override
	public void onSelected(final String s) {
		// TODO: Implement this method
		new AlertDialog.Builder(this)
			.setMessage(getResString(R.string.setTimeAsk).replace("[TIME]",s))
			.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface di,int w){
					getActivity().performSend("time set "+s);
				}
			})
			.setNegativeButton(android.R.string.cancel,Consistant.BLANK_DIALOG_CLICK_LISTENER)
			.show();
	}

	@Override
	public int getViewId() {
		// TODO: Implement this method
		return R.id.settime;
	}

	@Override
	public String[] onPlayersList() throws IOException, AuthenticationException {
		// TODO: Implement this method
		return getResources().getStringArray(R.array.setTimeConst);
	}

	@Override
	public String onPlayerNameHint() {
		// TODO: Implement this method
		return getResString(R.string.setTimeHint);
	}
	
}
