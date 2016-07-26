package com.nao20010128nao.Wisecraft;
import android.content.*;
import java.util.*;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.EditTextPreferenceDialogFragmentCompat;
import android.support.v7.preference.Preference;
import android.view.LayoutInflater;
import android.widget.Toast;
import com.nao20010128nao.ToolBox.HandledPreference;
import com.nao20010128nao.Wisecraft.misc.Factories;
import com.nao20010128nao.Wisecraft.misc.SetTextColor;
import com.nao20010128nao.Wisecraft.misc.compat.AppCompatAlertDialog;
import com.nao20010128nao.Wisecraft.misc.pref.SHablePreferenceFragment;
import java.lang.reflect.Field;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import com.nao20010128nao.Wisecraft.misc.pref.PreferenceUtils;
import com.nao20010128nao.Wisecraft.misc.Treatment;
import android.text.InputType;
import android.text.InputFilter;

public class FragmentSettingsActivity extends AppCompatActivity {
	public static final Map<String,Class<? extends BaseFragment>> FRAGMENT_CLASSES=new HashMap<String,Class<? extends BaseFragment>>(){{
			put("root",HubPrefFragment.class);
			put("basics",Basics.class);
			put("features",Features.class);
			put("asfsls",Asfsls.class);
	}};
	public static final String DIALOG_FRAGMENT_TAG_PREFIX="settings@com.nao20010128nao.Wisecraft#";
	
