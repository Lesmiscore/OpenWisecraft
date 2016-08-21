package com.nao20010128nao.Wisecraft;
import android.content.*;
import android.content.pm.*;
import android.os.*;
import android.support.v7.preference.*;
import android.support.v7.widget.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;

import com.nao20010128nao.Wisecraft.misc.compat.BuildConfig;
import com.nao20010128nao.Wisecraft.misc.compat.R;

public class VersionInfoFragment extends PreferenceFragmentCompat
{

	@Override
	public void onResume() {
		// TODO: Implement this method
		super.onResume();
	}
	
	@Override
	public void onCreatePreferences(Bundle p1, String p2) {
		// TODO: Implement this method
		Class d=getBuildConfigClass();
		addPreferencesFromResource(R.xml.libcompat_version_fragment);
		findPreference("versionInfo_wisecraft").setSummary(getVersionName(getContext()));
		findPreference("versionInfo_wisecraftInternal").setSummary(getField(d,null,"GIT_REVISION_HASH"));
		findPreference("versionInfo_i18nInternal").setSummary(getField(d,null,"GIT_REVISION_HASH_I18N"));
		findPreference("versionInfo_statusesLayoutInternal").setSummary(getField(d,null,"GIT_REVISION_HASH_STATUSES_LAYOUT"));
		findPreference("versionInfo_materialIconsInternal").setSummary(getField(d,null,"GIT_REVISION_HASH_MATERIAL_ICONS"));
		findPreference("versionInfo_calligraphyInternal").setSummary(getField(d,null,"GIT_REVISION_HASH_CALLIGRAPHY"));
		findPreference("versionInfo_pstsInternal").setSummary(getField(d,null,"GIT_REVISION_HASH_PSTS"));
		findPreference("versionInfo_colorPickerInternal").setSummary(getField(d,null,"GIT_REVISION_HASH_COLOR_PICKER"));
	}
	
	private Class getBuildConfigClass(){
		try {
			return onGetBuildConfigClass();
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	@Override
	protected RecyclerView.Adapter onCreateAdapter(PreferenceScreen preferenceScreen) {
		// TODO: Implement this method
		return new ViewModifyablePreferenceGroupAdapter(preferenceScreen);
	}
	
	public Class onGetBuildConfigClass()throws ClassNotFoundException{
		return BuildConfig.class;
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
	
	
	private static String getVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            DebugWriter.writeToE("Utils",e);
			return "";
        }
    }
	
	private static <T> Object getField(Class<T> clz,T instance,String name){
		try {
			return clz.getField(name).get(instance);
		} catch (Throwable e) {
			return null;
		}
	}
}
