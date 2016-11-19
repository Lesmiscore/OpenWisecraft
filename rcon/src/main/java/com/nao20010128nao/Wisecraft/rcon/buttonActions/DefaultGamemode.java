package com.nao20010128nao.Wisecraft.rcon.buttonActions;
import android.content.*;
import android.support.v7.app.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.rcon.*;

public class DefaultGamemode extends NameSelectAction {
	public DefaultGamemode(RCONActivityBase a) {
		super(a);
	}

	@Override
	public void onSelected(final String s) {
		new AlertDialog.Builder(this,getActivity().getPresenter().getDialogStyleId())
			.setMessage(getResString(R.string.defaultgamemodeAsk).replace("[MODE]", s))
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface di, int w) {
					getActivity().performSend("defaultgamemode " + s);
				}
			})
			.setNegativeButton(android.R.string.cancel, RconModule_Constant.BLANK_DIALOG_CLICK_LISTENER)
			.show();
	}

	@Override
	public int getViewId() {
		return R.id.defaultgamemode;
	}

	@Override
	public String[] onPlayersList() {
		return getResources().getStringArray(R.array.defaultgamemodeConst);
	}

	@Override
	public int getTitleId() {
		return R.string.defaultgm;
	}
}
