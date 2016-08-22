package com.nao20010128nao.Wisecraft;

import android.support.v7.preference.*;
import android.support.v7.widget.*;

public abstract class ViewHolderCatchablePreferenceFragment extends PreferenceFragmentCompat 
{
	@Override
	protected RecyclerView.Adapter onCreateAdapter(PreferenceScreen preferenceScreen) {
		// TODO: Implement this method
		return new ViewModifyablePreferenceGroupAdapter(preferenceScreen,this);
	}

	public void onModifyPreferenceViewHolder(PreferenceViewHolder viewHolder,Preference pref){

	}

	public static class ViewModifyablePreferenceGroupAdapter extends PreferenceGroupAdapter{
		ViewHolderCatchablePreferenceFragment fragment;
		public ViewModifyablePreferenceGroupAdapter(PreferenceGroup pg,ViewHolderCatchablePreferenceFragment fragment){
			super(pg);
			this.fragment=fragment;
		}

		@Override
		public void onBindViewHolder(PreferenceViewHolder holder, int position) {
			// TODO: Implement this method
			super.onBindViewHolder(holder, position);
			fragment.onModifyPreferenceViewHolder(holder,getItem(position));
		}
	}
}
