package com.nao20010128nao.Wisecraft.rcon.buttonActions;
import android.content.ContextWrapper;
import android.view.View.OnClickListener;
import android.widget.EditText;
import com.nao20010128nao.Wisecraft.rcon.R;
import com.nao20010128nao.Wisecraft.rcon.RCONActivityBase;

public abstract class BaseAction extends ContextWrapper implements OnClickListener {
	private RCONActivityBase ra;
	public BaseAction(RCONActivityBase act) {
		super(act);
		ra = act;
		act.findViewById(getViewId()).setOnClickListener(this);
	}
	public abstract int getViewId();
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
