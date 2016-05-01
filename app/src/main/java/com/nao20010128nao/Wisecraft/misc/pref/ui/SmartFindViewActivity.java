package com.nao20010128nao.Wisecraft.misc.pref.ui;
import android.app.Activity;
import android.view.View;

public abstract class SmartFindViewActivity extends Activity {

	public <T extends View> T find(int id) {
		// TODO: Implement this method
		return (T)findViewById(id);
	}

	public <T extends View> T find(String id) {
		// TODO: Implement this method
		try {
			return find((int)Class.forName(getPackageName() + ".R$id").getField(id).get(null));
		} catch (Throwable e) {

		}
		return null;
	}

	public <T extends View> T find(int id, View parent) {
		// TODO: Implement this method
		return (T)parent.findViewById(id);
	}

	public <T extends View> T find(String id, View parent) {
		// TODO: Implement this method
		try {
			return find((int)Class.forName(getPackageName() + ".R$id").getField(id).get(null), parent);
		} catch (Throwable e) {

		}
		return null;
	}

	public int look(String resKind, String name) {
		// TODO: Implement this method
		try {
			return (int)Class.forName(getPackageName() + ".R$" + resKind).getField(name).get(null);
		} catch (Throwable e) {

		}
		return -1;
	}
}
