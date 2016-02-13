package com.nao20010128nao.Wisecraft.rcon.buttonActions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import com.nao20010128nao.Wisecraft.Constant;
import com.nao20010128nao.Wisecraft.R;
import com.nao20010128nao.Wisecraft.rcon.RCONActivity;

public class DefaultGamemode extends NameSelectAction {
	public DefaultGamemode(RCONActivity a) {
		super(a);
	}

	@Override
	public void onSelected(final String s) {
		// TODO: Implement this method
		new AlertDialog.Builder(this)
			.setMessage(getResString(R.string.defaultgamemodeAsk).replace("[MODE]", s))
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface di, int w) {
					getActivity().performSend("defaultgamemode " + s);
				}
			})
			.setNegativeButton(android.R.string.cancel, Constant.BLANK_DIALOG_CLICK_LISTENER)
			.show();
	}

	@Override
	public int getViewId() {
		// TODO: Implement this method
		return R.id.defaultgamemode;
	}

	@Override
	public String[] onPlayersList() {
		// TODO: Implement this method
		return getResources().getStringArray(R.array.defaultgamemodeConst);
	}
}
