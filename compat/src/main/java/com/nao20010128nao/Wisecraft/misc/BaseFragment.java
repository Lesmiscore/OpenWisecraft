package com.nao20010128nao.Wisecraft.misc;
import android.support.v4.app.*;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public abstract class BaseFragment<Parent extends FragmentActivity> extends Fragment {
	protected SharedPreferences pref;
	public Parent getParentActivity(){
		return (Parent)getActivity();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		pref=PreferenceManager.getDefaultSharedPreferences(getParentActivity());
	}
}
