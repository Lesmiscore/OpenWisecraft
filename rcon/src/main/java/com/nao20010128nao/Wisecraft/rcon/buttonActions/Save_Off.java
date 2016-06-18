package com.nao20010128nao.Wisecraft.rcon.buttonActions;

import android.content.*;
import android.view.*;

import com.nao20010128nao.Wisecraft.misc.RconModule_Constant;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import com.nao20010128nao.Wisecraft.rcon.*;

import com.nao20010128nao.Wisecraft.rcon.R;

public class Save_Off extends BaseAction {
	public Save_Off(RCONActivityBase act) {
		super(act);
	}

	@Override
	public void onClick(View p1) {
		// TODO: Implement this method
		new AppCompatAlertDialog.Builder(this,((Presenter)getActivity().getApplication()).getDialogStyleId())
			.setMessage(R.string.auSure)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface di, int w) {
					getActivity().performSend("save-off");
				}
			})
			.setNegativeButton(android.R.string.cancel, RconModule_Constant.BLANK_DIALOG_CLICK_LISTENER)
			.show();
	}

	@Override
	public int getViewId() {
		// TODO: Implement this method
		return R.id.saveoff;
	}
}