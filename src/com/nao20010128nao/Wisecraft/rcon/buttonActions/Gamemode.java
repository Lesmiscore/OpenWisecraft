package com.nao20010128nao.Wisecraft.rcon.buttonActions;

import com.nao20010128nao.Wisecraft.rcon.*;
import android.app.*;
import com.nao20010128nao.Wisecraft.*;
import android.content.*;
import java.io.*;
import com.google.rconclient.rcon.*;
import android.util.*;

public class Gamemode extends NameSelectAction
{
	String player=null;
	boolean isPlayer=true;
	public Gamemode(RCONActivity a){
		super(a);
	}

	@Override
	public void onSelected(final String s) {
		// TODO: Implement this method
		Log.d("gamemode","value:"+s);
		Log.d("gamemode","playe:"+player);
		if(isPlayer){
			player=s;
			isPlayer=false;
			onClick(null);
			return;
		}
		new AlertDialog.Builder(this)
			.setMessage(getResString(R.string.gamemodeAsk).replace("[PLAYER]",player).replace("[MODE]",s))
			.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface di,int w){
					getActivity().performSend("gamemode "+s+" "+player);
					player=null;
					isPlayer=true;
				}
			})
			.setNegativeButton(android.R.string.cancel,new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface di,int w){
					player=null;
					isPlayer=true;
				}
			})
			.show();
	}

	@Override
	public int getViewId() {
		// TODO: Implement this method
		return R.id.gamemode;
	}

	@Override
	public String[] onPlayersList() throws IOException, AuthenticationException {
		// TODO: Implement this method
		if(!isPlayer)
			return getResources().getStringArray(R.array.gamemodeConst);
		else
			return super.onPlayersList();
	}

	@Override
	public String onPlayerNameHint() {
		// TODO: Implement this method
		if(!isPlayer)
			return getResString(R.string.gamemodeHint);
		else
			return null;
	}
}
