package com.nao20010128nao.Wisecraft.rcon.buttonActions;

import android.support.v7.app.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.rcon.*;
import com.nao20010128nao.Wisecraft.rcon.*;

import java.io.*;

public class PardonIp extends NameSelectAction {
    public PardonIp(RCONActivityBase a) {
        super(a);
    }

    @Override
    public void onSelected(final String s) {
        new AlertDialog.Builder(this, getActivity().getPresenter().getDialogStyleId())
                .setMessage(getResString(R.string.pardonIpAsk).replace("[PLAYER]", s))
                .setPositiveButton(android.R.string.ok, (di, w) -> getActivity().performSend("pardon-ip " + s))
                .setNegativeButton(android.R.string.cancel, RconModule_Constant.BLANK_DIALOG_CLICK_LISTENER)
                .show();
    }

    @Override
    public String onPlayerNameHint() {
        return getResString(R.string.pardonIpHint);
    }

    @Override
    public String[] onPlayersList() throws IOException, AuthenticationException {
        return getActivity().getRCon().banIPList();
    }

    @Override
    public int getViewId() {
        return R.id.pardonip;
    }

    @Override
    public int getTitleId() {
        return R.string.pardon_ip;
    }
}
