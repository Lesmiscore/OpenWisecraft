package com.nao20010128nao.Wisecraft.rcon.buttonActions;
import android.content.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.rcon.*;

public class Me extends BaseAction {
	EditText cmd;
	AlertDialog dialog;
	public Me(RCONActivityBase r) {
		super(r);
	}

	@Override
	public void onClick(View p1) {
		dialog = new AlertDialog.Builder(this,getActivity().getPresenter().getDialogStyleId())
			.setView(inflateDialogView())
			.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface di,int w){
					getActivity().performSend("me " + cmd.getText());
				}
			})
			.show();
	}

	@Override
	public int getViewId() {
		return R.id.me;
	}
	public View inflateDialogView() {
		View v=((LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.command_continue, null, false);
		((TextView)v.findViewById(R.id.commandStarts)).setText("/me ");
		cmd = (EditText)v.findViewById(R.id.command);
		return v;
	}

	@Override
	public int getTitleId() {
		return R.string.me;
	}
}
