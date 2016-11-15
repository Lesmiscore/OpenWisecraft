package com.nao20010128nao.Wisecraft.misc;

import android.content.*;

public abstract class QuickDialogClickListener implements DialogInterface.OnClickListener 
{
	@Override
	public final void onClick(DialogInterface p1, int p2) {
		onClick(p2);
	}

	public abstract void onClick(int which);
}
