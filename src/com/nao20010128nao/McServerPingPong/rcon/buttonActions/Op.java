package com.nao20010128nao.McServerPingPong.rcon.buttonActions;
import com.nao20010128nao.McServerPingPong.rcon.*;
import android.view.*;
import com.nao20010128nao.McServerPingPong.*;
import java.io.*;
import com.google.rconclient.rcon.*;

public class Op extends BaseAction
{
	public Op(RCONActivity a){
		super(a);
	}

	@Override
	public void onClick(View p1) {
		// TODO: Implement this method
		String[] player;
		try {
			player = getActivity().getRCon().list();
		} catch (IOException e) {
			return;
		} catch (AuthenticationException e) {
			return;
		}
		
	}

	@Override
	public int getViewId() {
		// TODO: Implement this method
		return R.id.op;
	}
}
