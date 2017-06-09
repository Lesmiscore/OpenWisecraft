package com.nao20010128nao.Wisecraft.rcon.buttonActions;
import android.content.*;
import android.support.v7.app.*;
import com.nao20010128nao.Wisecraft.rcon.*;

public class Op extends NameSelectAction {
	public Op(RCONActivityBase a) {
		super(a);
	}

	@Override
	public void onSelected(final String s) {
		new AlertDialog.Builder(this,getActivity().getPresenter().getDialogStyleId())
			.setMessage(getResString(R.string.giveOpAsk).replace("[PLAYER]", s))
			.setPositiveButton(android.R.string.ok, (di, w) -> getActivity().performSend("op " + s))
			.setNegativeButton(android.R.string.cancel, (di, w) -> {})
			.show();
	}

	@Override
	public int getViewId() {
		return R.id.op;
	}

	@Override
	public int getTitleId() {
		return R.string.op;
	}
}
