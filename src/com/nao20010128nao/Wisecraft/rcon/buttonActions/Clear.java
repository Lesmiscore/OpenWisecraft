package com.nao20010128nao.Wisecraft.rcon.buttonActions;
import com.nao20010128nao.Wisecraft.rcon.*;
import android.view.*;
import com.nao20010128nao.Wisecraft.*;
import android.app.*;
import android.widget.*;
import java.io.*;
import com.google.rconclient.rcon.*;
import static com.nao20010128nao.Wisecraft.Utils.*;

public class Clear extends NameSelectAction
{
	String   player       ,item       ;
	Button   changePlayer ,changeItem ;
	TextView playerView   ,itemView   ;
	String   playerHint   ,itemHint   ;
	/*      |             ,           |*/

	Button executeButton;
	AlertDialog dialog;
	
	String[] list;
	String hint;
	int selecting;
	
	public Clear(RCONActivity r){
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
		return R.id.clear;
	}

	@Override
	public void onSelected(String s) {
		// TODO: Implement this method
		switch(selecting){
			case 1:
				player=s;
				playerView.setText(s);
				break;
			case 2:
				item=s;
				itemView.setText(s);
				break;
		}
		selecting=-1;
	}
	
	public View inflateDialogView(){
		View v=((LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.clear_screen,null,false);
		changePlayer=(Button)v.findViewById(R.id.changePlayer);
		changeItem=(Button)v.findViewById(R.id.changeItem);
		
		executeButton=(Button)v.findViewById(R.id.execute);
		
		playerView=(TextView)v.findViewById(R.id.playerName);
		itemView=(TextView)v.findViewById(R.id.itemId);

		changePlayer.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v){
					hint=getResString(R.string.givePlayerHint);
					list=null;
					selecting=1;
					Clear.super.onClick(v);
				}
			});
		changeItem.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v){
					hint=getResString(R.string.giveItemHint);
					list=getResources().getStringArray(R.array.giveItemConst);
					selecting=2;
					Clear.super.onClick(v);
				}
			});
		executeButton.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v){
					if(isNullString(player)||isNullString(item)){
						AlertDialog.Builder b=new AlertDialog.Builder(Clear.this);
						String mes="";
						if(isNullString(player)){
							mes+=getResString(R.string.giveSelectPlayer)+"\n";
						}
						if(isNullString(item)){
							mes+=getResString(R.string.giveSelectItem)+"\n";
						}
						b.setMessage(mes);
						b.setPositiveButton(android.R.string.ok,Constant.BLANK_DIALOG_CLICK_LISTENER);
						b.show();
					}else{
						getActivity().performSend("clear "+player+" "+item);
						dialog.dismiss();
					}
				}
			});
		return v;
	}

	@Override
	public String[] onPlayersList() throws IOException,AuthenticationException{
		// TODO: Implement this method
		if(list==null){
			return super.onPlayersList();
		}else{
			return list;
		}
	}

	@Override
	public String onPlayerNameHint() {
		// TODO: Implement this method
		return hint;
	}
}
