package com.nao20010128nao.Wisecraft.misc;
import android.support.v4.app.*;

public abstract class BaseFragment<Parent extends FragmentActivity> extends Fragment {
	public Parent getParentActivity(){
		return (Parent)getActivity();
	}
}
