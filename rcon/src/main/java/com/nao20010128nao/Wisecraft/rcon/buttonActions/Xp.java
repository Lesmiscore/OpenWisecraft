package com.nao20010128nao.Wisecraft.rcon.buttonActions;

import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.rcon.*;
import com.nao20010128nao.Wisecraft.rcon.*;

import java.io.*;

import static com.nao20010128nao.Wisecraft.misc.RconModule_Utils.*;

public class Xp extends NameSelectAction {
    String amount, player;
    Button changeAmount, changePlayer;
    TextView amountView, playerView;
    String amountHint, playerHint;
    /*      |             ,             |*/

    Button executeButton;
    AlertDialog dialog;

    String[] list;
    String hint;
    int selecting;

    public Xp(RCONActivityBase r) {
        super(r);
    }

    @Override
    public void onClick(View p1) {
        dialog = new AlertDialog.Builder(this, getActivity().getPresenter().getDialogStyleId())
                .setView(inflateDialogView())
                .show();
    }

    @Override
    public int getViewId() {
        return R.id.xp;
    }

    @Override
    public void onSelected(String s) {
        switch (selecting) {
            case 0:
                amount = s;
                amountView.setText(s);
                break;
            case 1:
                player = s;
                playerView.setText(s);
                break;
        }
        selecting = -1;
    }

    public View inflateDialogView() {
        View v = ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.xp_screen, null, false);
        changeAmount = v.findViewById(R.id.changeAmount);
        changePlayer = v.findViewById(R.id.changePlayer);

        executeButton = v.findViewById(R.id.execute);

        amountView = v.findViewById(R.id.amountView);
        playerView = v.findViewById(R.id.playerName);

        changeAmount.setOnClickListener(v13 -> {
            hint = getResString(R.string.giveAmountHint);
            list = getResources().getStringArray(R.array.giveAmountConst);
            selecting = 0;
            Xp.super.onClick(v13);
        });
        changePlayer.setOnClickListener(v12 -> {
            hint = getResString(R.string.givePlayerHint);
            list = null;
            selecting = 1;
            Xp.super.onClick(v12);
        });
        executeButton.setOnClickListener(v1 -> {
            if (isNullString(amount) || isNullString(player)) {
                AlertDialog.Builder b = new AlertDialog.Builder(Xp.this, getActivity().getPresenter().getDialogStyleId());
                String mes = "";
                if (isNullString(amount)) {
                    mes += getResString(R.string.giveSelectAmount) + "\n";
                }
                if (isNullString(player)) {
                    mes += getResString(R.string.giveSelectPlayer) + "\n";
                }
                b.setMessage(mes);
                b.setPositiveButton(android.R.string.ok, RconModule_Constant.BLANK_DIALOG_CLICK_LISTENER);
                b.show();
            } else {
                getActivity().performSend("xp " + amount + "L " + player);
                dialog.dismiss();
            }
        });
        return v;
    }

    @Override
    public String[] onPlayersList() throws IOException, AuthenticationException {
        if (list == null) {
            return super.onPlayersList();
        } else {
            return list;
        }
    }

    @Override
    public String onPlayerNameHint() {
        return hint;
    }

    @Override
    public int getTitleId() {
        return R.string.xp;
    }
}
