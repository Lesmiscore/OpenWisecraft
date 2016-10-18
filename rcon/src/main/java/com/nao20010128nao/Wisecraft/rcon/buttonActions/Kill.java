package com.nao20010128nao.Wisecraft.rcon.buttonActions;
import android.content.*;

import com.nao20010128nao.Wisecraft.misc.RconModule_Constant;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import com.nao20010128nao.Wisecraft.rcon.*;

import com.nao20010128nao.Wisecraft.rcon.R;

public class Kill extends NameSelectAction {
	public Kill(RCONActivityBase a) {
		super(a);
	}

	@Override
	public void onSelected(final String s) {
		new AppCompatAlertDialog.Builder(this,getActivity().getPresenter().getDialogStyleId())
			.setMessage(getResString(R.string.killAsk).replace("[PLAYER]", s))
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface di, int w) {
					getActivity().performSend("kill " + s);
				}
			})
			.setNegativeButton(android.R.string.cancel, RconModule_Constant.BLANK_DIALOG_CLICK_LISTENER)
			.show();
	}

	@Override
	public int getViewId() {
		return R.id.kill;
	}

	@Override
	public int getTitleId() {
		return R.string.kill;
	}
}
