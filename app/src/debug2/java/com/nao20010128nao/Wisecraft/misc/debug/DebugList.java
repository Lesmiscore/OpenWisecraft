package com.nao20010128nao.Wisecraft.misc.debug;

import android.content.*;
import android.support.v7.app.*;
import android.support.v7.preference.*;
import android.os.*;
import com.annimon.stream.*;
import com.nao20010128nao.GroovyRoom.*;
import com.nao20010128nao.ToolBox.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.pref.*;
import com.nao20010128nao.Wisecraft.widget.*;
import java.util.*;

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
             Context c = Utils.wrapContextForPreference(getContext());
             PreferenceScreen preferences=getPreferenceManager().createPreferenceScreen(c);
             setPreferenceScreen(preferences);
             
             DebugBridge.getInstance().addDebugInfos(getContext(),preferences);
             
             List<Preference> components = new ArrayList<>();
             // components to add
             components.add(new SimplePref(c, "Groovy Availability (classes3.dex)", Debug2Utils.testGroovy()?"Yes":"No"));
             components.add(new HandledPreferenceCompat(c)
                 .title("LogCat")
                 .onClick(a->startActivity(new Intent(getContext(), LogCatActivity.class))));
             components.add(new HandledPreferenceCompat(c)
                 .title("Widget State Inspector")
                 .onClick(a->startActivity(new Intent(getContext(), WidgetStateInspector.class))));
             if(Debug2Utils.testGroovy()){
                 components.add(new HandledPreferenceCompat(c)
                     .title("Groovy Test Kit")
                     .onClick(a->startActivity(new Intent(getContext(), GroovyTestKit.class))));
             }
             Stream.of(components)
                 .peek(preferences::addPreference)
                 .forEach(a->a.setVisible(true));
         }
    }
}
