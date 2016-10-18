package com.nao20010128nao.Wisecraft.rcon.buttonActions;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.nao20010128nao.Wisecraft.misc.RconModule_Constant;
import com.nao20010128nao.Wisecraft.rcon.R;
import com.nao20010128nao.Wisecraft.misc.compat.AppCompatAlertDialog;
import com.nao20010128nao.Wisecraft.misc.rcon.AuthenticationException;
import com.nao20010128nao.Wisecraft.rcon.RCONActivityBase;
import java.io.IOException;

import static com.nao20010128nao.Wisecraft.misc.RconModule_Utils.*;
import android.support.v7.app.AlertDialog;
import com.nao20010128nao.Wisecraft.rcon.Presenter;

public class Xp extends NameSelectAction {
	String   amount       ,player       ;
	Button   changeAmount ,changePlayer ;
	TextView amountView   ,playerView   ;
	String   amountHint   ,playerHint   ;
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
		dialog = new AppCompatAlertDialog.Builder(this,getActivity().getPresenter().getDialogStyleId())
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
		View v=((LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.xp_screen, null, false);
		changeAmount = (Button)v.findViewById(R.id.changeAmount);
		changePlayer = (Button)v.findViewById(R.id.changePlayer);

		executeButton = (Button)v.findViewById(R.id.execute);

		amountView = (TextView)v.findViewById(R.id.amountView);
		playerView = (TextView)v.findViewById(R.id.playerName);

		changeAmount.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v) {
					hint = getResString(R.string.giveAmountHint);
					list = getResources().getStringArray(R.array.giveAmountConst);
					selecting = 0;
					Xp.super.onClick(v);
				}
			});
		changePlayer.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v) {
					hint = getResString(R.string.givePlayerHint);
					list = null;
					selecting = 1;
					Xp.super.onClick(v);
				}
			});
		executeButton.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v) {
					if (isNullString(amount) || isNullString(player)) {
						AppCompatAlertDialog.Builder b=new AppCompatAlertDialog.Builder(Xp.this,getActivity().getPresenter().getDialogStyleId());
						String mes="";
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
		return R.string.xp;
	}
}
