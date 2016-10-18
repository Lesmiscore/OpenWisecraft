package com.nao20010128nao.Wisecraft.rcon.buttonActions;

import android.content.*;
import android.view.*;

import com.nao20010128nao.Wisecraft.misc.RconModule_Constant;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import com.nao20010128nao.Wisecraft.rcon.*;

import com.nao20010128nao.Wisecraft.rcon.R;

public class Save_All extends BaseAction {
	public Save_All(RCONActivityBase act) {
		super(act);
	}

	@Override
	public void onClick(View p1) {
		new AppCompatAlertDialog.Builder(this,getActivity().getPresenter().getDialogStyleId())
			.setMessage(R.string.auSure)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface di, int w) {
					getActivity().performSend("save-all");
				}
			})
			.setNegativeButton(android.R.string.cancel, RconModule_Constant.BLANK_DIALOG_CLICK_LISTENER)
			.show();
	}

	@Override
	public int getViewId() {
		return R.id.saveall;
	}

	@Override
	public int getTitleId() {
		return R.string.saveall;
	}
}
