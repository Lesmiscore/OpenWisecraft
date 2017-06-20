package com.nao20010128nao.Wisecraft.rcon.buttonActions;

import android.support.v7.app.*;
import android.view.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.rcon.*;

public class Save_On extends BaseAction {
    public Save_On(RCONActivityBase act) {
        super(act);
    }

    @Override
    public void onClick(View p1) {
        new AlertDialog.Builder(this, getActivity().getPresenter().getDialogStyleId())
                .setMessage(R.string.auSure)
                .setPositiveButton(android.R.string.ok, (di, w) -> getActivity().performSend("save-on"))
                .setNegativeButton(android.R.string.cancel, RconModule_Constant.BLANK_DIALOG_CLICK_LISTENER)
                .show();
    }

    @Override
    public int getViewId() {
        return R.id.saveon;
    }

    @Override
    public int getTitleId() {
        return R.string.saveon;
    }
}
