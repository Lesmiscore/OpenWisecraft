package com.nao20010128nao.Wisecraft.rcon.buttonActions;
import android.os.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import com.nao20010128nao.Wisecraft.misc.rcon.*;
import com.nao20010128nao.Wisecraft.rcon.*;
import java.io.*;

import com.nao20010128nao.Wisecraft.rcon.R;

public abstract class NameSelectAction extends BaseAction {
	EditText name;
	ListView online;
	Button submit;
	AlertDialog dialog;
	public NameSelectAction(RCONActivityBase act) {
		super(act);
	}

	@Override
	public void onClick(View p1) {
		dialog = new AlertDialog.Builder(this,getActivity().getPresenter().getDialogStyleId())
			.setView(inflatePlayersView())
			.show();
	}
	private View inflatePlayersView() {
		View v=((LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.rcon_players_base, null, false);
		online = (ListView)v.findViewById(R.id.players);
		name = (EditText)v.findViewById(R.id.playerName);
		submit = (Button)v.findViewById(R.id.ok);
		final ArrayAdapter<String> aa=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		online.setAdapter(aa);
		online.setOnItemClickListener(new ListView.OnItemClickListener(){
				public void onItemClick(AdapterView a, View v, int o, long i) {
					name.setText(aa.getItem(o).toString());
				}
			});
		submit.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View p1) {
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
				} catch (Throwable e) {
					DebugWriter.writeToE("RCON-NSA",e);
					getActivity().getPresenter().showSelfMessage(getActivity(), R.string.rconListError, Presenter.MESSAGE_SHOW_LENGTH_SHORT);
				}
				return RconModule_Constant.EMPTY_STRING_ARRAY;
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