	int which;
	SharedPreferences pref;
	boolean requireRestart=false;
	List<String> nonRestartKeys=Collections.unmodifiableList(Arrays.asList(new String[]{
		"showPcUserFace",
		"selectFont",
		"sendInfos",
		"exitCompletely",
		"useBright",
		"allowAutoUpdateSLSCode",
		"aausc_monnet"
	}));
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		pref=PreferenceManager.getDefaultSharedPreferences(this);
		if(pref.getBoolean("useBright",false)){
			setTheme(R.style.AppTheme_Bright);
			getTheme().applyStyle(R.style.AppTheme_Bright,true);
		}
		super.onCreate(savedInstanceState);
		pref.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener(){
				public void onSharedPreferenceChanged(SharedPreferences pref,String key){
					if(nonRestartKeys.contains(key))return;
					requireRestart=true;
				}
			});
		getSupportFragmentManager()
			.beginTransaction()
			.replace(android.R.id.content,new HubPrefFragment())
			.addToBackStack("root")
			.commit();
	}
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}

	@Override
	public void onBackPressed() {
		// TODO: Implement this method
		FragmentManager sfm=getSupportFragmentManager();
		if(sfm.getBackStackEntryCount()<2){
			finish();
			return;
		}
		sfm.popBackStack();
	}

	@Override
	public void finish() {
		// TODO: Implement this method
		if(requireRestart){
			ServerListActivityImpl.instance.get().finish();
			startActivity(new Intent(this,ServerListActivity.class));
		}
		super.finish();
	}
	
	
	public static class HubPrefFragment extends BaseFragment {
		SharedPreferences pref;
		@Override
		public void onCreatePreferences(Bundle p1, String p2) {
			// TODO: Implement this method
			addPreferencesFromResource(R.xml.settings_parent_compat);
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			// TODO: Implement this method
			pref=PreferenceManager.getDefaultSharedPreferences(getContext());
			super.onCreate(savedInstanceState);
			sH("basics", new HandledPreference.OnClickListener(){
					public void onClick(String a, String b, String c) {
						getActivity().getSupportFragmentManager()
							.beginTransaction()
							.replace(android.R.id.content,new Basics())
							.addToBackStack("basics")
							.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
							.commit();
					}
				});
			sH("features", new HandledPreference.OnClickListener(){
					public void onClick(String a, String b, String c) {
						getActivity()
							.getSupportFragmentManager()
							.beginTransaction()
							.replace(android.R.id.content,new Features())
							.addToBackStack("features")
							.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
							.commit();
					}
				});
			sH("asfsls",new HandledPreference.OnClickListener(){
					public void onClick(String a,String b,String c){
						getActivity()
							.getSupportFragmentManager()
							.beginTransaction()
							.replace(android.R.id.content,new Asfsls())
							.addToBackStack("asfsls")
							.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
							.commit();
					}
				});
			sH("osl",new HandledPreference.OnClickListener(){
					public void onClick(String a,String b,String c){
						startActivity(new Intent(getContext(),OpenSourceActivity.class));
					}
				});
			sH("aboutApp",new HandledPreference.OnClickListener(){
					public void onClick(String a,String b,String c){
						startActivity(new Intent(getContext(),AboutAppActivity.class));
					}
				});
			findPreference("asfsls").setEnabled(pref.getBoolean("feature_asfsls",false));
			((SetTextColor)findPreference("settingsAttention")).setTextColor(ContextCompat.getColor(getContext(),R.color.color888));
		}
		@Override
		public void onResume() {
			// TODO: Implement this method
			super.onResume();
			getActivity().setTitle(R.string.settings);
			findPreference("asfsls").setEnabled(pref.getBoolean("feature_asfsls",false));
		}
	}

	
	public abstract static class BaseFragment extends SHablePreferenceFragment {
		protected SharedPreferences pref;
		@Override
		public void onCreate(Bundle savedInstanceState) {
			// TODO: Implement this method
			pref=getPreferenceManager().getSharedPreferences();
			super.onCreate(savedInstanceState);
		}

		@Override
		public LayoutInflater getLayoutInflater(Bundle savedInstanceState) {
			// TODO: Implement this method
			return super.getLayoutInflater(savedInstanceState).cloneInContext(getActivity());
		}

		@Override
		public Context getContext() {
			// TODO: Implement this method
			return CalligraphyContextWrapper.wrap(super.getContext());
		}
	}


	public static class Basics extends BaseFragment {
		public static final String PARALLELS_DIALOG_FRAGMENT_TAG=DIALOG_FRAGMENT_TAG_PREFIX+"parallels-dialog";
		int which;
		@Override
		public void onCreatePreferences(Bundle p1, String p2) {
			// TODO: Implement this method
			addPreferencesFromResource(R.xml.settings_basic_compat);
			sH("serverListStyle", new HandledPreference.OnClickListener(){
					public void onClick(String a, String b, String c) {
						new AppCompatAlertDialog.Builder(getContext(),R.style.AppAlertDialog)
							.setTitle(R.string.serverListStyle)
							.setSingleChoiceItems(getResources().getStringArray(R.array.serverListStyles),pref.getInt("serverListStyle2",0),new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface di,int w){
									which=w;
								}
							})
							.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface di,int w){
									pref.edit().putInt("serverListStyle2",which).commit();
								}
							})
							.setNegativeButton(android.R.string.cancel,new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface di,int w){

								}
							})
							.show();
					}
				});
			sH("selectFont",new HandledPreference.OnClickListener(){
					public void onClick(String a,String b,String c){
						String[] choice=getFontChoices();
						String[] display=TheApplication.instance.getDisplayFontNames(choice);
						final List<String> choiceList=Arrays.<String>asList(choice);
						new AppCompatAlertDialog.Builder(getContext(),R.style.AppAlertDialog)
							.setSingleChoiceItems(display, choiceList.indexOf(TheApplication.instance.getFontFieldName())
							, new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface di, int w) {
									di.cancel();
									TheApplication.instance.setFontFieldName(choiceList.get(w));
									Toast.makeText(getContext(),R.string.saved_fonts,Toast.LENGTH_LONG).show();
								}
							})
							.show();
					}
					String[] getFontChoices() {
						List<String> l=new ArrayList();
						for (Field f:TheApplication.fonts) {
							l.add(f.getName());
						}
						l.remove("icomoon1");
						return Factories.strArray(l);
					}
				});
			findPreference("useBright").setEnabled(getResources().getBoolean(R.bool.useBrightEnabled));
		}

		@Override
		public void onResume() {
			// TODO: Implement this method
			super.onResume();
			getActivity().setTitle(R.string.basics);
		}
		
		/*
		@Override
		public void onDisplayPreferenceDialog(Preference preference) {
			// TODO: Implement this method
			if(preference.getKey().equals("parallels")){
				/*
				EditTextPreferenceDialogFragmentCompat etpdf=EditTextPreferenceDialogFragmentCompat.newInstance(preference.getKey());
				etpdf.setTargetFragment(this,0);
				etpdf.setStyle(DialogFragment.STYLE_NORMAL,R.style.AppAlertDialog);
				etpdf.show(getFragmentManager(),PARALLELS_DIALOG_FRAGMENT_TAG);
				/
				//I'll show a EditText dialog with AlertDialog.Builder because the text color of buttons can't be changed
				PreferenceUtils.showEditTextDialog(getActivity(),preference,getString(R.string.parallels_default),new Treatment<View>(){
					public void process(View v){
						EditText et=(EditText)v.findViewById(android.R.id.edit);
						et.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_NORMAL);
						ArrayList<InputFilter> ifs=new ArrayList<InputFilter>(Arrays.<InputFilter>asList(et.getFilters()));
						ifs.add(new InputFilter.LengthFilter(3));
						et.setFilters(ifs.toArray(new InputFilter[ifs.size()]));
					}
				});
				return;
			}
			super.onDisplayPreferenceDialog(preference);
		}
		*/
	}


	public static class Features extends BaseFragment {
		int which;
		@Override
		public void onCreatePreferences(Bundle p1, String p2) {
			// TODO: Implement this method
			addPreferencesFromResource(R.xml.settings_features_compat);
		}

		@Override
		public void onResume() {
			// TODO: Implement this method
			super.onResume();
			getActivity().setTitle(R.string.features);
		}
	}


	public static class Asfsls extends BaseFragment {
		int which;
		@Override
		public void onCreatePreferences(Bundle p1, String p2) {
			// TODO: Implement this method
			addPreferencesFromResource(R.xml.settings_asfsls_compat);
			SharedPreferences slsVersCache=getContext().getSharedPreferences("sls_vers_cache", 0);
			findPreference("currentSlsVersion").setSummary(slsVersCache.getString("dat.vcode",getResources().getString(R.string.unknown)));
		}

		@Override
		public void onResume() {
			// TODO: Implement this method
			super.onResume();
			getActivity().setTitle(R.string.addServerFromServerListSite);
		}
	}
}
