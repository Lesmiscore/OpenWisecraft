package com.nao20010128nao.Wisecraft;
import android.content.*;
import android.content.pm.*;
import android.os.*;
import android.support.v7.preference.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import java.util.*;

import com.nao20010128nao.Wisecraft.misc.compat.BuildConfig;
import com.nao20010128nao.Wisecraft.misc.compat.R;
import com.nao20010128nao.Wisecraft.misc.pref.*;

public class VersionInfoFragment extends ViewHolderCatchablePreferenceFragment
{
	private boolean showBuildData=false;
	private List<Preference> buildData;
	@Override
	public void onCreatePreferences(Bundle p1, String p2) {
		addPreferencesFromResource(R.xml.libcompat_version_fragment);
		findPreference("versionInfo_wisecraft").setSummary(getVersionName(getContext()));
		findPreference("versionInfo_wisecraftInternal").setSummary(BuildConfig.GIT_REVISION_HASH);
		findPreference("versionInfo_i18nInternal").setSummary(BuildConfig.GIT_REVISION_HASH_COLOR_PICKER);
		findPreference("versionInfo_statusesLayoutInternal").setSummary(BuildConfig.GIT_REVISION_HASH_STATUSES_LAYOUT);
		findPreference("versionInfo_materialIconsInternal").setSummary(BuildConfig.GIT_REVISION_HASH_MATERIAL_ICONS);
		findPreference("versionInfo_calligraphyInternal").setSummary(BuildConfig.GIT_REVISION_HASH_CALLIGRAPHY);
		findPreference("versionInfo_pstsInternal").setSummary(BuildConfig.GIT_REVISION_HASH_PSTS);
		findPreference("versionInfo_colorPickerInternal").setSummary(BuildConfig.GIT_REVISION_HASH_COLOR_PICKER);
		setShowBuildData(showBuildData);
	}
	
	private static String getVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            DebugWriter.writeToE("Utils",e);
			return "";
        }
    }
	
	public void setShowBuildData(boolean showBuildData) {
		this.showBuildData = showBuildData;
		if((showBuildData)&(buildData!=null)){
			for(Preference pref:buildData){
				pref.setVisible(true);
			}
		}else if((showBuildData)&(buildData==null)){
			if(getActivity()!=null){
				Context c=CompatUtils.wrapContextForPreference(getActivity());
				buildData=new ArrayList<>();
				buildData.add(new SimplePref(c,"Build ID",BuildConfig.CI_BUILD_ID));
				buildData.add(new SimplePref(c,"Build Ref",BuildConfig.CI_BUILD_REF_NAME));
				buildData.add(new SimplePref(c,"Runner ID",BuildConfig.CI_RUNNER_ID));
				buildData.add(new SimplePref(c,"Build Stage",BuildConfig.CI_BUILD_STAGE));
				buildData.add(new SimplePref(c,"Build Name",BuildConfig.CI_BUILD_NAME));
				for(Preference pref:buildData){
					getPreferenceScreen().addPreference(pref);
					pref.setVisible(true);
				}
			}
		}else if((!showBuildData)&(buildData!=null)){
			for(Preference pref:buildData){
				pref.setVisible(false);
			}
		}
	}

	public boolean getShowBuildData() {
		return showBuildData;
	}
}
