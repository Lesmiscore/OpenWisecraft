package com.nao20010128nao.Wisecraft.misc.compat;
import android.support.v7.app.AlertDialog;
import android.content.Context;

public class AppCompatAlertDialog extends AlertDialog
{
	public AppCompatAlertDialog(Context ctx){
		super(ctx);
	}
	public AppCompatAlertDialog(Context ctx,int attr){
		super(ctx,attr);
	}
}
