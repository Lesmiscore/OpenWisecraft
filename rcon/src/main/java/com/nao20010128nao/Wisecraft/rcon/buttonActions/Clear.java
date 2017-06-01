package com.nao20010128nao.Wisecraft.rcon.buttonActions;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.rcon.*;
import com.nao20010128nao.Wisecraft.rcon.*;
import java.io.*;

import static com.nao20010128nao.Wisecraft.misc.RconModule_Utils.*;

public class Clear extends NameSelectAction {
	String   player       ,item       ;
	Button   changePlayer ,changeItem ;
	TextView playerView   ,itemView   ;
	String   playerHint   ,itemHint   ;
	/*      |             ,           |*/

	Button executeButton;
	AlertDialog dialog;

	String[] list;
	String hint;
	int selecting;

	public Clear(RCONActivityBase r) {
		super(r);
	}

	@Override
	public void onClick(View p1) {
		dialog = new AlertDialog.Builder(this,getActivity().getPresenter().getDialogStyleId())
			.setView(inflateDialogView())
			.show();
	}

	@Override
	public int getViewId() {
		return R.id.clear;
	}

	@Override
	public void onSelected(String s) {
		switch (selecting) {
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
		View v=((LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.clear_screen, null, false);
		changePlayer = (Button)v.findViewById(R.id.changePlayer);
		changeItem = (Button)v.findViewById(R.id.changeItem);

		executeButton = (Button)v.findViewById(R.id.execute);

		playerView = (TextView)v.findViewById(R.id.playerName);
		itemView = (TextView)v.findViewById(R.id.itemId);

		changePlayer.setOnClickListener(v13 -> {
            hint = getResString(R.string.givePlayerHint);
            list = null;
            selecting = 1;
            Clear.super.onClick(v13);
        });
		changeItem.setOnClickListener(v12 -> {
            hint = getResString(R.string.giveItemHint);
            list = getResources().getStringArray(R.array.giveItemConst);
            selecting = 2;
            Clear.super.onClick(v12);
        });
		executeButton.setOnClickListener(v1 -> {
            if (isNullString(player) || isNullString(item)) {
                AlertDialog.Builder b=new AlertDialog.Builder(Clear.this,getActivity().getPresenter().getDialogStyleId());
                String mes="";
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
                getActivity().performSend("clear " + player + " " + item);
                dialog.dismiss();
            }
        });
		return v;
	}

	@Override
	public String[] onPlayersList() throws IOException,AuthenticationException {
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
		return R.string.clear;
	}
}
