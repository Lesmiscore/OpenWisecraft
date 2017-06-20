package com.nao20010128nao.Wisecraft.rcon.buttonActions;

import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.rcon.*;
import com.nao20010128nao.Wisecraft.rcon.*;

import java.io.*;

import static com.nao20010128nao.Wisecraft.misc.RconModule_Utils.*;

public class Give extends NameSelectAction {
    String amount, player, item;
    Button changeAmount, changePlayer, changeItem;
    TextView amountView, playerView, itemView;
    String amountHint, playerHint, itemHint;
    /*      |             ,             ,           |*/

    Button executeButton;
    AlertDialog dialog;

    String[] list;
    String hint;
    int selecting;

    public Give(RCONActivityBase r) {
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
        return R.id.give;
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
            case 2:
                item = s;
                itemView.setText(s);
                break;
        }
        selecting = -1;
    }

    public View inflateDialogView() {
        View v = ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.give_screen, null, false);
        changeAmount = (Button) v.findViewById(R.id.changeAmount);
        changePlayer = (Button) v.findViewById(R.id.changePlayer);
        changeItem = (Button) v.findViewById(R.id.changeItem);

        executeButton = (Button) v.findViewById(R.id.execute);

        amountView = (TextView) v.findViewById(R.id.amountView);
        playerView = (TextView) v.findViewById(R.id.playerName);
        itemView = (TextView) v.findViewById(R.id.itemId);

        changeAmount.setOnClickListener(v14 -> {
            hint = getResString(R.string.giveAmountHint);
            list = getResources().getStringArray(R.array.giveAmountConst);
            selecting = 0;
            Give.super.onClick(v14);
        });
        changePlayer.setOnClickListener(v13 -> {
            hint = getResString(R.string.givePlayerHint);
            list = null;
            selecting = 1;
            Give.super.onClick(v13);
        });
        changeItem.setOnClickListener(v12 -> {
            hint = getResString(R.string.giveItemHint);
            list = getResources().getStringArray(R.array.giveItemConst);
            selecting = 2;
            Give.super.onClick(v12);
        });
        executeButton.setOnClickListener(v1 -> {
            if (isNullString(amount) || isNullString(player) || isNullString(item)) {
                AlertDialog.Builder b = new AlertDialog.Builder(Give.this, getActivity().getPresenter().getDialogStyleId());
                String mes = "";
                if (isNullString(amount)) {
                    mes += getResString(R.string.giveSelectAmount) + "\n";
                }
                if (isNullString(player)) {
                    mes += getResString(R.string.giveSelectPlayer) + "\n";
                }
                if (isNullString(item)) {
                    mes += getResString(R.string.giveSelectItem) + "\n";
                }
                b.setMessage(mes);
                b.setPositiveButton(android.R.string.ok, RconModule_Constant.BLANK_DIALOG_CLICK_LISTENER);
                b.show();
            } else {
                getActivity().performSend("give " + player + " " + item + " " + amount);
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
        return R.string.give;
    }
}
