package com.nao20010128nao.Wisecraft.misc;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;

public abstract class BaseFragment<Parent extends FragmentActivity> extends Fragment {
	public Parent getParentActivity(){
		return (Parent)getActivity();
	}
}
