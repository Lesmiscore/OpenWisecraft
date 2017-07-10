package com.nao20010128nao.Wisecraft.misc.debug;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.*;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;
import com.annimon.stream.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.pref.*;
import java.util.*;

import static com.nao20010128nao.Wisecraft.misc.compat.BuildConfig.*;

public class DebugList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getSupportFragmentManager().beginTransaction()
            .replace(android.R.id.content,new InternalFragment())
            .commit();
        
        
    }
    
    @Override
    public void onBackPressed(){
        finish();
    }
    
    public static class InternalFragment extends ViewHolderCatchablePreferenceFragment {
         @Override
         public void onCreatePreferences(Bundle p1, String p2) {
             PreferenceScreen preferences=getPreferenceScreen();
             DebugBridge.getInstance().addDebugInfos(getContext(),preferences);
             
             Context c = Utils.wrapContextForPreference(getContext());
             List<Preference> components = new ArrayList<>();
             components.add(new SimplePref(c, "Build ID",    CI_BUILD_ID));
             components.add(new SimplePref(c, "Build Ref",   CI_BUILD_REF_NAME));
             components.add(new SimplePref(c, "Runner ID",   CI_RUNNER_ID));
             components.add(new SimplePref(c, "Build Stage", CI_BUILD_STAGE));
             components.add(new SimplePref(c, "Build Name",  CI_BUILD_NAME));
             Stream.of(components)
                 .peek(preferences::addPreference)
                 .forEach(a->a.setVisible(true));
         }
    }
}
