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

public class VersionInfoFragment extends ViewHolderCatchablePreferenceFragment
{

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
	}
	
	private static String getVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            DebugWriter.writeToE("Utils",e);
			return "";
        }
    }
}
