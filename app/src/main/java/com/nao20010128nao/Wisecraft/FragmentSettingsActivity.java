package com.nao20010128nao.Wisecraft;
import android.content.*;
import android.os.*;
import android.preference.*;
import android.support.v7.app.*;
import android.widget.*;
import com.nao20010128nao.ToolBox.*;
import com.nao20010128nao.Wisecraft.misc.Factories;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import com.nao20010128nao.Wisecraft.misc.pref.*;
import java.lang.reflect.*;
import java.util.*;
import uk.co.chrisjenx.calligraphy.*;

public class FragmentSettingsActivity extends AppCompatActivity {
	int which;
	SharedPreferences pref;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		pref=PreferenceManager.getDefaultSharedPreferences(this);
		if(pref.getBoolean("useBright",false)){
			setTheme(R.style.AppTheme_Bright);
			getTheme().applyStyle(R.style.AppTheme_Bright,true);
		}
		super.onCreate(savedInstanceState);
		getSupportFragmentManager().beginTransaction().replace(android.R.id.content,new HubPrefFragment()).commit();
	}
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}
	
	public static class HubPrefFragment extends SHablePreferenceFragment {
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
						startActivity(new Intent(getContext(),Basics.class));
					}
				});
			sH("features", new HandledPreference.OnClickListener(){
					public void onClick(String a, String b, String c) {
						startActivity(new Intent(getContext(),Features.class));
					}
				});
			sH("asfsls",new HandledPreference.OnClickListener(){
					public void onClick(String a,String b,String c){
						startActivity(new Intent(getContext(),Asfsls.class));
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
		}
		@Override
		public void onResume() {
			// TODO: Implement this method
			super.onResume();
			findPreference("asfsls").setEnabled(pref.getBoolean("feature_asfsls",false));
		}
	}

	public abstract static class BaseFragmentSettingsActivity extends AppCompatActivity {
		SharedPreferences pref;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO: Implement this method
			if(pref.getBoolean("useBright",false)){
				setTheme(R.style.AppTheme_Bright);
				getTheme().applyStyle(R.style.AppTheme_Bright,true);
			}
			super.onCreate(savedInstanceState);
		}
		@Override
		protected void attachBaseContext(Context newBase) {
			super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
			pref=PreferenceManager.getDefaultSharedPreferences(this);
		}
		
		public abstract static class BaseFragment extends SHablePreferenceFragment {
			protected SharedPreferences pref;
			@Override
			public void onCreate(Bundle savedInstanceState) {
				// TODO: Implement this method
				pref=PreferenceManager.getDefaultSharedPreferences(getContext());
				super.onCreate(savedInstanceState);
			}
		}
	}
	public static class Basics extends BaseFragmentSettingsActivity {
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO: Implement this method
			super.onCreate(savedInstanceState);
			getSupportFragmentManager().beginTransaction().replace(android.R.id.content,new Content()).commit();
		}
		public static class Content extends BaseFragment {
			int which;
			@Override
			public void onCreatePreferences(Bundle p1, String p2) {
				// TODO: Implement this method
				addPreferencesFromResource(R.xml.settings_basic_compat);
				sH("serverListStyle", new HandledPreference.OnClickListener(){
						public void onClick(String a, String b, String c) {
							new AppCompatAlertDialog.Builder(getContext(),R.style.AppAlertDialog)
								.setTitle(R.string.serverListStyle)
								.setSingleChoiceItems(getResources().getStringArray(R.array.serverListStyles),pref.getInt("main_style",0),new DialogInterface.OnClickListener(){
									public void onClick(DialogInterface di,int w){
										which=w;
									}
								})
								.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener(){
									public void onClick(DialogInterface di,int w){
										pref.edit().putInt("main_style",which).commit();
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
		}
	}
	public static class Features extends BaseFragmentSettingsActivity {
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO: Implement this method
			super.onCreate(savedInstanceState);
			getSupportFragmentManager().beginTransaction().replace(android.R.id.content,new Content()).commit();
		}
		public static class Content extends BaseFragment {
			int which;
			@Override
			public void onCreatePreferences(Bundle p1, String p2) {
				// TODO: Implement this method
				addPreferencesFromResource(R.xml.settings_features_compat);
			}
		}
	}
	public static class Asfsls extends BaseFragmentSettingsActivity {
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO: Implement this method
			super.onCreate(savedInstanceState);
			if(!pref.getBoolean("feature_asfsls",false)){
				finish();
				return;
			}
			getSupportFragmentManager().beginTransaction().replace(android.R.id.content,new Content()).commit();
		}
		public static class Content extends BaseFragment {
			int which;
			@Override
			public void onCreatePreferences(Bundle p1, String p2) {
				// TODO: Implement this method
				addPreferencesFromResource(R.xml.settings_asfsls_compat);
				SharedPreferences slsVersCache=getContext().getSharedPreferences("sls_vers_cache", 0);
				findPreference("currentSlsVersion").setSummary(slsVersCache.getString("dat.vcode",getResources().getString(R.string.unknown)));
			}
		}
	}
}
