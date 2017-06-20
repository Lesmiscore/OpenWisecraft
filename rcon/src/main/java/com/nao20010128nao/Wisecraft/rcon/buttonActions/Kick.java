package com.nao20010128nao.Wisecraft.rcon.buttonActions;

import android.support.v7.app.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.rcon.*;

public class Kick extends NameSelectAction {
    public Kick(RCONActivityBase a) {
        super(a);
    }

    @Override
    public void onSelected(final String s) {
        new AlertDialog.Builder(this, getActivity().getPresenter().getDialogStyleId())
                .setMessage(getResString(R.string.kickAsk).replace("[PLAYER]", s))
                .setPositiveButton(android.R.string.ok, (di, w) -> getActivity().performSend("kick " + s))
                .setNegativeButton(android.R.string.cancel, RconModule_Constant.BLANK_DIALOG_CLICK_LISTENER)
                .show();
    }

    @Override
    public int getViewId() {
        return R.id.kick;
    }

    @Override
    public int getTitleId() {
        return R.string.kick;
    }
}
