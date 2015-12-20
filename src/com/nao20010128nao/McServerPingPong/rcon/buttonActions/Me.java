package com.nao20010128nao.McServerPingPong.rcon.buttonActions;
import com.nao20010128nao.McServerPingPong.rcon.*;
import android.view.*;
import com.nao20010128nao.McServerPingPong.*;
import android.widget.*;
import android.app.*;

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
		return R.string.me;
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
