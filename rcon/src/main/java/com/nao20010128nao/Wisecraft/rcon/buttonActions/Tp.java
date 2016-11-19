package com.nao20010128nao.Wisecraft.rcon.buttonActions;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.rcon.*;

public class Tp extends BaseAction {
	EditText cmd;
	AlertDialog dialog;
	public Tp(RCONActivityBase r) {
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
		return R.id.tp;
	}
	public View inflateDialogView() {
		View v=((LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.command_continue, null, false);
		((TextView)v.findViewById(R.id.commandStarts)).setText("/tp ");
		cmd = (EditText)v.findViewById(R.id.command);
		((Button)v.findViewById(R.id.ok)).setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View p1) {
					getActivity().performSend("tp " + cmd.getText());

				}
			});
		return v;
	}

	@Override
	public int getTitleId() {
		return R.string.tp;
	}
}
