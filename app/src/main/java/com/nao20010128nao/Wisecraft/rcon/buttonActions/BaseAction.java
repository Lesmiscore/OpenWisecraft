package com.nao20010128nao.Wisecraft.rcon.buttonActions;
import android.content.ContextWrapper;
import android.view.View.OnClickListener;
import android.widget.EditText;
import com.nao20010128nao.Wisecraft.R;
import com.nao20010128nao.Wisecraft.rcon.RCONActivity;

public abstract class BaseAction extends ContextWrapper implements OnClickListener {
	private RCONActivity ra;
	public BaseAction(RCONActivity act) {
		super(act);
		ra = act;
		act.findViewById(getViewId()).setOnClickListener(this);
	}
	public abstract int getViewId();
	public RCONActivity getActivity() {
		return ra;
	}
	public void setCommandText(CharSequence cs) {
		((EditText)ra.findViewById(R.id.command)).setText(cs);
	}
	public String getResString(int id) {
		return getResources().getString(id);
	}
}
