package com.nao20010128nao.Wisecraft.rcon.buttonActions;
import android.widget.*;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import com.google.rconclient.rcon.AuthenticationException;
import com.nao20010128nao.Wisecraft.R;
import com.nao20010128nao.Wisecraft.rcon.RCONActivity;
import java.io.IOException;
import com.nao20010128nao.Wisecraft.misc.compat.CompatArrayAdapter;
import com.nao20010128nao.Wisecraft.misc.DebugWriter;

public abstract class NameSelectAction extends BaseAction {
	EditText name;
	ListView online;
	Button submit;
	AlertDialog dialog;
	public NameSelectAction(RCONActivity act) {
		super(act);
	}

	@Override
	public void onClick(View p1) {
		// TODO: Implement this method
		dialog = new AlertDialog.Builder(this)
			.setView(inflatePlayersView())
			.show();
	}
	private View inflatePlayersView() {
		View v=((LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.rcon_players_base, null, false);
		online = (ListView)v.findViewById(R.id.players);
		name = (EditText)v.findViewById(R.id.playerName);
		submit = (Button)v.findViewById(R.id.ok);
		final ArrayAdapter aa=new ArrayAdapter(this, android.R.layout.simple_list_item_1);
		online.setAdapter(aa);
		online.setOnItemClickListener(new ListView.OnItemClickListener(){
				public void onItemClick(AdapterView a, View v, int o, long i) {
					name.setText(aa.getItem(o).toString());
				}
			});
		submit.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View p1) {
					// TODO: Implement this method
					try {
						dialog.cancel();
						dialog.dismiss();
						dialog.hide();
						dialog = null;
					} finally {
						onSelected(name.getText().toString());
					}
				}
			});
		String hint=onPlayerNameHint();
		if (hint != null) {
			name.setHint(hint);
		}
		new AsyncTask<Void,Void,String[]>(){
			public String[] doInBackground(Void[] a) {
				try {
					return onPlayersList();
				} catch (IOException e) {
					DebugWriter.writeToE("RCON-NSA",e);
				} catch (AuthenticationException e) {
					DebugWriter.writeToE("RCON-NSA",e);
				}
				return null;
			}
			public void onPostExecute(String[] s) {
				CompatArrayAdapter.addAll(aa,s);
			}
		}.execute();
		return v;
	}
	public String[] onPlayersList()throws IOException,AuthenticationException {
		return getActivity().getRCon().list();
	}
	public String onPlayerNameHint() {
		return null;
	}
	public abstract void onSelected(String name);
}
