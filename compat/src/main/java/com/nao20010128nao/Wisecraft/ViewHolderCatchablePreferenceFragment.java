package com.nao20010128nao.Wisecraft;

import android.support.v7.preference.*;
import android.support.v7.widget.*;

public abstract class ViewHolderCatchablePreferenceFragment extends PreferenceFragmentCompat 
{
	@Override
	protected RecyclerView.Adapter onCreateAdapter(PreferenceScreen preferenceScreen) {
		// TODO: Implement this method
		return new ViewModifyablePreferenceGroupAdapter(preferenceScreen);
	}

	public void onModifyPreferenceViewHolder(PreferenceViewHolder viewHolder,Preference pref){

	}

	class ViewModifyablePreferenceGroupAdapter extends PreferenceGroupAdapter{
		public ViewModifyablePreferenceGroupAdapter(PreferenceGroup pg){
			super(pg);
		}

		@Override
		public void onBindViewHolder(PreferenceViewHolder holder, int position) {
			// TODO: Implement this method
			super.onBindViewHolder(holder, position);
			onModifyPreferenceViewHolder(holder,getItem(position));
		}
	}
}
