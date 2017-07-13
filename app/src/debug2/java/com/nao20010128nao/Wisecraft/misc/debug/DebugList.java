package com.nao20010128nao.Wisecraft.misc.debug;

import android.content.*;
import android.support.v7.app.*;
import android.support.v7.preference.*;
import android.os.*;
import android.widget.*;
import com.annimon.stream.*;
import com.nao20010128nao.GroovyRoom.LogCatActivity;
import com.nao20010128nao.ToolBox.HandledPreferenceCompat;
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
             Context c = Utils.wrapContextForPreference(getContext());
             PreferenceScreen preferences=getPreferenceManager().createPreferenceScreen(c);
             setPreferenceScreen(preferences);
             
             DebugBridge.getInstance().addDebugInfos(getContext(),preferences);
             
             List<Preference> components = new ArrayList<>();
             // components to add
             components.add(new SimplePref(c, "Groovy Availability (classes3.dex)", testGroovy()?"Yes":"No"));
             components.add(new HandledPreferenceCompat(c)
                 .title("LogCat")
                 .onClick(a->startActivity(new Intent(getContext(), LogCatActivity.class))));
             Stream.of(components)
                 .peek(preferences::addPreference)
                 .forEach(a->a.setVisible(true));
         }
         
         boolean testGroovy(){
             try{
                 Class.forName("groovy.lang.GroovyObject");
                 return true;
             }catch(Throwable e){
                 return false;
             }
         }
    }
}
