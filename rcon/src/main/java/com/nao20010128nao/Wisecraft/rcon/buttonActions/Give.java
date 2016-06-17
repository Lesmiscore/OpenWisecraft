package com.nao20010128nao.Wisecraft.rcon.buttonActions;
import android.view.*;
import android.widget.*;

import com.nao20010128nao.Wisecraft.misc.RconModule_Constant;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import com.nao20010128nao.Wisecraft.misc.rcon.*;
import com.nao20010128nao.Wisecraft.rcon.*;
import java.io.*;

import com.nao20010128nao.Wisecraft.rcon.R;

import static com.nao20010128nao.Wisecraft.misc.RconModule_Utils.*;
import android.support.v7.app.AlertDialog;

public class Give extends NameSelectAction {
	String   amount       ,player       ,item       ;
	Button   changeAmount ,changePlayer ,changeItem ;
	TextView amountView   ,playerView   ,itemView   ;
	String   amountHint   ,playerHint   ,itemHint   ;
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
		// TODO: Implement this method
		dialog = new AppCompatAlertDialog.Builder(this,((Presenter)getActivity().getApplication()).getDialogStyleId())
			.setView(inflateDialogView())
			.show();
	}

	@Override
	public int getViewId() {
		// TODO: Implement this method
		return R.id.give;
	}

	@Override
	public void onSelected(String s) {
		// TODO: Implement this method
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
		View v=((LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.give_screen, null, false);
		changeAmount = (Button)v.findViewById(R.id.changeAmount);
		changePlayer = (Button)v.findViewById(R.id.changePlayer);
		changeItem = (Button)v.findViewById(R.id.changeItem);

		executeButton = (Button)v.findViewById(R.id.execute);

		amountView = (TextView)v.findViewById(R.id.amountView);
		playerView = (TextView)v.findViewById(R.id.playerName);
		itemView = (TextView)v.findViewById(R.id.itemId);

		changeAmount.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v) {
					hint = getResString(R.string.giveAmountHint);
					list = getResources().getStringArray(R.array.giveAmountConst);
					selecting = 0;
					Give.super.onClick(v);
				}
			});
		changePlayer.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v) {
					hint = getResString(R.string.givePlayerHint);
					list = null;
					selecting = 1;
					Give.super.onClick(v);
				}
			});
		changeItem.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v) {
					hint = getResString(R.string.giveItemHint);
					list = getResources().getStringArray(R.array.giveItemConst);
					selecting = 2;
					Give.super.onClick(v);
				}
			});
		executeButton.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v) {
					if (isNullString(amount) || isNullString(player) || isNullString(item)) {
						AppCompatAlertDialog.Builder b=new AppCompatAlertDialog.Builder(Give.this);
						String mes="";
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
				}
			});
		return v;
	}

	@Override
	public String[] onPlayersList() throws IOException,AuthenticationException {
		// TODO: Implement this method
		if (list == null) {
			return super.onPlayersList();
		} else {
			return list;
		}
	}

	@Override
	public String onPlayerNameHint() {
		// TODO: Implement this method
		return hint;
	}
}
