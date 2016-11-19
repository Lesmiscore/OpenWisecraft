package com.nao20010128nao.Wisecraft.rcon.buttonActions;
import android.content.*;
import android.support.v7.app.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.rcon.*;
import com.nao20010128nao.Wisecraft.rcon.*;
import java.io.*;

public class Time_Set extends NameSelectAction {
	public Time_Set(RCONActivityBase a) {
		super(a);
	}

	@Override
	public void onSelected(final String s) {
		new AlertDialog.Builder(this,getActivity().getPresenter().getDialogStyleId())
			.setMessage(getResString(R.string.setTimeAsk).replace("[TIME]", s))
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface di, int w) {
					getActivity().performSend("time set " + s);
				}
			})
			.setNegativeButton(android.R.string.cancel, RconModule_Constant.BLANK_DIALOG_CLICK_LISTENER)
			.show();
	}

	@Override
	public int getViewId() {
		return R.id.settime;
	}

	@Override
	public String[] onPlayersList() throws IOException, AuthenticationException {
		return getResources().getStringArray(R.array.setTimeConst);
	}

	@Override
	public String onPlayerNameHint() {
		return getResString(R.string.setTimeHint);
	}

	@Override
	public int getTitleId() {
		return R.string.gametime;
	}
}
