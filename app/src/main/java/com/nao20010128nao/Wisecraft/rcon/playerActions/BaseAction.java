package com.nao20010128nao.Wisecraft.rcon.playerActions;
import android.content.*;
import com.nao20010128nao.Wisecraft.rcon.*;
import android.view.*;

public abstract class BaseAction extends ContextWrapper
{
	RCONActivity ra;
	public BaseAction(RCONActivity activity){
		super(activity);
		ra=activity;
	}
	public abstract String getCommandTitle();
	public abstract void onCommand(String player);
	
	public RCONActivity getActivity(){
		return ra;
	}
	public String getResString(int id){
		return getResources().getString(id);
	}
}
