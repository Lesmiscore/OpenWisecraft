package com.nao20010128nao.McServerPingPong.rcon.buttonActions;
import com.nao20010128nao.McServerPingPong.rcon.*;
import android.view.*;
import android.app.*;
import android.content.*;
import com.nao20010128nao.McServerPingPong.*;
import android.widget.*;
import android.os.*;
import java.io.*;
import com.google.rconclient.rcon.*;

public abstract class NameSelectAction extends BaseAction
{
	EditText name;
	ListView online;
	Button submit;
	AlertDialog dialog;
	public NameSelectAction(RCONActivity act){
		super(act);
	}

	@Override
	public void onClick(View p1) {
		// TODO: Implement this method
		dialog=new AlertDialog.Builder(this)
			.setView(inflatePlayersView())
			.show();
	}
	private View inflatePlayersView(){
		View v=((LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.rcon_players_base,null,false);
		online=(ListView)v.findViewById(R.id.players);
		name=(EditText)v.findViewById(R.id.playerName);
		submit=(Button)v.findViewById(R.id.ok);
		final ArrayAdapter aa=new ArrayAdapter(this,android.R.layout.simple_list_item_1);
		online.setAdapter(aa);
		online.setOnItemClickListener(new ListView.OnItemClickListener(){
			public void onItemClick(AdapterView a,View v,int o,long i){
				name.setText(aa.getItem(o).toString());
			}
		});
		submit.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View p1) {
				// TODO: Implement this method
				onSelected(name.getText().toString());
				dialog.cancel();
			}
		});
		String hint=onPlayerNameHint();
		if(hint!=null){
			name.setHint(hint);
		}
		new AsyncTask<Void,Void,String[]>(){
			public String[] doInBackground(Void[] a){
				try {
					return onPlayersList();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (AuthenticationException e) {
					e.printStackTrace();
				}
				return null;
			}
			public void onPostExecute(String[] s){
				aa.addAll(s);
			}
		}.execute();
		return v;
	}
	public String[] onPlayersList()throws IOException,AuthenticationException{
		return getActivity().getRCon().list();
	}
	public String onPlayerNameHint(){
		return null;
	}
	public abstract void onSelected(String name);
}
