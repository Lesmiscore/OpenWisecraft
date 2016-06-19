package com.nao20010128nao.WRcon;
import com.nao20010128nao.Wisecraft.rcon.RCONActivityBase;
import android.os.Bundle;
import com.nao20010128nao.Wisecraft.misc.compat.AppCompatAlertDialog;
import android.content.DialogInterface;

public class RCONActivity extends RCONActivityBase
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setTitle(getIntent().getStringExtra("ip")+":"+getIntent().getIntExtra("port",0));
	}

	@Override
	public void exitActivity() {
		// TODO: Implement this method
		new AppCompatAlertDialog.Builder(this,R.style.AppAlertDialog)
			.setMessage(R.string.auSure_exit)
			.setNegativeButton(android.R.string.ok,new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface di,int w){
					RCONActivity.super.exitActivity();
				}
			})
			.setPositiveButton(android.R.string.cancel,null)
			.show();
	}
}
