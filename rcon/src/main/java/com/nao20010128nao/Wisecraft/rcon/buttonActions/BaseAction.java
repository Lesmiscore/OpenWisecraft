package com.nao20010128nao.Wisecraft.rcon.buttonActions;
import android.content.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.mikepenz.materialdrawer.*;
import com.mikepenz.materialdrawer.model.*;
import com.mikepenz.materialdrawer.model.interfaces.*;
import com.nao20010128nao.Wisecraft.rcon.*;

import com.nao20010128nao.Wisecraft.rcon.R;

public abstract class BaseAction extends ContextWrapper implements OnClickListener {
	private RCONActivityBase ra;
	public BaseAction(RCONActivityBase act) {
		super(act);
		ra = act;
	}
	@Deprecated
	public abstract int getViewId();
	public abstract int getTitleId();
	public final int getDrawableId(){return -1;}//don't implement it at present
	
	public PrimaryDrawerItem newDrawerItem(){
		PrimaryDrawerItem pdi=getActivity().onCreatePrimaryDrawerItem();
		pdi.withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener(){
			public boolean onItemClick(View v,int i,IDrawerItem di){
				onClick(v);
				return true;
			}
		});
		pdi.withDescription(getTitleId());
		return pdi;
	}
	public RCONActivityBase getActivity() {
		return ra;
	}
	public void setCommandText(CharSequence cs) {
		((EditText)ra.findViewById(R.id.command)).setText(cs);
	}
	public String getResString(int id) {
		return getResources().getString(id);
	}
}
