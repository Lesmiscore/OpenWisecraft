package com.nao20010128nao.Wisecraft.misc;
import android.content.*;
import android.os.*;
import android.preference.*;
import android.support.v4.app.*;
import android.view.*;

public abstract class BaseFragment<Parent extends FragmentActivity> extends Fragment {
	protected SharedPreferences pref;
	public Parent getParentActivity(){
		return (Parent)getActivity();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pref=PreferenceManager.getDefaultSharedPreferences(getParentActivity());
	}
	
	public View findViewById(int id){
		return getView().findViewById(id);
	}
}
