package com.nao20010128nao.Wisecraft.rcon.buttonActions;
import android.app.*;
import android.view.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.rcon.*;

public class Me extends BaseAction
{
	EditText cmd;
	AlertDialog dialog;
	public Me(RCONActivity r){
		super(r);
	}

	@Override
	public void onClick(View p1) {
		// TODO: Implement this method
		dialog=new AlertDialog.Builder(this)
			.setView(inflateDialogView())
			.show();
	}

	@Override
	public int getViewId() {
		// TODO: Implement this method
		return R.id.me;
	}
	public View inflateDialogView(){
		View v=((LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.command_continue,null,false);
		((TextView)v.findViewById(R.id.commandStarts)).setText("/me ");
		cmd=(EditText)v.findViewById(R.id.command);
		((Button)v.findViewById(R.id.ok)).setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View p1) {
					// TODO: Implement this method
					getActivity().performSend("me "+cmd.getText());
					
				}
		});
		return v;
	}
}
