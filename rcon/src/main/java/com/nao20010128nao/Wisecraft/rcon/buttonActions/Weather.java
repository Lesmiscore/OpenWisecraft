package com.nao20010128nao.Wisecraft.rcon.buttonActions;
import android.content.*;

import com.nao20010128nao.Wisecraft.misc.RconModule_Constant;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import com.nao20010128nao.Wisecraft.misc.rcon.*;
import com.nao20010128nao.Wisecraft.rcon.*;
import java.io.*;

import com.nao20010128nao.Wisecraft.rcon.R;

public class Weather extends NameSelectAction {
	public Weather(RCONActivityBase a) {
		super(a);
	}

	@Override
	public void onSelected(final String s) {
		new AppCompatAlertDialog.Builder(this,getActivity().getPresenter().getDialogStyleId())
			.setMessage(getResString(R.string.weatherAsk).replace("[MODE]", s))
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface di, int w) {
					getActivity().performSend("weather " + s);
				}
			})
			.setNegativeButton(android.R.string.cancel, RconModule_Constant.BLANK_DIALOG_CLICK_LISTENER)
			.show();
	}

	@Override
	public int getViewId() {
		return R.id.weather;
	}

	@Override
	public String[] onPlayersList() throws IOException, AuthenticationException {
		return super.onPlayersList();
	}

	@Override
	public int getTitleId() {
		return R.string.changeweather;
	}
}
