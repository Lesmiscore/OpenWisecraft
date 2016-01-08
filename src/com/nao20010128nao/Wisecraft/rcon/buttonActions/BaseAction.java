package com.nao20010128nao.Wisecraft.rcon.buttonActions;
import android.content.*;
import android.view.View.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.rcon.*;

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
