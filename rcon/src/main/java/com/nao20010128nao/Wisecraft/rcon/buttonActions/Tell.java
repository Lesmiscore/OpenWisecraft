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

public class Tell extends NameSelectAction {
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

	public Tell(RCONActivityBase r) {
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
		return R.id.tell;
	}

	@Override
	public void onSelected(String s) {
		// TODO: Implement this method
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
		View v=((LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.tell_screen, null, false);
		changePlayer = (Button)v.findViewById(R.id.changePlayer);
		changeItem = (Button)v.findViewById(R.id.changeItem);

		executeButton = (Button)v.findViewById(R.id.execute);

		playerView = (TextView)v.findViewById(R.id.playerName);
		itemView = (TextView)v.findViewById(R.id.itemId);

		changePlayer.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v) {
					hint = getResString(R.string.givePlayerHint);
					list = null;
					selecting = 1;
					Tell.super.onClick(v);
				}
			});
		changeItem.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v) {
					hint = getResString(R.string.tellMessageHint);
					list = RconModule_Constant.EMPTY_STRING_ARRAY;
					selecting = 2;
					Tell.super.onClick(v);
				}
			});
		executeButton.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v) {
					if (isNullString(player) || isNullString(item)) {
						AppCompatAlertDialog.Builder b=new AppCompatAlertDialog.Builder(Tell.this);
						String mes="";
						if (isNullString(player)) {
							mes += getResString(R.string.giveSelectPlayer) + "\n";
						}
						if (isNullString(item)) {
							mes += getResString(R.string.tellSetMessage) + "\n";
						}
						b.setMessage(mes);
						b.setPositiveButton(android.R.string.ok, RconModule_Constant.BLANK_DIALOG_CLICK_LISTENER);
						b.show();
					} else {
						getActivity().performSend("tell " + player + " " + item);
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