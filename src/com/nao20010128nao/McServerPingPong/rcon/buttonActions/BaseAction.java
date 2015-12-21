package com.nao20010128nao.McServerPingPong.rcon.buttonActions;
import android.content.*;
import com.nao20010128nao.McServerPingPong.rcon.*;
import android.widget.*;
import android.view.View.*;
import com.nao20010128nao.McServerPingPong.*;

public abstract class BaseAction extends ContextWrapper implements OnClickListener
{
	private RCONActivity ra;
	public BaseAction(RCONActivity act){
		super(act);
		ra=act;
		act.findViewById(getViewId()).setOnClickListener(this);
	}
	public abstract int getViewId();
	public RCONActivity getActivity(){
		return ra;
	}
	public void setCommandText(CharSequence cs){
		((EditText)ra.findViewById(R.id.command)).setText(cs);
	}
	public String getResString(int id){
		return getResources().getString(id);
	}
}
