package com.nao20010128nao.Wisecraft.rcon.buttonActions;

import android.support.v7.app.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.rcon.*;

public class BanIp extends NameSelectAction {
    public BanIp(RCONActivityBase a) {
        super(a);
    }

    @Override
    public void onSelected(final String s) {
        new AlertDialog.Builder(this, getActivity().getPresenter().getDialogStyleId())
                .setMessage(getResString(R.string.banIpAsk).replace("[PLAYER]", s))
                .setPositiveButton(android.R.string.ok, (di, w) -> getActivity().performSend("ban-ip " + s))
                .setNegativeButton(android.R.string.cancel, RconModule_Constant.BLANK_DIALOG_CLICK_LISTENER)
                .show();
    }

    @Override
    public String onPlayerNameHint() {
        return getResString(R.string.banIpHint);
    }

    @Override
    public int getViewId() {
        return R.id.banip;
    }

    @Override
    public int getTitleId() {
        return R.string.ban_ip;
    }
}
